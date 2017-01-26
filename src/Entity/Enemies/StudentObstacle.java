package Entity.Enemies;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;

import Entity.Animation;
import Entity.Enemy;
import TileMap.TileMap;

public class StudentObstacle extends Enemy {
	private ArrayList<BufferedImage[]> sprites;

	private static final int IDLEDOWN = 0;
	private static final int WALKINGDOWN = 1;
	private static final int IDLEUP = 2;
	private static final int WALKINGUP = 3;

	private static final int IDLERIGHT = 4;
	private static final int WALKINGRIGHT = 5;
	private static final int IDLELEFT = 6;
	private static final int WALKINGLEFT = 7;

	private static final int UP = 10;
	private static final int RIGHT = 11;
	private static final int LEFT = 12;
	private static final int DOWN = 13;

	private final int[] numFrames = { 1, 4, 1, 4, 1, 4, 1, 4 };

	public StudentObstacle(TileMap tm) {
		super(tm);

		moveSpeed = 0.3;
		maxSpeed = 0.3;

		width = 16;
		height = 16;
		cwidth = 20;
		cheight = 20;

		damage = 0;
		// load sprites

		try {
			BufferedImage spritesheet = ImageIO
					.read(getClass().getResourceAsStream("/Sprites/Enemies/studentObstacle2.gif"));

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
		int[] walkingDirection = { 1, 3, 5, 7 };

		animation = new Animation();
		currentAction = getRandom(walkingDirection);
		animation.setFrames(sprites.get(currentAction));
		animation.setDelay(100);

		randomizeDirection();

	}

	private void randomizeDirection() {

	}

	private int getRandom(int[] array) {
		int rnd = new Random().nextInt(array.length);
		switch (rnd) {
		case 0: {
			up = true;
			right = false;
			down = false;
			left = false;
			break;
		}

		case 1: {
			up = false;
			right = false;
			down = true;
			left = false;
			break;
		}

		case 2: {
			up = false;
			right = true;
			down = false;
			left = false;
			break;
		}

		case 3: {
			up = false;
			right = false;
			down = false;
			left = true;
			break;
		}

		}
		return array[rnd];
	}

	private void getNextPosition() {

		if (left) {
			dx -= moveSpeed;
			if (dx < maxSpeed) {
				dx = maxSpeed;
			}
			if (currentAction != WALKINGLEFT) {
				currentAction = WALKINGLEFT;
				animation.setFrames(sprites.get(currentAction));
				animation.setDelay(100);
			}
		}

		else if (right) {
			dx += moveSpeed;
			if (dx > -maxSpeed) {
				dx = -maxSpeed;
			}
			if (currentAction != WALKINGRIGHT) {
				currentAction = WALKINGRIGHT;
				animation.setFrames(sprites.get(currentAction));
				animation.setDelay(100);
			}
			facingRight = false;
		}

		if (down) {
			dy += moveSpeed;
			if (dy > -maxSpeed) {
				dy = -maxSpeed;
			}
			if (currentAction != WALKINGUP) {
				currentAction = WALKINGUP;
				animation.setFrames(sprites.get(currentAction));
				animation.setDelay(100);
			}
		} else if (up) {
			dy -= moveSpeed;
			if (dy < maxSpeed) {
				dy = maxSpeed;
			}
			if (currentAction != WALKINGDOWN) {
				currentAction = WALKINGDOWN;
				animation.setFrames(sprites.get(currentAction));
				animation.setDelay(100);
			}
		}

	}

	public void update() {
		getNextPosition();
		checkTileMapCollision();
		setPosition(xtemp, ytemp);

		// check flinching
		if (flinching) {
			long elapsed = (System.nanoTime() - flinchTimer) / 1000000;
			if (elapsed > 400) {
				flinching = false;
			}
		}

		// reverse direction if hit wall

		if (right && dx == 0) {
			right = false;
			left = true;
			facingRight = false;
		} else if (left && dx == 0) {
			left = false;
			right = true;
			facingRight = true;
		}

		else if (up && dy == 0) {
			up = false;
			down = true;
			facingDown = true;
		}

		else if (down && dy == 0) {
			up = true;
			down = false;
			facingDown = true;
		}

		// update animation
		animation.update();
	}

	public void draw(Graphics2D g) {
		setMapPosition();
		super.draw(g, 1);
	}
}
