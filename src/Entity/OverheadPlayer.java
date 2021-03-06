package Entity;

import Audio.AudioPlayer;
import TileMap.*;

import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;

import java.awt.*;
import java.awt.image.BufferedImage;

public class OverheadPlayer extends MapObject {

	private String character;
	private ArrayList<BufferedImage[]> sprites;
	// associating the number of frames to a specific action
	private final int[] numFrames = { 1, 4, 1, 4, 1, 4, 1, 4 };

	private static final int IDLEDOWN = 0;
	private static final int WALKINGDOWN = 1;
	private static final int IDLEUP = 2;
	private static final int WALKINGUP = 3;

	private static final int IDLERIGHT = 4;
	private static final int WALKINGRIGHT = 5;
	private static final int IDLELEFT = 6;
	private static final int WALKINGLEFT = 7;

	private HashMap<String, AudioPlayer> sfx;

	public OverheadPlayer(TileMap tm, String c) {
		super(tm);
		width = 16;
		height = 16;
		character = c;

		cwidth = 5;
		cheight = 5;

		moveSpeed = 0.2;
		maxSpeed = 1.6;
		tempMaxSpeed = maxSpeed;
		stopSpeed = 0.4;

		if (character == "/Sprites/Player/artplayermale.gif") {
			character = "/Sprites/Player/artoverheadmale.gif";
		} else if (character == "/Sprites/Player/artplayerfemale.gif") {
			character = "/Sprites/Player/artoverheadfemale.gif";
		}

		else if (character == "/Sprites/Player/cpscplayerfemale.gif") {
			character = "/Sprites/Player/cpscoverheadfemale.gif";
		} else if (character == "/Sprites/Player/cpscplayermale.gif") {
			character = "/Sprites/Player/cpscoverheadmale.gif";
		}

		else if (character == "/Sprites/Player/kineplayerfemale.gif") {
			character = "/Sprites/Player/kineoverheadfemale.gif";
		} else if (character == "/Sprites/Player/kineplayermale.gif") {
			character = "/Sprites/Player/kineoverheadmale.gif";
		}

		facingRight = true;

		// load sprites
		try {
			BufferedImage spritesheet = ImageIO.read(getClass().getResourceAsStream(character));

			sprites = new ArrayList<BufferedImage[]>();
			for (int i = 0; i < 8; i++) {
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
		currentAction = 0;
		animation.setFrames(sprites.get(currentAction));
		animation.setDelay(100);

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

	private void getNextPosition() {

		if (right) {
			dx += moveSpeed;
			if (dx > maxSpeed) {
				dx = maxSpeed;
			}
			if (currentAction != WALKINGLEFT) {
				currentAction = WALKINGLEFT;
				animation.setFrames(sprites.get(currentAction));
				animation.setDelay(100);
			}
			facingRight = false;
		}

		else if (left) {
			dx -= moveSpeed;
			if (dx < -maxSpeed) {
				dx = -maxSpeed;
			}
			if (currentAction != WALKINGRIGHT) {
				currentAction = WALKINGRIGHT;
				animation.setFrames(sprites.get(currentAction));
				animation.setDelay(100);
			}

		}

		if (up) {
			dy -= moveSpeed;
			if (dy < -maxSpeed) {
				dy = -maxSpeed;
			}
			if (currentAction != WALKINGUP) {
				currentAction = WALKINGUP;
				animation.setFrames(sprites.get(currentAction));
				animation.setDelay(100);
			}
			facingDown = false;
		} else if (down) {
			dy += moveSpeed;
			if (dy > maxSpeed) {
				dy = maxSpeed;
			}
			if (currentAction != WALKINGDOWN) {
				currentAction = WALKINGDOWN;
				animation.setFrames(sprites.get(currentAction));
				animation.setDelay(100);
			}
			facingDown = true;
		}

		if (!right && !left) {
			dx = 0;
		}
		if (right && left) {
			dx = 0;
			if (currentAction != IDLERIGHT) {
				currentAction = IDLERIGHT;
				animation.setFrames(sprites.get(currentAction));
				animation.setDelay(100);
			}
		}

		if (!up && !down) {
			dy = 0;
		}
		if (up && down) {
			dy = 0;
			if (currentAction != IDLEDOWN) {
				currentAction = IDLEDOWN;
				animation.setFrames(sprites.get(currentAction));
				animation.setDelay(100);
			}
		}

		if (dx == 0 && dy == 0) {
			if (down) {
				currentAction = IDLEDOWN;
			}
			if (up) {
				currentAction = IDLEUP;
			}
			if (left) {
				currentAction = IDLELEFT;
			}
			if (right) {
				currentAction = IDLERIGHT;
			}
			animation.setFrames(sprites.get(currentAction));
			animation.setDelay(100);

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

	public void update() {

		// update
		getNextPosition();
		checkTileMapCollision();
		setPosition(xtemp, ytemp);
		// check done flinching
		animation.update();

	}

	public void draw(Graphics2D g) {
		setMapPosition();
		super.draw(g, 1);
	}

}
