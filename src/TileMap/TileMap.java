package TileMap;

import java.awt.*;
import java.awt.Image.*;
import java.awt.image.BufferedImage;

import java.io.*;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import Entity.Enemy;
import Entity.Explosion;
import Main.GamePanel;

public class TileMap {

	// position
	private double x;
	private double y;

	// bounds
	private int xmin;
	private int ymin;
	private int xmax;
	private int ymax;

	private double tween;

	private int[][] map;
	private int[][] enemies;

	private ArrayList<int[]> enemyObjects;

	private int tileSize;
	private int numRows;
	private int numCols;
	private int width;
	private int height;

	// tileset
	private BufferedImage tileset;
	private BufferedImage objects;
	private int numTilesAcross;
	private Tile[][] tiles;

	// drawing
	private int rowOffset;
	private int colOffset;
	private int numRowsToDraw;
	private int numColsToDraw;

	public TileMap(String tileMap, String tileSet) {
		loadMap(tileMap);
		loadTiles(tileSet);
		numRowsToDraw = GamePanel.HEIGHT / tileSize + 2;
		numColsToDraw = GamePanel.WIDTH / tileSize + 2;
		tween = 0.07;

	}

	public void loadTiles(String s) {

		try {
			tileset = ImageIO.read(getClass().getResourceAsStream(s));
			numTilesAcross = tileset.getWidth() / tileSize;
			tiles = new Tile[2][numTilesAcross];

			BufferedImage subimage;
			for (int col = 0; col < numTilesAcross; col++) {

				subimage = tileset.getSubimage(col * tileSize, 0, tileSize, tileSize);
				tiles[0][col] = new Tile(subimage, Tile.NORMAL);
				subimage = tileset.getSubimage(col * tileSize, tileSize, tileSize, tileSize);
				tiles[1][col] = new Tile(subimage, Tile.BLOCKED);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void loadMap(String s) {

		// first line num cols, second is num rows

		try {
			InputStream in = getClass().getResourceAsStream(s);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));

			String delimsequal = "=";
			if (br.readLine().contains("header")) {
				for (int row = 0; row < 4; row++) {
					String line1 = br.readLine();
					String[] tokens1 = line1.split(delimsequal);
					if (tokens1[0].contentEquals("width")) {
						numCols = Integer.parseInt(tokens1[1]);
					}
					if (tokens1[0].contentEquals("height")) {
						numRows = Integer.parseInt(tokens1[1]);
					}
					if (tokens1[0].contentEquals("tileheight")) {
						tileSize = Integer.parseInt(tokens1[1]);
					}

				}
			}

			map = new int[numRows][numCols];
			enemies = new int[numRows][numCols];

			width = numCols * tileSize;
			height = numRows * tileSize;

			xmin = GamePanel.WIDTH - width;
			xmax = 0;
			ymin = GamePanel.HEIGHT - height;
			ymax = 0;
			// skip irrelevant data
			String skipString = br.readLine();

			while (!skipString.contains("data")) {
				skipString = br.readLine();
			}

			readFromMapToArray(map, br);

			skipString = br.readLine();
			while (!skipString.contains("data")) {
				skipString = br.readLine();
			}

			readFromMapToArray(enemies, br);

		}

		catch (Exception e) {
			e.printStackTrace();
		}

		enemyObjects = new ArrayList<int[]>();

		for (int i = 0; i < numRows; i++) {
			for (int j = 0; j < numCols; j++) {
				if (enemies[i][j] == 0 || enemies[i][j] >= 100)
					continue;
				else {
					int object[] = new int[4];
					object[0] = enemies[i][j];
					object[1] = j * tileSize;
					object[2] = i * tileSize;
					object[3] = enemies[i][j + 1];
					enemyObjects.add(object);
				}
			}
		}

	}

	private void readFromMapToArray(int[][] map, BufferedReader br) throws IOException {
		try {
			String delims2 = ",";
			for (int row = 0; row < numRows; row++) {
				String line = br.readLine();
				String[] tokens = line.split(delims2);
				for (int col = 0; col < numCols; col++) {
					map[row][col] = Integer.parseInt(tokens[col]);
					if (map[row][col] != 0) {
						map[row][col] = map[row][col] - 1;
					}
				}
			}
		} catch (NumberFormatException e) {

		}
	}

	public int getTileSize() {
		return tileSize;
	}

	public int getx() {
		return (int) x;
	}

	public int gety() {
		return (int) y;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getType(int row, int col) {
		int rc = map[row][col];
		int r = rc / numTilesAcross;
		int c = rc % numTilesAcross;
		return tiles[r][c].getType();

	}

	public void setTween(double d) {
		tween = d;
	}

	public void setPosition(double x, double y) {
		this.x += (x - this.x) * tween;
		this.y += (y - this.y) * tween;

		fixBounds();

		colOffset = (int) -this.x / tileSize;
		rowOffset = (int) -this.y / tileSize;

	}

	private void fixBounds() {

		if (x < xmin)
			x = xmin;
		if (y < ymin)
			y = ymin;
		if (x > xmax)
			x = xmax;
		if (y > ymax)
			y = ymax;
	}

	public ArrayList<int[]> getObjects() {
		return enemyObjects;
	}

	public void draw(Graphics2D g) {

		for (int row = rowOffset; row < rowOffset + numRowsToDraw; row++) {

			if (row >= numRows)
				break;
			for (int col = colOffset; col < colOffset + numColsToDraw; col++) {
				if (col >= numCols)
					break;

				int rc = map[row][col];
				int r = rc / numTilesAcross;
				int c = rc % numTilesAcross;
				g.drawImage(tiles[r][c].getImage(), (int) x + col * tileSize, (int) y + row * tileSize, null);
			}
		}
	}

}
