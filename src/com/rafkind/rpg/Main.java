package com.rafkind.rpg;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.CatmullRomSpline;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class Main extends ApplicationAdapter {
	SpriteBatch batch;
	World world;

	public Main(){
	}

	@Override
	public void create () {
		batch = new SpriteBatch();
		world = World.generate(100, 100);
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		world.render(batch);
		batch.end();
	}

	public static class DesktopLauncher {
		public static void main(String... args) {
			LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
			config.title = "RPG Battle";
			config.width = 1024;
			config.height = 768;
			new LwjglApplication(new Main(), config);
		}
	}
}

class World{
	public World(Tile[][] tiles){
		this.tiles = tiles;
		forest = new Texture("data/land/forest.png");
		ocean = new Texture("data/land/ocean.png");
		beach = new Texture("data/land/beach.png");
	}

	private Texture forest;
	private Texture ocean;
	private Texture beach;
	private Tile[][] tiles;

	public void render(SpriteBatch batch){
		int sprite_width = forest.getWidth();
		int sprite_height = forest.getHeight();
		// Affine2 scale = new Affine2().preScale(0.1f, 0.1f);
		// Affine2 scale = new Affine2();
		double scale = 0.2;
		for (int x = 0; x < tiles.length; x++){
			for (int y = 0; y < tiles[x].length; y++){
				/*
				int position_x = sprite_width * x + 2 * x;
				int position_y = sprite_height * y + 2 * y;
				*/
				int position_x = sprite_width * x;
				int position_y = sprite_height * y;

				Tile tile = tiles[x][y];
				switch (tile.getType()){
					case Forest: {
						// batch.draw(new TextureRegion(forest), position_x, position_y, scale);
						batch.draw(forest, (float)(position_x * scale), (float)(position_y * scale), (float)(forest.getWidth() * scale), (float)(forest.getHeight() * scale));
						break;
					}
					case Beach: {
						batch.draw(beach, (float)(position_x * scale), (float)(position_y * scale), (float)(forest.getWidth() * scale), (float)(forest.getHeight() * scale));
						break;
					}
					case Ocean: {
						batch.draw(ocean, (float)(position_x * scale), (float)(position_y * scale), (float)(ocean.getWidth() * scale), (float)(ocean.getHeight() * scale));
						break;
					}
				}
			}
		}
	}

	/* Set a blotch of tiles to be land */
	/*
	private static void applyLand2(Tile[][] tiles, int x, int y, int diameter){
		for (int cx = x - diameter / 2; cx < x + diameter / 2; cx++){
			for (int cy = y - diameter / 2; cy < y + diameter / 2; cy++){
				if (cx >= 0 && cx < tiles.length && cy >= 0 && cy < tiles[cx].length){
					tiles[cx][cy] = new Tile(1);
				}
			}
		}
	}
	*/

	/* Generates a spline with random control points */
	private static Vector2[] makeSpline(int width, int height){
		Vector2[] points = new Vector2[6];
		for (int i = 0; i < points.length; i++){
			points[i] = new Vector2(randomInteger(0, width), randomInteger(0, height));
		}

		CatmullRomSpline<Vector2> catmull = new CatmullRomSpline<>(points, false);
		Vector2[] spline = new Vector2[50];
		for (int i = 0; i < spline.length; i++){
			spline[i] = new Vector2();
			catmull.valueAt(spline[i], ((float)i) / ((float) spline.length - 1));
		}

		return spline;
	}

	private static void blotch(Tile[][] tiles, int x, int y){
		int size = 1;
		for (int ax = x - size; ax < x + size; ax++){
			for (int ay = y - size; ay < y + size; ay++){
				if (ax >= 0 && ax < tiles.length && ay >= 0 && ay < tiles[ax].length){
					tiles[ax][ay] = new Tile(1);
				}
			}
		}
	}

	private static void applyLand(Tile[][] tiles){
		Vector2[] spline = makeSpline(tiles.length, tiles[0].length);

		for (Vector2 point: spline){
			blotch(tiles, (int) point.x, (int) point.y);
		}
	}

	private static double computeNearestNeighbor(Tile[][] tiles, int x, int y){
		/*
		 * 1 2 3
		 * 4 5 6
		 * 7 8 9
		 */

		Tile v1 = null;
		Tile v2 = null;
		Tile v3 = null;
		Tile v4 = null;
		Tile v5 = tiles[x][y];
		Tile v6 = null;
		Tile v7 = null;
		Tile v8 = null;
		Tile v9 = null;

		if (x > 0 && y > 0){
			v1 = tiles[x - 1][y - 1];
		}
		if (y > 0){
			v2 = tiles[x][y - 1];
		}
		if (x + 1 < tiles.length && y > 0){
			v3 = tiles[x + 1][y - 1];
		}
		if (x > 0){
			v4 = tiles[x - 1][y];
		}
		if (x + 1 < tiles.length){
			v6 = tiles[x + 1][y];
		}
		if (x > 0 && y + 1 < tiles[x].length){
			v7 = tiles[x - 1][y + 1];
		}
		if (y + 1 < tiles[x].length){
			v8 = tiles[x][y + 1];
		}
		if (x + 1 < tiles.length && y + 1 < tiles[x].length){
			v9 = tiles[x + 1][y + 1];
		}

		double value = 0;
		int total = 0;
		if (v1 != null){
			value += v1.getValue();
			total += 1;
		}
		if (v2 != null){
			value += v2.getValue();
			total += 1;
		}
		if (v3 != null){
			value += v3.getValue();
			total += 1;
		}
		if (v4 != null){
			value += v4.getValue();
			total += 1;
		}
		if (v5 != null){
			value += v5.getValue();
			total += 1;
		}
		if (v6 != null){
			value += v6.getValue();
			total += 1;
		}
		if (v7 != null){
			value += v7.getValue();
			total += 1;
		}
		if (v8 != null){
			value += v8.getValue();
			total += 1;
		}
		if (v9 != null){
			value += v9.getValue();
			total += 1;
		}

		return value / total;
	}

	private static Tile[][] averageLand(Tile[][] tiles){
		/* A little wastey, but whatever */
		Tile[][] newTiles = new Tile[tiles.length][tiles[0].length];
		for (int x = 0; x < tiles.length; x++){
			for (int y = 0; y < tiles[x].length; y++){
				double average = computeNearestNeighbor(tiles, x, y);
				newTiles[x][y] = new Tile(average);
			}
		}

		return newTiles;
	}

	private static int randomInteger(int low, int high){
		return java.util.concurrent.ThreadLocalRandom.current().nextInt(low, high);
	}

	private static void initializeTiles(Tile[][] tiles){
		for (int x = 0; x < tiles.length; x++){
			for (int y = 0; y < tiles[x].length; y++){
				tiles[x][y] = new Tile(0);
			}
		}
	}

	public static World generate2(int width, int height){
		Tile[][] tiles = new Tile[width][height];
		initializeTiles(tiles);
		tiles[6][6] = new Tile(1);
		return new World(tiles);
	}

	public static World generate(int width, int height){
		Tile[][] tiles = new Tile[width][height];
		initializeTiles(tiles);
		int masses = 10;
		for (int i = 0; i < masses; i++){
			int x = randomInteger(0, width);
			int y = randomInteger(0, height);
			applyLand(tiles);
		}

		for (int i = 0; i < 3; i++){
			tiles = averageLand(tiles);
		}

		return new World(tiles);
	}
}

enum TileType{
	Forest,
	Beach,
	Ocean
}

class Tile{
	public Tile(double value){
		this.value = value;
		if (value >= 0.4){
			type = TileType.Forest;
		} else if (value >= 0.2){
			type = TileType.Beach;
		} else {
			type = TileType.Ocean;
		}
		
	}

	public TileType getType(){
		return type;
	}

	private double value;
	private TileType type;
	public double getValue(){
		return value;
	}
}
