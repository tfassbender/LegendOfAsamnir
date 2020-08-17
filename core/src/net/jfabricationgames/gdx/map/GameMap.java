package net.jfabricationgames.gdx.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

import net.jfabricationgames.gdx.item.Item;
import net.jfabricationgames.gdx.object.GameObject;
import net.jfabricationgames.gdx.screens.GameScreen;

public class GameMap implements Disposable {
	
	public static final String MAP_MATERIALS_CONFIG_FILE = "config/map/materials.json";
	
	private TiledMapLoader loader;
	private OrthographicCamera camera;
	private OrthogonalTiledMapRenderer renderer;
	
	private SpriteBatch batch;
	
	protected TiledMap map;
	protected Array<Item> items;
	protected Array<GameObject> objects;
	protected Vector2 playerStartingPosition;
	
	private TiledMapPhysicsLoader mapPhysicsLoader;
	
	public GameMap(String mapAsset, OrthographicCamera camera) {
		this.camera = camera;
		batch = new SpriteBatch();
		
		loader = new TiledMapLoader(mapAsset, this);
		loader.load();//initializes the map
		renderer = new OrthogonalTiledMapRenderer(map, GameScreen.WORLD_TO_SCREEN);
		
		mapPhysicsLoader = new TiledMapPhysicsLoader(GameScreen.SCREEN_TO_WORLD, Gdx.files.internal(MAP_MATERIALS_CONFIG_FILE));
		mapPhysicsLoader.createPhysics(map);
	}
	
	public void render(float delta) {
		renderer.setView(camera);
		renderer.render();
		
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		renderItems();
		renderObjects();
		batch.end();
	}
	
	private void renderItems() {
		for (Item item : items) {
			item.getSprite().draw(batch);
		}
	}
	private void renderObjects() {
		for (GameObject object : objects) {
			object.getSprite().draw(batch);
		}
	}
	
	public Vector2 getPlayerStartingPosition() {
		return playerStartingPosition;
	}
	
	public void addItem(Item item) {
		items.add(item);
	}
	
	public void removeItem(Item item) {
		items.removeValue(item, false);
	}
	
	@Override
	public void dispose() {
		renderer.dispose();
		map.dispose();
		batch.dispose();
	}
}
