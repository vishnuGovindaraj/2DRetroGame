package GameState;

import TileMap.Background;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Timer;
import java.util.TimerTask;

public class MenuState extends GameState {

	private Background bg;

	private int currentChoice = 0;
	private String pressStart = "PRESS START";
	private int delay = 1000;

	private Color titleColor;
	private Font titleFont;

	private Font font;

	private Timer flash;

	private boolean flashing;

	public MenuState(GameStateManager gsm) {

		this.gsm = gsm;

		try {
			bg = new Background("/Backgrounds/gradepoint.gif", 1);

			bg.setVector(0, 0);

			titleColor = new Color(128, 0, 0);

			font = new Font("Arial", Font.BOLD, 12);

			flash = new Timer();
			flash.scheduleAtFixedRate(new TimerTask() {
				public void run() {
					flashText();
				}
			}, 0, 750);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void init() {
	}

	public void update() {
		bg.update();
	}

	public void draw(Graphics2D g) {
		// draw bg
		bg.draw(g);

		g.setFont(titleFont);

		// draw menu options
		g.setFont(font);

		g.setColor(textColor());

		g.drawString(pressStart, 90, 140);

	}

	public void flashText() {
		flashing = !flashing;
	}

	public Color textColor() {
		if (flashing)
			return Color.WHITE;
		else
			return Color.BLACK;
	}

	public void keyPressed(int k) {
		if (k == KeyEvent.VK_ENTER) {
			gsm.resetGame();
			select();
		}
		// if F1 is pressed in the start screen, year 4 final exam level is
		// loaded after which game ends.
		if (k == KeyEvent.VK_F1) {

			gsm.setPlatformerGrades(100, 0);
			gsm.setPlatformerGrades(100, 1);
			gsm.setPlatformerGrades(100, 2);
			gsm.setPlatformerGrades(100, 3);
			gsm.setPlatformerGrades(100, 4);
			gsm.setPlatformerGrades(100, 5);
			gsm.setPlatformerGrades(100, 6);
			gsm.setPlatformerGrades(100, 7);
			gsm.setFinalGrades(100, 0);
			gsm.setFinalGrades(100, 1);
			gsm.setFinalGrades(100, 2);
			gsm.setFinalGrades(100, 3);
			gsm.setPlatformerCourse(gsm.getNumCourses() * 4);

			gsm.setState(GameStateManager.FINALSTATE1);
		}
	}

	private void select() {
		gsm.setState(GameStateManager.SELECTCHARACTER);

	}

	public void keyReleased(int k) {
	}

}
