package FinalStages;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import Audio.AudioPlayer;
import Entity.*;
import Entity.Enemies.*;
import GameState.*;
import Main.GamePanel;
import TileMap.Background;
import TileMap.TileMap;

public class FinalState1 extends GameState {

	private int splashTimeLeft = 5;
	private int levelTimeLeft = 45;

	private Timer levelTimer;
	private Timer splashTimer;

	private boolean displaySplash = true;
	private boolean levelFinished;
	private boolean skipReportCard;

	private int delay = 1000;

	private OverheadPlayer overheadPlayer;
	private int examsRead = 0;
	private int numTeachers = 5;
	private int numPoints = 5;
	private int totalExams;
	private int detectionPercentage = 0;
	private int detectionRate = 1;

	private int platformer;
	private int courseNumber;
	private int currentYear;

	private ArrayList<Exam> exams;
	private ArrayList<Enemy> teachers;
	private ArrayList<int[]> enemyObjects;
	private Font font;
	private Font gpaFont;
	private Font reportFont;
	private Font finalReportFont;

	private TileMap tileMap;
	private Background bg;

	private BufferedImage img;
	private String character;

	int x;
	int y;
	int xDelta = 3;
	int yDelta = 2;

	private AudioPlayer bgMusic;
	private boolean displayReportCard;
	private Font titleFont;
	private boolean intersecting;

	public FinalState1(GameStateManager gsm, String c, int n) {
		this.gsm = gsm;
		character = c;
		init();
	}

	@Override
	public void init() {

		tileMap = new TileMap("/Maps/finalmap.txt", "/Tilesets/transfinal.gif");
		enemyObjects = tileMap.getObjects();
		overheadPlayer = new OverheadPlayer(tileMap, character);
		overheadPlayer.setPosition(tileMap.getTileSize() * 4, tileMap.getTileSize() * 27);

		tileMap.setPosition(GamePanel.WIDTH / 2 - overheadPlayer.getx(), GamePanel.HEIGHT / 2 - overheadPlayer.gety());

		tileMap.setTween(1);

		bg = new Background("/Backgrounds/bluebg.gif", 0.5);

		platformer = gsm.getPlatformerCourse();
		courseNumber = gsm.getNumCourses();
		currentYear = platformer / courseNumber;

		exams = new ArrayList<Exam>();

		populateEnemies();

		splashTimer = new Timer();
		splashTimer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				splashInterval();
			}

		}, 0, delay);

		font = new Font("Arial", Font.PLAIN, 10);
		titleFont = new Font("Arial", Font.BOLD, 22);
		gpaFont = new Font("Arial", Font.PLAIN, 18);
		reportFont = new Font("Arial", Font.PLAIN, 14);
		finalReportFont = new Font("Arial", Font.PLAIN, 10);

	}

	private void levelInterval() {
		if (levelTimeLeft == 1) {
			levelTimer.cancel();
		}
		--levelTimeLeft;

	}

	private void startLevelTimer() {
		levelTimer = new Timer();
		levelTimer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				levelInterval();
			}

		}, 0, delay);
	}

	private boolean splashInterval() {
		if (splashTimeLeft == 1) {
			splashTimer.cancel();
			if (displaySplash == true) {
				displaySplash = false;
				startLevelTimer();
			}

			return false;
		}
		--splashTimeLeft;
		return true;
	}

	private void populateEnemies() {
		teachers = new ArrayList<Enemy>();
		Teacher t = null;

		numTeachers = 5 + currentYear * 2;

		// create teachers
		for (int i = 0; i < numTeachers; i++) {
			t = new Teacher(tileMap);
			teachers.add(t);
			int initX = (((4 * (int) (Math.random() * (30 / 4)) + 1) * tileMap.getTileSize()
					+ tileMap.getTileSize() / 2));
			int initY = (32 + 8);

			t.setPosition(initX, initY);
			Point pI = new Point(initX, initY);
			t.addPoint(pI);

			for (int j = 0; j < numPoints; j++) {

				// random point generation (tiles)
				int randX = (int) (Math.random() * (30 / 4));
				int randY = (int) (Math.random() * (28 / 3));
				// get absolute position
				int newX = (((4 * randX) + 1) * tileMap.getTileSize()) + tileMap.getTileSize() / 2;
				int newY = (((3 * randY) + 3) * tileMap.getTileSize()) + tileMap.getTileSize() / 2;

				Point p = new Point(newX, newY);

				t.addPoint(p);
			}
		}

		// get the exams positions from the tilemap, and set their positions,
		// and add them to the exams list
		for (int[] o : enemyObjects) {
			switch (o[0]) {
			case 61:
				Exam e = new Exam(tileMap);
				e.setPosition(o[1] + tileMap.getTileSize() / 2, o[2] + tileMap.getTileSize() / 2);
				e.setActualX(o[1] + tileMap.getTileSize() / 2);
				e.setActualY(o[2] + tileMap.getTileSize() / 2);
				exams.add(e);
				totalExams += 1;
				break;
			}

		}

	}

	@Override
	public void update() {
		// update player
		overheadPlayer.update();
		tileMap.setPosition(GamePanel.WIDTH / 2 - overheadPlayer.getx(), GamePanel.HEIGHT / 2 - overheadPlayer.gety());

		// set background
		bg.setPosition(tileMap.getx(), tileMap.gety());
		// attack enemies
		overheadPlayer.checkTeacher(teachers);

		// loopsound if it reached end

		for (Enemy e : teachers) {
			if (e.getIntersecting()) {
				intersecting = true;
				detectionPercentage += detectionRate;
			} else {
				intersecting = false;
			}
		}

		if (examsRead == totalExams || levelTimeLeft <= 0 || detectionPercentage >= 100 || levelFinished) {
			// if final level is beat display report card
			levelFinished = true;
			// when played finishes reading the report card
			if (skipReportCard) {
				// go to menu if all levels have been played
				if (gsm.getPlatformerCourse() - 1 == gsm.getNumCourses() * gsm.getNumYears()) {
					gsm.setState(GameStateManager.MENUSTATE);
				}
				// else go to next year
				else {
					gsm.setPlatformerCourse(gsm.getPlatformerCourse());
					gsm.setState(GameStateManager.PLATFORMERSTATE1);
				}
			}
		}

		for (Enemy t : teachers) {
			t.update();
		}

		for (Exam e : exams) {
			e.setPosition(e.getActualX() + tileMap.getx(), e.getActualY() + tileMap.gety());
			e.update();
		}
	}

	private void triggerNextState(Exam e) {

		if ((overheadPlayer.getx() < e.getActualX() + (tileMap.getTileSize() / 2) * 3)
				&& (overheadPlayer.getx() > e.getActualX() - (tileMap.getTileSize() / 2) * 3)
				&& (overheadPlayer.gety() < e.getActualY() + (tileMap.getTileSize() / 2) * 3)
				&& (overheadPlayer.gety() > e.getActualY() - (tileMap.getTileSize() / 2) * 3)) {

			e.isTriggered();
			examsRead += 1;
			e.update();

		}
	}

	@Override
	public void draw(Graphics2D g) {

		bg.draw(g);
		// draw tilemap
		tileMap.draw(g);
		for (Exam e : exams) {
			e.draw(g, 1);
		}

		overheadPlayer.draw(g);

		for (Enemy t : teachers) {
			t.draw(g, 1);
		}
		g.setColor(Color.black);
		g.fillRect(0, 16 * 240, 256, 16);

		// display splash screen in the beginning of the level, set on a timer
		if (displaySplash) {
			g.setColor(Color.black);
			g.fillRect(3 * 16, 6 * 16 - 8, 160, 80);
			g.setColor(Color.white);
			g.drawRect(3 * 16, 6 * 16 - 8, 160, 80);
			g.setFont(font);
			g.drawString("Year " + currentYear + " final Exam!", 4 * 16, 7 * 16);
			g.drawString("Press 'w' to copy exams", 4 * 16, 8 * 16);
			g.drawString("Don't get caught cheating!", 4 * 16, 9 * 16);

		}

		g.setColor(Color.white);
		g.setFont(font);
		g.drawString("Exams: " + Integer.toString(examsRead) + "/" + totalExams, 0, 10);
		g.drawString("Time: " + Integer.toString(levelTimeLeft), 10 * 10, 10);
		if (intersecting) {
			g.setColor(Color.red);
		} else {
			g.setColor(Color.white);
		}
		g.drawString("Detection: " + Integer.toString(detectionPercentage) + "%", 12 * 16 - 8, 10);

		// Show report card
		if (levelFinished) {

			if (gsm.getPlatformerCourse() < gsm.getNumCourses() * gsm.getNumYears()) {

				g.setColor(Color.black);
				g.fillRect(0, 0, 256, 240);
				g.setColor(Color.white);

				g.setFont(titleFont);
				g.drawString("Report Card", 70, 20);
				g.setFont(reportFont);
				g.drawString("Year  " + currentYear, 110, 40);

				gsm.setFinalGrades((int) (((double) examsRead / (double) totalExams) * 100), currentYear - 1);

				int i;
				int j = 0;
				for (i = currentYear * gsm.getNumCourses() - (gsm.getNumCourses()); i < currentYear
						* gsm.getNumCourses(); i++) {
					g.drawString("Course " + (i + 1) + " :    " + gsm.getPlatformerGrades(i) + "%", 70, 80 + j * 14);
					j++;
				}

				g.drawString("Final " + " :    " + gsm.getFinalGrades(currentYear - 1) + "%", 93, 80 + j * 14);

				DecimalFormat df = new DecimalFormat("#.##");
				g.setFont(gpaFont);
				g.drawString("GPA : " + String.valueOf(df.format(gsm.getGPA(platformer))), 91, 80 + j * 14 + 30);
				g.setFont(font);
				g.drawString("Press ' Enter ' to continue", 70, 230);

			} else {

				// show final report card
				g.setColor(Color.black);
				g.fillRect(0, 0, 256, 240);
				g.setColor(Color.white);

				g.setFont(titleFont);
				g.drawString("Final Report Card", 38, 20);
				g.setFont(finalReportFont);

				gsm.setFinalGrades((int) (((double) examsRead / (double) totalExams) * 100), currentYear - 1);

				int year = 1;
				g.drawString("Year  " + year, 36, 46);

				int i;
				int j = 0;
				for (i = year * gsm.getNumCourses() - (gsm.getNumCourses()); i < year * gsm.getNumCourses(); i++) {
					g.drawString("Course " + (i + 1) + " :    " + gsm.getPlatformerGrades(i) + "%", 12, 60 + j * 10);
					j++;
				}
				g.drawString("Final " + " :    " + gsm.getFinalGrades(year - 1) + "%", 26, 60 + j * 10);

				DecimalFormat df = new DecimalFormat("#.##");
				g.drawString("GPA :    " + String.valueOf(df.format(gsm.getGPA(year * gsm.getNumCourses()))), 31,
						60 + j * 10 + 10);

				year = 2;
				g.drawString("Year  " + year, 164, 46);

				j = 0;
				for (i = year * gsm.getNumCourses() - (gsm.getNumCourses()); i < year * gsm.getNumCourses(); i++) {
					g.drawString("Course " + (i + 1) + " :    " + gsm.getPlatformerGrades(i) + "%", 142, 60 + j * 10);
					j++;
				}
				g.drawString("Final " + " :    " + gsm.getFinalGrades(year - 1) + "%", 156, 60 + j * 10);
				g.drawString("GPA :    " + String.valueOf(df.format(gsm.getGPA(year * gsm.getNumCourses()))), 161,
						60 + j * 10 + 10);

				year = 3;
				g.drawString("Year  " + year, 36, 126);

				j = 0;
				for (i = year * gsm.getNumCourses() - (gsm.getNumCourses()); i < year * gsm.getNumCourses(); i++) {
					g.drawString("Course " + (i + 1) + " :    " + gsm.getPlatformerGrades(i) + "%", 12, 140 + j * 10);
					j++;
				}
				g.drawString("Final " + " :    " + gsm.getFinalGrades(year - 1) + "%", 26, 140 + j * 10);
				g.drawString("GPA :    " + String.valueOf(df.format(gsm.getGPA(year * gsm.getNumCourses()))), 31,
						140 + j * 10 + 10);

				year = 4;
				g.drawString("Year  " + year, 164, 126);

				j = 0;
				for (i = year * gsm.getNumCourses() - (gsm.getNumCourses()); i < year * gsm.getNumCourses(); i++) {
					g.drawString("Course " + (i + 1) + " :    " + gsm.getPlatformerGrades(i) + "%", 142, 140 + j * 10);
					j++;
				}
				g.drawString("Final " + " :    " + gsm.getFinalGrades(year - 1) + "%", 156, 140 + j * 10);
				g.drawString("GPA :    " + String.valueOf(df.format(gsm.getGPA(year * gsm.getNumCourses()))), 161,
						140 + j * 10 + 10);

				g.setFont(gpaFont);
				g.drawString(
						"Final GPA : " + String.valueOf(df.format(gsm.getGPA(gsm.getNumYears() * gsm.getNumCourses()))),
						70, 210);

				g.setFont(font);
				g.drawString("Press ' Enter ' to continue", 70, 230);
			}
		}

	}

	public void printReportCard() {

	}

	@Override
	public void keyPressed(int k) {
		if (k == KeyEvent.VK_LEFT && !displaySplash) {
			overheadPlayer.setLeft(true);
		}

		if (k == KeyEvent.VK_RIGHT && !displaySplash) {
			overheadPlayer.setRight(true);
		}

		if (k == KeyEvent.VK_UP && !displaySplash) {
			overheadPlayer.setUp(true);
		}

		if (k == KeyEvent.VK_DOWN && !displaySplash) {
			overheadPlayer.setDown(true);
		}
		// trigger for reading the exams
		if (k == KeyEvent.VK_W) {
			for (Exam e : exams) {
				if (e.getReadAgain())
					triggerNextState(e);
			}
			if (displaySplash) {
				displaySplash = false;
				startLevelTimer();
			}
		}

		// F1 to skip level
		if (k == KeyEvent.VK_F1)
			levelFinished = true;

		if (k == KeyEvent.VK_ENTER) {
			if (levelFinished) {

				// If all levels complete, go back to menu
				if (gsm.getPlatformerCourse() >= gsm.getNumCourses() * gsm.getNumYears()) {
					gsm.setState(0);
				} else {
					skipReportCard = true;
					// levelTimer.cancel();
				}
			}
			if (displaySplash) {
				displaySplash = false;
				startLevelTimer();
			}
		}
	}

	@Override
	public void keyReleased(int k) {
		// when key is released set the current direction of the player to false
		if (k == KeyEvent.VK_LEFT)
			overheadPlayer.setLeft(false);
		if (k == KeyEvent.VK_RIGHT)
			overheadPlayer.setRight(false);
		if (k == KeyEvent.VK_UP)
			overheadPlayer.setUp(false);
		if (k == KeyEvent.VK_DOWN)
			overheadPlayer.setDown(false);

	}

}