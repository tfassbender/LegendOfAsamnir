package net.jfabricationgames.gdx.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

import net.jfabricationgames.gdx.item.Item;
import net.jfabricationgames.gdx.screens.GameScreen;

public class GameMap implements Disposable {
	
	private TiledMapLoader loader;
	private OrthographicCamera camera;
	private OrthogonalTiledMapRenderer renderer;
	
	private SpriteBatch batch;
	
	protected TiledMap map;
	protected Array<Item> items;
	protected Vector2 playerStartingPosition;
	
	private TiledMapPhysicsLoader mapPhysicsLoader;
	
	public GameMap(String mapAsset, OrthographicCamera camera, World world) {
		this.camera = camera;
		batch = new SpriteBatch();
		
		loader = new TiledMapLoader(mapAsset, this);
		loader.load();//initializes the map
		renderer = new OrthogonalTiledMapRenderer(map, GameScreen.WORLD_TO_SCREEN);
		
		mapPhysicsLoader = new TiledMapPhysicsLoader(world, GameScreen.SCREEN_TO_WORLD, Gdx.files.internal("map/materials.json"));
		mapPhysicsLoader.createPhysics(map);
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
