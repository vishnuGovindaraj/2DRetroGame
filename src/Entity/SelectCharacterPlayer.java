package Entity;

import Audio.AudioPlayer;
import TileMap.*;

import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;

import java.awt.*;
import java.awt.image.BufferedImage;

public class SelectCharacterPlayer extends MapObject {

	// player vars
	private int health;
	private int maxHealth;
	private int fire;
	private int maxFire;

	private boolean flinching;

	private long flinchTimer;

	// fireball
	private boolean firing;
	private int fireCost;
	private int fireBallDamage;
	private ArrayList<Projectile> projectiles;

	// scratch

	private boolean scratching;

	private int scratchDamage;
	private int scratchRange;

	// gliding
	private boolean gliding;

	// animations
	private ArrayList<BufferedImage[]> sprites;
	// associating the number of frames to a specific action
	private final int[] numFrames = { 2, 4, 1, 1, 4, 2, 5 };

	// animation actions
	private static final int IDLE = 0;
	private static final int WALKING = 1;
	private static final int JUMPING = 2;
	private static final int FALLING = 3;
	private static final int GLIDING = 4;
	private static final int FIREBALL = 5;
	private static final int SCRATCHING = 6;

	private HashMap<String, AudioPlayer> sfx;

	public SelectCharacterPlayer(TileMap tm, String s) {
		super(tm);
		width = 24;
		height = 24;

		cwidth = 10;
		cheight = 10;

		moveSpeed = 0.3;
		maxSpeed = 1.6;
		tempMaxSpeed = maxSpeed;
		stopSpeed = 0.4;
		fallSpeed = 0.15;
		maxFallSpeed = 4.0;
		jumpStart = -4.8;
		stopJumpSpeed = 0.3;

		facingRight = true;

		setHealth(maxHealth = 5);
		fire = maxFire = 2500;

		fireCost = 200;
		fireBallDamage = 5;

		projectiles = new ArrayList<Projectile>();
		scratchDamage = 8;
		scratchRange = 40;

		// load sprites
		try {
			BufferedImage spritesheet = ImageIO.read(getClass().getResourceAsStream(s));

			sprites = new ArrayList<BufferedImage[]>();

			for (int i = 0; i < 5; i++) {
				BufferedImage[] bi = new BufferedImage[numFrames[i]];

				for (int j = 0; j < numFrames[i]; j++) {
					if (i != SCRATCHING) {
						bi[j] = spritesheet.getSubimage(j * width, i * height, width, height);
					} else {
						bi[j] = spritesheet.getSubimage(j * width * 2, i * height, width * 2, height);
					}
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

		sfx.put("jump", new AudioPlayer("/SFX/jump.mp3"));
		sfx.put("scratch", new AudioPlayer("/SFX/scratch.mp3"));
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
		scratching = true;
	}

	public void setGliding(boolean b) {
		gliding = b;
	}

	public void checkCollision(ArrayList<Enemy> enemies) {
		for (Enemy e : enemies) {
			if (intersects(e)) {

				this.dx -= 1;
				this.dy -= 1;
				if (dx <= 0) {
					dx = 0;
				}
				if (dy <= 0) {
					dy = 0;
				}

				if (e.getLeft() && !e.getIntersecting()) {
					e.setLeft(false);
					e.setRight(true);
					e.setDx(0);
					e.setIntersecting(true);
				}

				if (e.getRight() && !e.getIntersecting()) {
					e.setLeft(true);
					e.setRight(false);
					e.setDx(0);
					e.setIntersecting(true);

				}
			} else {
				e.setIntersecting(false);
			}

		}
	}

	public void checkStudent(ArrayList<Enemy> enemies) {

		// loop through enemies
		for (int i = 0; i < enemies.size(); i++) {
			Enemy e = enemies.get(i);

			// check for collision
			if (intersects(e)) {
				maxSpeed = tempMaxSpeed / 2;

			} else {
				maxSpeed = tempMaxSpeed;
			}
		}

	}

	public void checkTeacher(ArrayList<Enemy> enemies) {
		for (int i = 0; i < enemies.size(); i++) {
			Enemy e = enemies.get(i);
			// check enemy collisions

			if (e.intersects(this)) {
			}
		}
	}

	public void hit(int damage) {
		if (flinching)
			return;
		setHealth(getHealth() - damage);
		if (getHealth() < 0)
			setHealth(0);
		flinching = true;
		flinchTimer = System.nanoTime();
	}

	// determine the players next position via keyboard input
	private void getNextPosition() {
		// right left movement
		if (right) {
			dx += moveSpeed;
			if (dx > maxSpeed) {
				dx = maxSpeed;
			}
		}
		if (left) {
			dx -= moveSpeed;
			if (dx < -maxSpeed) {
				dx = -maxSpeed;
			}
		}

		if (up) {
			dy -= moveSpeed;
			if (dy < -maxSpeed) {
				dy = -maxSpeed;
			}
		}

		if (down) {
			dy += moveSpeed;
			if (dy > maxSpeed) {
				dy = maxSpeed;
			}
		}

		if (!right && !left) {
			dx = 0;
		}
		if (right && left) {
			dx = 0;
			if (currentAction != IDLE) {
				currentAction = IDLE;
				animation.setFrames(sprites.get(IDLE));
				animation.setDelay(400);
			}
		}
		if (!up && !down) {
			dy = 0;
		}
		if (up && down) {
			dy = 0;
			if (currentAction != IDLE) {
				currentAction = IDLE;
				animation.setFrames(sprites.get(IDLE));
				animation.setDelay(400);
			}
		}

		// cant attack when you are moving, unless in the air
	}

	public void update() {

		// update
		getNextPosition();
		checkTileMapCollision();
		// System.out.println(xtemp + " " + ytemp);
		setPosition(xtemp, ytemp);

		if (left || right || up || down) {
			if (currentAction != WALKING) {
				currentAction = WALKING;
				animation.setFrames(sprites.get(WALKING));
				animation.setDelay(100);
			}
		}
		// check done flinching
		else if (flinching) {
			long elapsed = (System.nanoTime() - flinchTimer) / 1000000;
			if (elapsed > 1000) {
				flinching = false;
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

		// set directino

		if (right)
			facingRight = true;
		if (left)
			facingRight = false;
	}

	public void draw(Graphics2D g) {
		setMapPosition();

		// draw player
		if (flinching) {
			long elapsed = (System.nanoTime() - flinchTimer) / 1000000;
			if (elapsed / 100 % 2 == 0) {
				return;
			}
		}

		super.draw(g, 1);

	}

	private void setHealth(int health) {
		this.health = health;
	}

}
