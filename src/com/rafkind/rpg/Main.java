package com.rafkind.rpg;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class Main extends ApplicationAdapter {
	SpriteBatch batch;
	Texture forest;

	@Override
	public void create () {
		batch = new SpriteBatch();
		forest = new Texture("data/land/forest.png");
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		batch.draw(forest, 0, 0);
		batch.draw(forest, 0, 100);
		batch.end();
	}

	public static class DesktopLauncher {
		public static void main(String... args) {
			LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
			new LwjglApplication(new Main(), config);
		}
	}
}

class World{
	public World(Tile[][] tiles){
		this.tiles = tiles;
	}

	private Tile[][] tiles;

	/* Set a blotch of tiles to be land */
	private static void applyLand(Tile[][] tiles, int x, int y, int diameter){
		for (int cx = x - diameter / 2; cx < x + diameter / 2; cx++){
			for (int cy = y - diameter / 2; cy < y + diameter / 2; cy++){
				if (cx >= 0 && cx < tiles.length && cy >= 0 && cy < tiles[cx].length){
					tiles[cx][cy] = new Tile(1);
				}
			}
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

	public static World generate(int width, int height){
		Tile[][] tiles = new Tile[width][height];
		initializeTiles(tiles);
		int diameter = 20;
		int masses = 5;
		if (width < diameter || height < diameter){
			throw new RuntimeException("Width and height must be larger than " + diameter + " but given " + width + ", " + height);
		}
		for (int i = 0; i < masses; i++){
			int x = randomInteger(diameter, width - diameter);
			int y = randomInteger(diameter, height - diameter);
			applyLand(tiles, x, y, diameter);
		}

		for (int i = 0; i < 5; i++){
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
		if (value >= 0.5){
			type = TileType.Forest;
		} else {
			type = TileType.Ocean;
		}
		
	}

	private double value;
	private TileType type;
	public double getValue(){
		return value;
	}
}
