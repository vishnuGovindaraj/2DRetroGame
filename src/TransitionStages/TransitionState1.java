package TransitionStages;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import Audio.AudioPlayer;
import Entity.*;
import Entity.Enemies.*;
import GameState.*;
import Main.GamePanel;
import TileMap.Background;
import TileMap.TileMap;

public class TransitionState1 extends GameState {

	// timers for splash screen and level
	private int transitionNumber;
	private int levelTimeLeft = 45;
	private int penalty = 0;
	private int splashScreenTimeLeft = 5;
	private Timer splashTimer;
	private Timer levelTimer;
	private Timer penaltyTimer;

	private int gradePenalty = 0;

	// delay for time decrementing is 1 second
	private int delay = 1000;
	private int initialDelay = 0;

	private int numStudents = 60;

	private boolean displaySplash = true;
	private boolean displayPenalty = false;

	private OverheadPlayer overheadPlayer;
	private ArrayList<Enemy> enemies;
	private ArrayList<Door> doors;

	private TileMap tileMap;
	private Background bg;
	private String character;

	private ArrayList<int[]> enemyObjects;

	private int transSpeed = 8;

	private AudioPlayer bgMusic;
	private Font font;
	private boolean openDoor;

	private int currentOpenDoor;

	public TransitionState1(GameStateManager gsm, String c, int n) {
		transitionNumber = n;
		this.gsm = gsm;
		character = c;
		init();

	}

	@Override
	public void init() {

		// initialize tilemap
		tileMap = new TileMap("/Maps/transitionmap2.txt", "/Tilesets/transfinal.gif");
		// get position of doors from the map
		enemyObjects = tileMap.getObjects();
		tileMap.setTween(1);
		tileMap.setPosition(0, 0);

		bg = new Background("/Backgrounds/grassbg2.gif", 0.5);

		doors = new ArrayList<Door>();
		overheadPlayer = new OverheadPlayer(tileMap, character);
		// get last position of player from previous transition level, and set
		// it as initial position
		Point initial = gsm.getTransPosition();
		overheadPlayer.setPosition(initial.x, initial.y);

		// generate and display doors and student obstacles
		populateObjects();

		// list of all the open doors, for every transition level
		int[] doorNumbers = new int[] { 108, 121, 102, 115, 120, 106, 118, 113 };

		// set current open door depending on which level player is on
		for (Door d : doors) {
			if (d.getDoorNumber() == doorNumbers[gsm.getPlatformerCourse() - 1]) {
				currentOpenDoor = d.getDoorNumber();
				d.setOpen(true);
			}
		}

		// create timer for displaying splash screen
		splashTimer = new Timer();
		splashTimer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				splashSCreenInterval();
			}
		}, initialDelay, delay);

		// font for displaying all text
		font = new Font("Arial", Font.PLAIN, 9);
	}

	// decrement time remaining for splash screen
	private boolean splashSCreenInterval() {
		if (splashScreenTimeLeft == 1) {
			splashTimer.cancel();
			// after splash screen is displayed, then start the timer for the
			// level.
			if (displaySplash == true) {
				displaySplash = false;
				startLevelTimer();
			}
			return false;
		}
		--splashScreenTimeLeft;
		return true;
	}

	// create the timer for displaying time left for level
	private void startLevelTimer() {
		levelTimer = new Timer();
		levelTimer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				levelInterval();
			}

		}, 0, delay);
	}

	private void startPenaltyTimer() {
		penaltyTimer = new Timer();
		penaltyTimer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				penaltyInterval();
			}

		}, 0, delay * 2);
	}

	// decrement time remaining for completing level
	private void levelInterval() {
		if (levelTimeLeft == 1) {
			displayPenalty = true;
			startPenaltyTimer();
			levelTimer.cancel();
		}
		--levelTimeLeft;
	}

	// increment penalty for every second after level has finished
	private void penaltyInterval() {
		if (penalty == 100) {
			penaltyTimer.cancel();
		}
		if (!openDoor) {
			++penalty;
		}

	}

	// populate the screen with doors and student obstacles
	private void populateObjects() {
		enemies = new ArrayList<Enemy>();

		int maxX = tileMap.getWidth() - tileMap.getTileSize() * 2;
		int minX = tileMap.getTileSize();

		int maxY = tileMap.getHeight() - tileMap.getTileSize() * 2;
		int minY = tileMap.getTileSize();

		numStudents = (int) (numStudents + 10 * gsm.getPlatformerCourse());
		for (int i = 0; i < numStudents; i++) {
			StudentObstacle s = new StudentObstacle(tileMap);
			int x = 0;
			int y = 0;
			boolean blocked = false;
			do {
				x = minX + (int) (Math.random() * ((maxX - minX)));
				y = minY + (int) (Math.random() * ((maxY - minY)));

				s.setPosition(x, y);

				blocked = ((tileMap.getType(y / 16, x / 16) == 1) || (tileMap.getType(y / 16 + 1, x / 16) == 1)
						|| (tileMap.getType(y / 16 - 1, x / 16) == 1) || (tileMap.getType(y / 16, x / 16 + 1) == 1)
						|| (tileMap.getType(y / 16, x / 16 - 1) == 1));

			} while (blocked);
			enemies.add(s);
		}

		// get the positions of the doors from the maps dynamically at runtime.
		for (int[] o : enemyObjects) {
			switch (o[0]) {
			case 55:
				Door dUp = new Door(tileMap, 10);
				dUp.setPosition(o[1] + tileMap.getTileSize() / 2, o[2] + tileMap.getTileSize() / 2);
				dUp.setDoorNumber(o[3]);
				dUp.setActualX(o[1] + tileMap.getTileSize() / 2);
				dUp.setActualY(o[2] + tileMap.getTileSize() / 2);
				doors.add(dUp);
				break;

			case 56:
				Door dRight = new Door(tileMap, 11);
				dRight.setPosition(o[1] + tileMap.getTileSize() / 2, o[2] + tileMap.getTileSize() / 2);
				dRight.setDoorNumber(o[3]);
				dRight.setActualX(o[1] + tileMap.getTileSize() / 2);
				dRight.setActualY(o[2] + tileMap.getTileSize() / 2);
				doors.add(dRight);
				break;

			case 57:
				Door dLeft = new Door(tileMap, 12);
				dLeft.setPosition(o[1] + tileMap.getTileSize() / 2, o[2] + tileMap.getTileSize() / 2);
				dLeft.setDoorNumber(o[3]);
				dLeft.setActualX(o[1] + tileMap.getTileSize() / 2);
				dLeft.setActualY(o[2] + tileMap.getTileSize() / 2);
				doors.add(dLeft);
				break;
			case 58:
				Door dDown = new Door(tileMap, 13);
				dDown.setPosition(o[1] + tileMap.getTileSize() / 2, o[2] + tileMap.getTileSize() / 2);
				dDown.setDoorNumber(o[3]);
				dDown.setActualX(o[1] + tileMap.getTileSize() / 2);
				dDown.setActualY(o[2] + tileMap.getTileSize() / 2);
				doors.add(dDown);
				break;

			}
		}
	}

	public void update() {

		for (Enemy e : enemies) {
			e.setMapPosition();
			e.update();
		}
		// check collisions between player and student obstacles
		overheadPlayer.checkCollision(enemies);
		overheadPlayer.update();

		// move map accordingly depending on player's position by panning.
		transitions();

		// trigger to go to the next state when the player is near the open door
		for (Door d : doors) {
			if (openDoor) {
				if (d.isOpen()) {
					d.isTriggered();
					d.update();

					if (d.getCurrentState()) {
						// stop the timer
						levelTimer.cancel();
						if (displayPenalty) {
							penaltyTimer.cancel();
						}

						// save players position when current transition level
						// is finished; it becomes starting point of the next
						// transition level;
						Point p = new Point(overheadPlayer.getx(), overheadPlayer.gety());
						gsm.saveTransPosition(p);
						gsm.setPenalty(penalty);
						gsm.setPlatformerCourse(gsm.getPlatformerCourse() + 1);
						// if all the platformer levels have been played for the
						// year, then move to final level
						if (((gsm.getPlatformerCourse() - 1) % gsm.getNumCourses() == 0)) {
							gsm.setState(GameStateManager.FINALSTATE1);
						}
						// move to platformer level if more courses need to be
						// played to complete the year
						else {
							gsm.setState(GameStateManager.PLATFORMERSTATE1);
						}
					}
				}
			}
			d.update();
		}
		// bgMusic.loopIfStopped();
	}

	// Move map to correct position when player crosses screen boundary
	private void transitions() {
		int mapX = overheadPlayer.getx() / GamePanel.WIDTH;
		int mapY = overheadPlayer.gety() / GamePanel.HEIGHT;

		// If player moves past boundary of screen, pan tile map
		if ((mapY) * (-GamePanel.HEIGHT) < tileMap.gety()) {
			tileMap.setPosition(tileMap.getx(), tileMap.gety() - transSpeed);
			updateDoors(0, -transSpeed);
		}
		if ((mapY) * (-GamePanel.HEIGHT) > tileMap.gety()) {
			tileMap.setPosition(tileMap.getx(), tileMap.gety() + transSpeed);
			updateDoors(0, transSpeed);
		}
		if ((mapX) * (-GamePanel.WIDTH) < tileMap.getx()) {
			tileMap.setPosition(tileMap.getx() - transSpeed, tileMap.gety());
			updateDoors(-transSpeed, 0);
		}
		if ((mapX) * (-GamePanel.WIDTH) > tileMap.getx()) {
			tileMap.setPosition(tileMap.getx() + transSpeed, tileMap.gety());
			updateDoors(transSpeed, 0);
		}
	}

	// when the map is moved, move the doors appropriately so they remain
	// stationary
	private void updateDoors(int xOffset, int yOffset) {
		for (Door d : doors) {
			d.setPosition(d.getx() + xOffset, d.gety() + yOffset);
		}
	}

	@Override
	public void draw(Graphics2D g) {

		// draw background
		bg.draw(g);
		// draw tile map
		tileMap.draw(g);
		// draw doors
		for (Door d : doors) {
			d.draw(g, 1);
		}
		// draw the player
		overheadPlayer.draw(g);
		// draw the student obstacles
		for (Enemy e : enemies) {
			e.draw(g, 1);
		}

		// get the current transform of graphics object, so it can be reverted
		// back after rotating the door numbers
		AffineTransform orig = g.getTransform();
		// check the orientation of the doors, and display its door numbers
		// appropriately
		for (Door d : doors) {
			if (!d.getTriggered()) {
				if (d.getOrientation() == 10) {
					g.drawString(String.valueOf(d.getDoorNumber()), d.getx() - 8, d.gety() + 5);
				}
				if (d.getOrientation() == 11) {
					g.rotate((Math.toRadians(90)), d.getx() - 5, d.gety() - 8);
					g.drawString(String.valueOf(d.getDoorNumber()), d.getx() - 5, d.gety() - 8);
					g.setTransform(orig);
				}

				if (d.getOrientation() == 12) {
					g.rotate((Math.toRadians(-90)), d.getx() + 5, d.gety() + 8);
					g.drawString(String.valueOf(d.getDoorNumber()), d.getx() + 5, d.gety() + 8);
					g.setTransform(orig);
				}
				if (d.getOrientation() == 13) {
					g.drawString(String.valueOf(d.getDoorNumber()), d.getx() - 8, d.gety() + 2);
				}
			}
		}

		// intro splash screen giving instructions to the player
		if (displaySplash) {
			g.setColor(Color.black);
			g.fillRect(3 * 16, 6 * 16 - 8, 160, 80);
			g.setColor(Color.white);
			g.drawRect(3 * 16, 6 * 16 - 8, 160, 80);
			g.setFont(font);
			g.drawString("Your next class is at Room: " + Integer.toString(currentOpenDoor), 4 * 16, 7 * 16);
			g.drawString("You have " + levelTimeLeft + " seconds!", 4 * 16, 8 * 16);
			g.drawString("Press 'w' to open the door.", 4 * 16, 9 * 16);
		}

		if (!displayPenalty) {
			// hud displaying destination room, and time left
			g.setFont(font);
			g.drawString("Rm:" + Integer.toString(currentOpenDoor), 0, 10);
			g.drawString("Time: " + levelTimeLeft, 13 * 16, 10);
		}
		// display penalty if level timer has finished
		else {
			g.drawString("Rm:" + Integer.toString(currentOpenDoor), 0, 10);
			g.setColor(Color.red);
			g.drawString("Penalty: " + penalty + "%", 12 * 16, 10);
			g.setColor(Color.white);
		}

	}

	@Override
	public void keyPressed(int k) {
		if (k == KeyEvent.VK_F1) {
			openDoor = true;
		}

		if (k == KeyEvent.VK_LEFT && !openDoor && !displaySplash) {
			overheadPlayer.setLeft(true);
		}

		if (k == KeyEvent.VK_RIGHT && !openDoor && !displaySplash) {
			overheadPlayer.setRight(true);
		}

		if (k == KeyEvent.VK_UP && !openDoor && !displaySplash) {
			overheadPlayer.setUp(true);
		}

		if (k == KeyEvent.VK_DOWN && !openDoor && !displaySplash) {
			overheadPlayer.setDown(true);
		}

		if (k == KeyEvent.VK_W) {
			// if w is pressed when splash screen is showing, then stop
			// displaying it, and start the level.
			if (displaySplash) {
				displaySplash = false;
				startLevelTimer();
			}

			// openDoor = false;
			for (Door d : doors) {
				if ((overheadPlayer.getx() < d.getActualX() + (tileMap.getTileSize() / 2) * 3)
						&& (overheadPlayer.getx() > d.getActualX() - (tileMap.getTileSize() / 2) * 3)
						&& (overheadPlayer.gety() < d.getActualY() + (tileMap.getTileSize() / 2) * 3)
						&& (overheadPlayer.gety() > d.getActualY() - (tileMap.getTileSize() / 2) * 3) && d.isOpen()) {
					openDoor = true;
				}
			}
		}

		if (k == KeyEvent.VK_ENTER) {
			if (displaySplash) {
				displaySplash = false;
				startLevelTimer();
			}

		}
	}

	@Override
	public void keyReleased(int k) {
		if (k == KeyEvent.VK_LEFT)
			overheadPlayer.setLeft(false);
		if (k == KeyEvent.VK_RIGHT)
			overheadPlayer.setRight(false);
		if (k == KeyEvent.VK_UP)
			overheadPlayer.setUp(false);
		if (k == KeyEvent.VK_DOWN)
			overheadPlayer.setDown(false);
		if (k == KeyEvent.VK_W) {

		}
	}

}
