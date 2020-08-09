package net.jfabricationgames.gdx.map;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

import net.jfabricationgames.gdx.item.Item;

public class GameMap implements Disposable {
	
	private TiledMapLoader loader;
	private OrthographicCamera camera;
	private OrthogonalTiledMapRenderer renderer;
	
	private SpriteBatch batch;
	
	protected TiledMap map;
	protected Array<Item> items;
	protected Vector2 playerStartingPosition;
	
	public GameMap(String mapAsset, OrthographicCamera camera) {
		this.camera = camera;
		batch = new SpriteBatch();
		
		loader = new TiledMapLoader(mapAsset, this);
		loader.load();//initializes the map
		renderer = new OrthogonalTiledMapRenderer(map);
	}
	
	public void render(float delta) {
		renderer.setView(camera);
		renderer.render();
		
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		renderItems();
		batch.end();
	}
	
	private void renderItems() {
		for (Item item : items) {
			item.getSprite().draw(batch);
		}
	}
	
	public Vector2 getPlayerStartingPosition() {
		return playerStartingPosition;
	}
	
	@Override
	public void dispose() {
		renderer.dispose();
		map.dispose();
		batch.dispose();
	}
}
