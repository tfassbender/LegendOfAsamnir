package net.jfabricationgames.gdx.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

import net.jfabricationgames.gdx.enemy.Enemy;
import net.jfabricationgames.gdx.item.Item;
import net.jfabricationgames.gdx.object.GameObject;
import net.jfabricationgames.gdx.screens.GameScreen;

public class GameMap implements Disposable {
	
	public static final String MAP_MATERIALS_CONFIG_FILE = "config/map/materials.json";
	
	private OrthographicCamera camera;
	private OrthogonalTiledMapRenderer renderer;
	
	private SpriteBatch batch;
	
	protected TiledMap map;
	protected Vector2 playerStartingPosition;
	
	protected Array<Item> items;
	protected Array<GameObject> objects;
	protected Array<Enemy> enemies;
	
	public GameMap(String mapAsset, OrthographicCamera camera) {
		this.camera = camera;
		batch = new SpriteBatch();
		
		TiledMapLoader loader = new TiledMapLoader(mapAsset, this);
		loader.load();//initializes the map
		renderer = new OrthogonalTiledMapRenderer(map, GameScreen.WORLD_TO_SCREEN);
		
		TiledMapPhysicsLoader mapPhysicsLoader = new TiledMapPhysicsLoader(GameScreen.SCREEN_TO_WORLD, Gdx.files.internal(MAP_MATERIALS_CONFIG_FILE));
		mapPhysicsLoader.createPhysics(map);
	}
	
	public void render(float delta) {
		renderer.setView(camera);
		renderer.render();
		
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		renderItems(delta);
		renderObjects(delta);
		processEnemies(delta);
		renderEnemies(delta);
		batch.end();
	}
	
	private void renderItems(float delta) {
		for (Item item : items) {
			item.draw(delta, batch);
		}
	}
	private void renderObjects(float delta) {
		for (GameObject object : objects) {
			object.draw(delta, batch);
		}
	}

	private void processEnemies(float delta) {
		for (Enemy enemy : enemies) {
			enemy.act(delta);
		}
	}
	private void renderEnemies(float delta) {
		for (Enemy enemy : enemies) {
			enemy.draw(delta, batch);
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
	
	public void addObject(GameObject object) {
		objects.add(object);
	}
	
	public void removeObject(GameObject gameObject) {
		objects.removeValue(gameObject, false);
	}
	
	public void addEnemy(Enemy enemy) {
		enemies.add(enemy);
	}
	
	public void removeEnemy(Enemy enemy) {
		enemies.removeValue(enemy, false);
	}
	
	@Override
	public void dispose() {
		renderer.dispose();
		map.dispose();
		batch.dispose();
	}
}
