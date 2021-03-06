package Entity.Enemies;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import TileMap.TileMap;
import Entity.*;

public class Exam extends Enemy {

	private int actualX;
	private int actualY;

	private boolean open = false;
	private ArrayList<BufferedImage[]> sprites;

	private final int[] numFrames = { 1, 1 };

	private boolean triggered = false;
	private boolean cantReadAnymore = true;

	// animation actions
	private static final int VISIBLE = 0;
	private static final int NOTVISIBLE = 1;

	private static int delay = 500;

	public Exam(TileMap tm) {
		super(tm);

		width = 16;
		height = 16;

		cwidth = 20;
		cheight = 20;

		damage = 0;
		// load sprites

		try {
			BufferedImage spritesheet = ImageIO.read(getClass().getResourceAsStream("/Sprites/Enemies/exam.gif"));

			sprites = new ArrayList<BufferedImage[]>();

			for (int i = 0; i < 2; i++) {
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
		currentAction = VISIBLE;
		animation.setFrames(sprites.get(VISIBLE));
		animation.setDelay(delay);

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
			if (currentAction != NOTVISIBLE) {
				currentAction = NOTVISIBLE;
				animation.setFrames(sprites.get(NOTVISIBLE));
				animation.setDelay(delay);
				cantReadAnymore = false;
			}

		}

	}

	public boolean getReadAgain() {
		return cantReadAnymore;
	}

	public boolean getPlayedOnce() {
		return animation.hasPlayedOnce();

	}

	public void draw(Graphics2D g) {
		setMapPosition();
		super.draw(g, 1);
	}
}
