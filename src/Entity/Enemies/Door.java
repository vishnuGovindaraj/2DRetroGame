package Entity.Enemies;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import TileMap.TileMap;
import Entity.*;

public class Door extends Enemy {

	private int doorNumber = 0;
	private int actualX;
	private int actualY;

	private boolean open = false;
	private boolean isOpenState = false;
	private ArrayList<BufferedImage[]> sprites;

	private final int[] numFrames = { 1, 4, 1, 4, 1, 4, 1, 4 };

	private boolean triggered = false;

	// animation actions
	private static final int CLOSEDUP = 0;
	private static final int OPENUP = 1;
	private static final int CLOSEDRIGHT = 2;
	private static final int OPENRIGHT = 3;

	private static final int CLOSEDLEFT = 4;
	private static final int OPENLEFT = 5;
	private static final int CLOSEDDOWN = 6;
	private static final int OPENDOWN = 7;

	private static final int UP = 10;
	private static final int RIGHT = 11;
	private static final int LEFT = 12;
	private static final int DOWN = 13;

	private int orientation;
	private static int delay = 500;

	public Door(TileMap tm, int o) {
		super(tm);

		orientation = o;
		width = 16;
		height = 16;

		cwidth = 20;
		cheight = 20;

		damage = 0;
		// load sprites

		try {
			BufferedImage spritesheet = ImageIO.read(getClass().getResourceAsStream("/Sprites/Enemies/door.gif"));

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
		if (orientation == UP) {

			animation = new Animation();
			currentAction = CLOSEDUP;
			animation.setFrames(sprites.get(OPENUP));
			animation.setDelay(-1);

		}

		if (orientation == RIGHT) {
			animation = new Animation();
			currentAction = CLOSEDLEFT;
			animation.setFrames(sprites.get(OPENLEFT));
			animation.setDelay(-1);
		}

		if (orientation == LEFT) {
			animation = new Animation();
			currentAction = CLOSEDRIGHT;
			animation.setFrames(sprites.get(OPENRIGHT));
			animation.setDelay(-1);
		}

		if (orientation == DOWN) {
			animation = new Animation();
			currentAction = CLOSEDDOWN;
			animation.setFrames(sprites.get(OPENDOWN));
			animation.setDelay(-1);
		}

	}

	public void setDoorNumber(int d) {
		doorNumber = d;
	}

	public int getDoorNumber() {
		return doorNumber;
	}

	public boolean isOpen() {
		return open;
	}

	public void setOpen(boolean s) {
		open = s;
	}

	public int getActualX() {
		return actualX;
	}

	public int getActualY() {
		return actualY;
	}

	public void setActualX(int x) {
		actualX = x;
	}

	public void setActualY(int y) {
		actualY = y;
	}

	public void isTriggered() {
		triggered = true;
	}

	public boolean getTriggered() {
		return triggered;
	}

	public void update() {
		checkTrigger();
		animation.update();
	}

	private void checkTrigger() {

		if (triggered) {
			if (currentAction != OPENUP && orientation == UP) {
				System.out.println(triggered + " " + orientation);
				animation.setDelay(delay);
				if (animation.hasPlayedOnce()) {
					animation.setLastFrame();
					currentAction = OPENUP;
					isOpenState = true;
				}
			}

			else if (currentAction != OPENLEFT && orientation == RIGHT) {
				animation.setDelay(delay);
				if (animation.hasPlayedOnce()) {
					animation.setLastFrame();
					currentAction = OPENLEFT;
					isOpenState = true;
				}

			} else if (currentAction != OPENRIGHT && orientation == LEFT) {
				animation.setDelay(delay);
				if (animation.hasPlayedOnce()) {
					animation.setLastFrame();
					currentAction = OPENRIGHT;
					isOpenState = true;
				}
			} else if (currentAction != OPENDOWN && orientation == DOWN) {
				animation.setDelay(delay);
				if (animation.hasPlayedOnce()) {
					animation.setLastFrame();
					currentAction = OPENDOWN;
					isOpenState = true;
				}
			}

		}

	}

	public int getOrientation() {
		return orientation;
	}

	public boolean getPlayedOnce() {
		return animation.hasPlayedOnce();

	}

	public boolean getCurrentState() {
		return isOpenState;
	}

	public void draw(Graphics2D g) {
		setMapPosition();
		super.draw(g, 1);
	}
}
