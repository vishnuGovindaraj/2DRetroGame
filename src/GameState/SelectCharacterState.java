package GameState;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import Entity.*;
import Entity.Enemies.*;
import GameState.*;
import Main.GamePanel;
import TileMap.*;
import Audio.AudioPlayer;

import javax.imageio.ImageIO;

import Entity.Animation;
import TileMap.Background;

public class SelectCharacterState extends GameState {

	private ArrayList<SelectCharacterPlayer> p;
	private AudioPlayer bgMusic;

	private TileMap tilemap;

	private Background bg;
	private int currentChoice = 0;
	private String[] courses = { "CPSC", "KINESIOLOGY", "ART" };
	private String[] skills = { "Speed", "Attack", "Jump" };
	private String[] instructions = { "Use arrow keys to select", "character. Press Enter ", "to confirm selection." };

	private Color titleColor;
	private Font instructionFont;
	private Font regularFont;

	private String character;

	private int numBars = 6;
	private int selection = 0;
	private boolean gender = true;

	private ArrayList<BufferedImage[]> sprites;
	// associating the number of frames to a specific action

	private final String[] characterPath = { "/Sprites/Player/cpscplayermale.gif", "/Sprites/Player/kineplayermale.gif",
			"/Sprites/Player/artplayermale.gif", "/Sprites/Player/cpscplayerfemale.gif",
			"/Sprites/Player/kineplayerfemale.gif", "/Sprites/Player/artplayerfemale.gif",

	};

	private static int currentCourse = 0;

	// character attributes: Speed, Attack, Jump
	private final int[][] Attributes = { { 3, 1, 2 }, { 1, 2, 3 }, { 2, 3, 1 } };

	public SelectCharacterState(GameStateManager gsm) {

		this.gsm = gsm;
		init();
	}

	@Override
	public void init() {

		tilemap = new TileMap("/Maps/selectCharacter.txt", "/Tilesets/falltileset.gif");

		tilemap.setPosition(0, 0);
		tilemap.setTween(1);

		bg = new Background("/Backgrounds/grey2.gif", 0.5);

		p = new ArrayList<SelectCharacterPlayer>();
		for (int i = 0; i < characterPath.length; i++) {
			SelectCharacterPlayer player = new SelectCharacterPlayer(tilemap, characterPath[i]);
			p.add(player);
			// walk animation
			p.get(i).setRight(true);
			// stay in same spot
			p.get(i).stayStill();

		}
		instructionFont = new Font("Arial", Font.PLAIN, 10);
		regularFont = new Font("Arial", Font.PLAIN, 12);

		bgMusic = new AudioPlayer("/Music/selectCharacter.mp3");
		bgMusic.play();

		// sets the 6 playable characters on the screen
		drawPlayers(35, 45, 80, 70);
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		for (SelectCharacterPlayer player : p) {
			player.update();
		}

		bg.setPosition(tilemap.getx(), tilemap.gety());
		bgMusic.loopIfStopped();
	}

	@Override
	public void draw(Graphics2D g) {
		// TODO Auto-generated method stub
		bg.draw(g);
		tilemap.draw(g);

		g.setFont(instructionFont);
		drawInstructions(g, 120, 180, 15);

		g.setFont(regularFont);
		drawSkills(g, 20, 180, 15);

		g.setColor(Color.yellow);
		drawSelectionBar(g, 20, 20, 50, 50, 3, 20);

		g.setColor(Color.white);
		g.drawString(courses[0], 30, 20);
		g.drawString(courses[1], 90, 20);
		g.drawString(courses[2], 200, 20);

		drawSkillBars(g, 60, 171, 7, 12, 2);

		for (int i = 0; i < p.size(); i++) {
			p.get(i).draw(g, 2);
		}

	}

	private void drawInstructions(Graphics2D g, int x, int y, int yOffset) {
		g.setColor(Color.white);
		for (int i = 0; i < instructions.length; i++) {
			g.drawString(instructions[i], x, y + yOffset * i);
		}

	}

	// draw the skills to the screen
	private void drawSkills(Graphics2D g, int x, int y, int stringOffset) {
		for (int j = 0; j < skills.length; j++) {
			g.drawString(skills[j], x, y + stringOffset * j);
		}

	}

	// draw players to the screen
	private void drawPlayers(int x, int y, int xOffset, int yOffset) {
		for (int i = 0; i < 6; i++) {
			if (i < 3) {
				p.get(i).setPosition(x + i * xOffset, y);
			} else {
				p.get(i).setPosition(x + (i - 3) * xOffset, y + yOffset);
			}
		}

	}

	// draw the borders of the players
	private void drawSelectionBar(Graphics2D g, int x, int y, int width, int height, int xOffset, int yOffset) {
		g.setColor(Color.white);
		int var = 10;
		if (gender) {
			g.drawRect((p.get(currentCourse).getx() - (p.get(currentCourse).getWidth() / 2)) - var / 2,
					p.get(currentCourse).gety() - (p.get(currentCourse).getHeight() / 2) - var / 2,
					(p.get(currentCourse).getWidth() * 2) + var, (p.get(currentCourse).getHeight() * 2) + var);
		}
		if (!gender) {
			g.drawRect((p.get(currentCourse + 3).getx() - (p.get(currentCourse + 3).getWidth() / 2)) - var / 2,
					p.get(currentCourse + 3).gety() - (p.get(currentCourse + 3).getHeight() / 2) - var / 2,
					(p.get(currentCourse + 3).getWidth() * 2) + var, (p.get(currentCourse + 3).getHeight() * 2) + var);
		}
	}

	// draw the skill bars corresponding to each skill
	private void drawSkillBars(Graphics2D g, int x, int y, int width, int height, int yOffset) {
		// draw filled bars
		g.setColor(Color.green);
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < Attributes[currentCourse][i] * 2; j++) {
				g.fillRect(x + width * j, y + i * height + i * yOffset, width, height);
			}
		}

		// draw outline of the bars
		g.setColor(Color.black);
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < numBars; j++) {
				g.drawRect(x + width * j, y + i * height + i * yOffset, width, height);
			}
		}
	}

	@Override
	public void keyPressed(int k) {
		if (k == KeyEvent.VK_LEFT) {
			currentCourse--;
			if (currentCourse == -1) {
				currentCourse = courses.length - 1;
			}
		}

		if (k == KeyEvent.VK_RIGHT) {
			currentCourse++;
			if (currentCourse == courses.length) {
				currentCourse = 0;
			}
		}
		if (k == KeyEvent.VK_UP) {
			if (gender) {
				gender = false;
			}

			else {
				gender = true;
			}
		}

		if (k == KeyEvent.VK_DOWN) {
			if (gender) {
				gender = false;
			} else {
				gender = true;
			}
		}
		if (k == KeyEvent.VK_ENTER) {
			System.out.println("gender " + gender + "character: " + currentCourse);
			if (gender) {
				character = characterPath[currentCourse];
			} else {
				character = characterPath[currentCourse + 3];
			}
			bgMusic.close();

			gsm.setCharacter(character);
			gsm.setCharacterAttributes(Attributes);
			gsm.setState(GameStateManager.PLATFORMERSTATE1);
		}

	}

	@Override
	public void keyReleased(int k) {
		// TODO Auto-generated method stub

	}

}
