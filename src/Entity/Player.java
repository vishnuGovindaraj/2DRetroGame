package Entity;

import Audio.AudioPlayer;
import Entity.Enemies.Teacher;
import TileMap.*;

import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Player extends MapObject {

	// player stats
	private int health;
	private int maxHealth;
	private int fire;
	private int maxFire;
	private int currentPlayer;

	private int score;
	private int grade;
	private int year;
	private int course;

	private boolean flashing;
	private boolean hitOnce;
	private boolean hitPickUp;

	private long flashTimer;

	// projectiles
	private boolean firing;
	private int projectileDamage;
	private ArrayList<Projectile> projectiles;

	private boolean attacking;

	private int attackDamage;
	private int attackRange;

	// animations
	private ArrayList<BufferedImage[]> sprites;
	// associating the number of frames to a specific action
	private final int[] numFrames = { 2, 4, 1, 1, 3, };

	private int[][] attributes;

	// animation actions
	private static final int IDLE = 0;
	private static final int WALKING = 1;
	private static final int JUMPING = 2;
	private static final int FALLING = 3;

	private int ATTACK = 4;

	private HashMap<String, AudioPlayer> sfx;
	private boolean platformer = true;

	public Player(TileMap tm, String character, int[][] attributes) {
		super(tm);
		this.attributes = attributes;

		if (character.contains("cpsc")) {
			currentPlayer = 0;
		}
		if (character.contains("kine")) {
			currentPlayer = 1;
		}
		if (character.contains("art")) {
			currentPlayer = 2;
		}

		// maxSpeed = attributes[currentPlayer][0] + 1;
		maxSpeed = 1.6 + 0.4 * attributes[currentPlayer][0];
		attackDamage = 1 * attributes[currentPlayer][1];
		projectileDamage = 1 * attributes[currentPlayer][1];
		jumpStart = -attributes[currentPlayer][2] - 4.2;

		System.out.println(maxSpeed + " " + jumpStart + " " + attackDamage);

		width = 24;
		height = 24;

		cwidth = 16;
		cheight = 16;

		moveSpeed = 1;

		stopSpeed = 0.8;
		fallSpeed = 0.25;

		maxFallSpeed = 4;
		stopJumpSpeed = 1;
		facingRight = true;

		setHealth(maxHealth = 5);

		projectiles = new ArrayList<Projectile>();
		attackRange = (int) (width * 2);
		// load sprites
		try {
			BufferedImage spritesheet = ImageIO.read(getClass().getResourceAsStream(character));

			sprites = new ArrayList<BufferedImage[]>();

			// make bufferedimage arrays for each set of animations
			for (int i = 0; i < 5; i++) {
				BufferedImage[] bi = new BufferedImage[numFrames[i]];

				for (int j = 0; j < numFrames[i]; j++) {
					bi[j] = spritesheet.getSubimage(j * width, i * height, width, height);
				}

				sprites.add(bi);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		animation = new Animation();
		currentAction = IDLE;
		animation.setFrames(sprites.get(IDLE));
		animation.setDelay(400);

		sfx = new HashMap<String, AudioPlayer>();

		sfx.put("pickup", new AudioPlayer("/SFX/pickup.mp3"));
		sfx.put("jump", new AudioPlayer("/SFX/jump.mp3"));
		sfx.put("attack", new AudioPlayer("/SFX/attack.mp3"));
		sfx.put("hitEnemy", new AudioPlayer("/SFX/enemyDeath.mp3"));
	}

	public int getHealth() {
		return health;
	}

	public int getMaxHealth() {
		return maxHealth;
	}

	public int getFire() {
		return fire;
	}

	public int getMaxFire() {
		return maxFire;
	}

	public void setFiring() {
		firing = true;

	}

	public void setScratching() {
		if (currentPlayer < 2)
			attacking = true;
	}

	public void checkAttack(ArrayList<Enemy> enemies) {

		// loop through enemies
		for (int i = 0; i < enemies.size(); i++) {
			Enemy e = enemies.get(i);
			// if cpsc or kine player uses their attack check if the enemy is
			// within range
			if (attacking && currentPlayer < 2) {
				if (facingRight) {
					if (e.getx() > x - cwidth && e.getx() < x + attackRange && e.gety() > y - height
							&& e.gety() < y + height) {
						if (!e.isPickUp) {
							e.hit(attackDamage);
						}
					}
					// facing Left
				} else {
					if (e.getx() < x + cwidth && e.getx() > x - attackRange && e.gety() < y + height
							&& e.gety() > y - height) {
						if (!e.isPickUp) {
							e.hit(attackDamage);
						}
					}
				}

			}

			// projectile hits enemy
			for (int j = 0; j < projectiles.size(); j++) {

				if (projectiles.get(j).intersects(e)) {
					sfx.get("hitEnemy").play();
					e.hit(projectileDamage);
					projectiles.get(j).setHit();
					break;
				}
			}

			// check enemy collisions
			if (intersects(e)) {
				if (!e.isPickUp && !attacking) {
					hit();
				} else if (!e.isPickUp && attacking) {

				} else {
					sfx.get("pickup").play();
					setHitPickUp(true);
					setHitOnce(true);
					e.setDead(true);
				}

			}
		}

	}

	public void hit() {
		if (isFlashing()) {
			return;
		}
		setHitPickUp(false);
		setFlashing(true);
		setHitOnce(true);
		flashTimer = System.nanoTime();
	}

	// determine the players next position via keyboard input
	private void getNextPosition() {
		// movement

		if (left) {
			dx -= moveSpeed;
			if (dx < -maxSpeed) {
				dx = -maxSpeed;
			}
		}

		else if (right) {
			dx += moveSpeed;
			if (dx > maxSpeed) {
				dx = maxSpeed;
			}
		} else {
			if (dx > 0) {
				dx -= stopSpeed;
				if (dx < 0) {
					dx = 0;
				}
			} else if (dx < 0) {
				dx += stopSpeed;
				if (dx > 0) {
					dx = 0;
				}
			}

		}

		// if attacking then make the player stop moving, unless jumping
		if (jumping && !falling) {
			sfx.get("jump").play();
			dy = jumpStart;
			falling = true;
		}

		// falling
		if (falling) {
			dy += fallSpeed;
			if (dy > 0)
				jumping = false;
			if (dy < 0 && !jumping)
				dy += stopJumpSpeed;
			if (dy > maxFallSpeed)
				dy = maxFallSpeed;
		}
	}

	public void update() {

		// update
		getNextPosition();
		checkTileMapCollision();
		setPosition(xtemp, ytemp);

		// stop attack after 1 attack
		if (currentAction == ATTACK && (currentPlayer == 0 || currentPlayer == 1)) {
			if (animation.hasPlayedOnce())
				attacking = false;

		}
		// stop projectile after 1 projectile has been drawn
		if (currentAction == ATTACK && currentPlayer == 2) {
			if (animation.hasPlayedOnce())
				firing = false;

		}

		// create the projectile
		if (firing && currentAction != ATTACK && (currentPlayer == 2)) {

			Projectile fb = new Projectile(tileMap, facingRight);
			fb.setPosition(x, y);
			projectiles.add(fb);
			attacking = false;

		}
		// update projectiles
		for (int i = 0; i < projectiles.size(); i++) {
			projectiles.get(i).update();
			if (projectiles.get(i).shouldRemove()) {
				projectiles.remove(i);
				i--;
			}
		}
		// check done flashing
		if (isFlashing()) {
			long elapsed = (System.nanoTime() - flashTimer) / 1000000;
			if (elapsed > 1000) {
				setFlashing(false);
			}
		}

		// if the player uses physical attack then show animation
		if (attacking && (currentPlayer == 0 || currentPlayer == 1)) {
			sfx.get("attack").play();
			if (currentAction != ATTACK) {
				currentAction = ATTACK;
				animation.setFrames(sprites.get(ATTACK));
				animation.setDelay(60);

			}
		}
		// if the player used a projectile, show attack animation
		else if (firing && currentPlayer == 2) {
			if (currentAction != ATTACK) {
				currentAction = ATTACK;
				animation.setFrames(sprites.get(ATTACK));
				animation.setDelay(100);

			}
		}

		else if (dy > 0) {

			if (currentAction != FALLING) {
				currentAction = FALLING;
				animation.setFrames(sprites.get(FALLING));
				animation.setDelay(100);

			}

		}

		else if (dy < 0) {
			if (currentAction != JUMPING) {
				currentAction = JUMPING;
				animation.setFrames(sprites.get(JUMPING));
				animation.setDelay(-1);

			}
		} else if (left || right) {
			if (currentAction != WALKING) {
				currentAction = WALKING;
				animation.setFrames(sprites.get(WALKING));
				animation.setDelay(100);

			}
		}

		else {
			if (currentAction != IDLE) {
				currentAction = IDLE;
				animation.setFrames(sprites.get(IDLE));
				animation.setDelay(400);
			}
		}
		animation.update();

		// set direction

		if (currentAction != ATTACK)
			;
		{
			if (right)
				facingRight = true;
			if (left)
				facingRight = false;
		}

		if (getHealth() <= 0) {
			GameOver();
		}

	}

	private void GameOver() {
		// TODO Auto-generated method stub

	}

	public void draw(Graphics2D g) {
		setMapPosition();

		for (int i = 0; i < projectiles.size(); i++) {
			projectiles.get(i).draw(g);
		}
		// draw player
		if (isFlashing()) {
			long elapsed = (System.nanoTime() - flashTimer) / 1000000;
			if (elapsed / 100 % 2 == 0) {
				return;
			}
		}

		super.draw(g, 1);

	}

	private void setHealth(int health) {
		this.health = health;
	}

	public int getScore() {
		return score;
	}

	public int getGrade() {
		return grade;
	}

	public int getYear() {
		return year;
	}

	public int getCourse() {
		return course;
	}

	public void setScore(int s) {
		score = s;
	}

	public void setGrade(int g) {
		grade = g;
	}

	public void setYear(int y) {
		year = y;
	}

	public void setCourse(int c) {
		course = c;
	}

	public void gradeDec(int amount) {
		grade -= amount;
	}

	public boolean isFlashing() {
		return flashing;
	}

	public void setFlashing(boolean flashing) {
		this.flashing = flashing;
	}

	public boolean isHitOnce() {
		return hitOnce;
	}

	public void setHitOnce(boolean hitOnce) {
		this.hitOnce = hitOnce;
	}

	public boolean isHitPickUp() {
		return hitPickUp;
	}

	public void setHitPickUp(boolean hitPickUp) {
		this.hitPickUp = hitPickUp;
	}

}
