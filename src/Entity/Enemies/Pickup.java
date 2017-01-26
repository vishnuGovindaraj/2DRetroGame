package Entity.Enemies;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import TileMap.TileMap;
import Entity.*;

public class Pickup extends Enemy {

	private BufferedImage[] sprites;

	public Pickup(TileMap tm) {
		super(tm);

		width = 16;
		height = 16;
		cwidth = 16;
		cheight = 16;

		setPickUp(true);

		try {
			BufferedImage spritesheet = ImageIO.read(getClass().getResourceAsStream("/Sprites/Enemies/pickup.gif"));

			sprites = new BufferedImage[1];
			for (int i = 0; i < sprites.length; i++) {
				sprites[i] = spritesheet.getSubimage(i * width, 0, width, height);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		animation = new Animation();
		animation.setFrames(sprites);
		animation.setDelay(300);

		right = true;
		facingRight = true;
	}

	public void update() {

		checkTileMapCollision();
		setPosition(xtemp, ytemp);
		// update animation
		animation.update();

	}

	public void draw(Graphics2D g) {
		setMapPosition();
		super.draw(g, 1);
	}
}
