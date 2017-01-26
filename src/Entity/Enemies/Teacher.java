package Entity.Enemies;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import TileMap.TileMap;
import Entity.*;

public class Teacher extends Enemy {

	private ArrayList<BufferedImage[]> sprites;

	private double angle = 0;
	double tempAngle = 0;
	private int[] xPoints;
	private int[] yPoints;
	private int nPoints;
	private ArrayList<Rectangle> rects;
	private ArrayList<Point> points;
	private int pointIndex;

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

	private boolean collisionDet;

	public Teacher(TileMap tm) {
		super(tm);

		points = new ArrayList<Point>();

		pointIndex = 0;
		moveSpeed = 0.7;
		maxSpeed = 0.7;
		fallSpeed = 0;
		maxFallSpeed = 0;

		width = 16;
		height = 16;
		cwidth = 1;
		cheight = 1;

		xPoints = new int[5];
		yPoints = new int[5];
		rects = new ArrayList<Rectangle>();

		health = maxHealth = 2;
		damage = 1;
		// load sprites

		try {
			BufferedImage spritesheet = ImageIO
					.read(getClass().getResourceAsStream("/Sprites/Enemies/studentObstacle1.gif"));

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
		currentAction = 1;
		animation.setFrames(sprites.get(currentAction));
		animation.setDelay(100);

		right = true;
		facingRight = true;
	}

	private void getNextPosition() {
		if (left) {
			if (currentAction != WALKINGLEFT) {
				currentAction = WALKINGLEFT;
				animation.setFrames(sprites.get(currentAction));
				animation.setDelay(100);
			}
		} else if (right) {
			if (currentAction != WALKINGRIGHT) {
				currentAction = WALKINGRIGHT;
				animation.setFrames(sprites.get(currentAction));
				animation.setDelay(100);
			}
		} else if (up) {
			if (currentAction != WALKINGUP) {
				currentAction = WALKINGUP;
				animation.setFrames(sprites.get(currentAction));
				animation.setDelay(100);
			}
		} else if (down) {
			if (currentAction != WALKINGDOWN) {
				currentAction = WALKINGDOWN;
				animation.setFrames(sprites.get(currentAction));
				animation.setDelay(100);
			}
		}

	}

	public void addPoint(Point p) {
		points.add(p);
	}

	public Point getPoint(int i) {
		return points.get(i);
	}

	public void update() {
		getNextPosition();
		checkTileMapCollision();
		setPosition(xtemp, ytemp);

		double deltaX;
		double deltaY;
		double newAngle = angle;
		double angleIncrement = (Math.PI / 124);

		deltaX = getPoint(pointIndex % points.size()).x - this.x;
		deltaY = getPoint(pointIndex % points.size()).y - this.y;

		if (Math.abs(deltaX) > 2) {

			if (deltaX > 0) {
				newAngle = 0;
				right = true;
				left = false;
				up = false;
				down = false;
			} else {
				newAngle = Math.PI;
				right = false;
				left = true;
				up = false;
				down = false;

			}

		} else {

			// if point reached
			if (Math.abs(deltaY) < 2 && Math.abs(deltaX) < 2) {
				pointIndex++;
			}

			if (deltaY < 0) {
				newAngle = 1.5 * Math.PI;
				right = false;
				left = false;
				up = true;
				down = false;
			} else {
				newAngle = (Math.PI) / 2;
				up = false;
				down = true;
				right = false;
				left = false;

			}
		}

		if (angle < newAngle - Math.PI / 512) {
			tempAngle += angleIncrement;
			angle = tempAngle;
			dy = 0;
			dx = 0;
		} else if (angle > newAngle + Math.PI / 512) {
			tempAngle -= angleIncrement;
			angle = tempAngle;
			dy = 0;
			dx = 0;
		}

		else {

			if (right) {
				dy = 0;
				dx += moveSpeed;
				if (dx > maxSpeed) {
					dx = maxSpeed;
				}

			}
			if (left) {
				dy = 0;
				dx -= moveSpeed;
				if (dx < -maxSpeed) {
					dx = -maxSpeed;
				}

			}

			if (down) {

				dx = 0;
				dy += moveSpeed;
				if (dy > maxSpeed) {
					dy = maxSpeed;
				}
			}
			if (up) {
				dx = 0;
				dy -= moveSpeed;
				if (dy < -maxSpeed) {
					dy = -maxSpeed;
				}
			}

		}

		// update animation
		animation.update();

	}

	public boolean intersects(MapObject o) {
		Polygon p1 = new Polygon(xPoints, yPoints, nPoints);
		// player collision rectangle
		Rectangle r2 = new Rectangle(o.getx() + (int) xmap - o.getCWidth() / 2,
				o.gety() + (int) ymap - o.getCHeight() / 2, o.getCWidth(), o.getCHeight());
		rects.add(r2);

		if (p1.intersects(r2)) {
			this.setIntersecting(true);
			collisionDet = true;
			return true;
		} else {
			this.setIntersecting(false);
			collisionDet = false;
			return false;
		}
	}

	public void draw(Graphics2D g, int s) {
		// Draw vision triangle

		int length = 103;
		double angleOffset = 0.3;

		int bodySize = 16;

		setMapPosition();

		// create vision polygon points
		xPoints[0] = (int) (x + xmap - (width / 2) + (Math.cos(angle) * 2.5)) + bodySize / 2;

		xPoints[1] = (int) (x + xmap - (width / 2) + (Math.cos(angle - angleOffset) * length));
		xPoints[2] = (int) (x + xmap - (width / 2) + (Math.cos(angle - (angleOffset / 2)) * length));
		xPoints[3] = (int) (x + xmap - (width / 2) + (Math.cos(angle + (angleOffset / 2)) * length));
		xPoints[4] = (int) (x + xmap - (width / 2) + (Math.cos(angle + angleOffset) * length));

		yPoints[0] = (int) (y + ymap - (height / 2) + (Math.sin(angle) * 2.5)) + bodySize / 2;

		yPoints[1] = (int) (y + ymap + (Math.sin(angle - angleOffset) * length));
		yPoints[2] = (int) (y + ymap + (Math.sin(angle - (angleOffset / 2)) * length));
		yPoints[3] = (int) (y + ymap + (Math.sin(angle + (angleOffset / 2)) * length));
		yPoints[4] = (int) (y + ymap + (Math.sin(angle + angleOffset) * length));
		nPoints = 5;

		super.draw(g, 1);
		if (!collisionDet) {
			g.setColor(Color.YELLOW);
		} else {
			g.setColor(Color.RED);
		}
		g.fillPolygon(xPoints, yPoints, nPoints);

		g.setColor(Color.BLUE);

	}
}