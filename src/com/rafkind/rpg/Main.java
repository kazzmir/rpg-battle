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
	public World(){
	}

	private Tile[][] tiles;
}

enum TileType{
	Forest,
	Beach,
	Ocean
}

class Tile{
}
