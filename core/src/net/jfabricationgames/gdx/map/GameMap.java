package net.jfabricationgames.gdx.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

import net.jfabricationgames.gdx.character.PlayableCharacter;
import net.jfabricationgames.gdx.character.PlayerFactory;
import net.jfabricationgames.gdx.cutscene.CutsceneHandler;
import net.jfabricationgames.gdx.enemy.Enemy;
import net.jfabricationgames.gdx.enemy.EnemyFactory;
import net.jfabricationgames.gdx.item.Item;
import net.jfabricationgames.gdx.item.ItemFactory;
import net.jfabricationgames.gdx.object.GameObject;
import net.jfabricationgames.gdx.object.GameObjectFactory;
import net.jfabricationgames.gdx.physics.PhysicsWorld;
import net.jfabricationgames.gdx.projectile.Projectile;
import net.jfabricationgames.gdx.projectile.ProjectileFactory;
import net.jfabricationgames.gdx.screens.game.GameScreen;

public class GameMap implements Disposable {
	
	public enum GlobalMapPropertyKeys {
		
		MINI_MAP_CONFIG_PATH("mini_map_config_path"), //
		MAP_WIDTH_IN_TILE_DIMENSIONS("width"), //
		MAP_HEIGHT_IN_TILE_DIMENSIONS("height"), //
		MAP_TILE_WIDTH_IN_PIXELS("tilewidth"), // 
		MAP_TILE_HEIGHT_IN_PIXELS("tileheight");
		
		private final String key;
		
		private GlobalMapPropertyKeys(String key) {
			this.key = key;
		}
		
		public String getKey() {
			return key;
		}
	}
	
	public static final String MAP_MATERIALS_CONFIG_FILE = "config/map/materials.json";
	public static final int[] BACKGROUND_LAYERS = new int[] {0, 1};
	public static final int[] TERRAIN_LAYERS = new int[] {2};
	
	protected TiledMap map;
	protected Vector2 playerStartingPosition;
	
	//the lists are initialized in the factories
	protected Array<Item> items;
	protected Array<Item> itemsAboveGameObjects;
	protected Array<GameObject> objects;
	protected Array<Enemy> enemies;
	protected Array<Projectile> projectiles;
	
	protected ItemFactory itemFactory;
	protected GameObjectFactory objectFactory;
	protected EnemyFactory enemyFactory;
	
	private OrthographicCamera camera;
	private OrthogonalTiledMapRenderer renderer;
	
	private SpriteBatch batch;
	private ShapeRenderer shapeRenderer;
	
	private PlayableCharacter player;
	
	private CutsceneHandler cutsceneHandler;
	
	public GameMap(OrthographicCamera camera) {
		this.camera = camera;
		
		batch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();
		
		itemsAboveGameObjects = new Array<>();
		projectiles = new Array<>();
		ProjectileFactory.createInstance(this);
		
		itemFactory = new ItemFactory(this);
		objectFactory = new GameObjectFactory(this);
		enemyFactory = new EnemyFactory(this);
		
		cutsceneHandler = CutsceneHandler.getInstance();
		cutsceneHandler.setGameMap(this);
		
		// create the player before other map objects, because it contains event listeners that listen for events that are fired when these objects are created
		player = PlayerFactory.createPlayer();
	}
	
	public void showMap(String mapAsset) {
		removeCurrentMapIfPresent();
		
		player.reAddToWorld();
		
		TiledMapLoader loader = new TiledMapLoader(mapAsset, this);
		loader.loadMap();
		renderer = new OrthogonalTiledMapRenderer(map, GameScreen.WORLD_TO_SCREEN, batch);
		renderer.setView(camera);
		
		TiledMapPhysicsLoader mapPhysicsLoader = new TiledMapPhysicsLoader(GameScreen.SCREEN_TO_WORLD, Gdx.files.internal(MAP_MATERIALS_CONFIG_FILE));
		mapPhysicsLoader.createPhysics(map);
		
		player.setPosition(playerStartingPosition.x, playerStartingPosition.y);
	}
	
	private void removeCurrentMapIfPresent() {
		if (isMapInitialized()) {
			removeGameObjects();
			removeBodiesFromWorld();
			clearObjectLists();
		}
	}
	
	private void removeGameObjects() {
		for (Item item : items) {
			item.removeFromMap();
		}
		for (Item item : itemsAboveGameObjects) {
			item.removeFromMap();
		}
		for (GameObject object : objects) {
			object.removeFromMap();
		}
		for (Enemy enemy : enemies) {
			enemy.removeFromMap();
		}
		for (Projectile projectile : projectiles) {
			projectile.removeFromMap();
		}
		player.removeFromMap();
	}
	
	private void removeBodiesFromWorld() {
		World world = PhysicsWorld.getInstance().getWorld();
		Gdx.app.debug(getClass().getSimpleName(), "removeCurrentMap - world locked: " + world.isLocked());
		
		Array<Body> bodies = new Array<Body>();
		world.getBodies(bodies);
		for (Body body : bodies) {
			world.destroyBody(body);
		}
	}
	
	private void clearObjectLists() {
		items.clear();
		itemsAboveGameObjects.clear();
		objects.clear();
		enemies.clear();
		projectiles.clear();
	}
	
	private boolean isMapInitialized() {
		return items != null && itemsAboveGameObjects != null && objects != null && enemies != null && projectiles != null;
	}
	
	public void renderBackground() {
		renderer.render(BACKGROUND_LAYERS);
	}
	
	public void processAndRenderGameObject(float delta) {
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		renderItems(delta);
		renderObjects(delta);
		renderItemsAboveGameObjects(delta);
		
		processCutscene(delta);
		processEnemies(delta);
		renderEnemies(delta);
		processProjectiles(delta);
		renderProjectiles();
		
		renderPlayer(delta);
		batch.end();
		
		shapeRenderer.setProjectionMatrix(camera.combined);
		shapeRenderer.begin(ShapeType.Filled);
		renderEnemyHealthBars();
		shapeRenderer.end();
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
	
	private void renderItemsAboveGameObjects(float delta) {
		for (Item item : itemsAboveGameObjects) {
			item.draw(delta, batch);
		}
	}
	
	private void processCutscene(float delta) {
		cutsceneHandler.act(delta);
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
	
	private void processProjectiles(float delta) {
		for (Projectile projectile : projectiles) {
			projectile.update(delta);
		}
	}
	private void renderProjectiles() {
		for (Projectile projectile : projectiles) {
			projectile.draw(batch);
		}
	}
	
	private void renderPlayer(float delta) {
		player.render(delta, batch);
	}
	
	private void renderEnemyHealthBars() {
		for (Enemy enemy : enemies) {
			enemy.drawHealthBar(shapeRenderer);
		}
	}
	
	public void renderTerrain() {
		renderer.setView(camera);
		renderer.render(TERRAIN_LAYERS);
	}
	
	public Vector2 getPlayerStartingPosition() {
		return playerStartingPosition;
	}
	
	public PlayableCharacter getPlayer() {
		return player;
	}
	
	public void addItem(Item item) {
		items.add(item);
	}
	
	public void addItemAboveGameObjects(Item item) {
		itemsAboveGameObjects.add(item);
	}
	
	public void removeItem(Item item, Body body) {
		items.removeValue(item, false);
		itemsAboveGameObjects.removeValue(item, false);
		removePhysicsBody(body);
	}
	
	public void addObject(GameObject object) {
		objects.add(object);
		object.postAddToGameMap();
	}
	
	public void removeObject(GameObject gameObject, Body body) {
		objects.removeValue(gameObject, false);
		removePhysicsBody(body);
	}
	
	public void addEnemy(Enemy enemy) {
		enemies.add(enemy);
	}
	
	public void removeEnemy(Enemy enemy, Body body) {
		enemies.removeValue(enemy, false);
		removePhysicsBody(body);
	}
	
	public void addProjectile(Projectile projectile) {
		projectiles.add(projectile);
	}
	
	public void removeProjectile(Projectile projectile, Body body) {
		projectiles.removeValue(projectile, false);
		removePhysicsBody(body);
	}
	
	private void removePhysicsBody(Body body) {
		PhysicsWorld.getInstance().removeBodyWhenPossible(body);
	}
	
	public ItemFactory getItemFactory() {
		return itemFactory;
	}
	
	public GameObjectFactory getObjectFactory() {
		return objectFactory;
	}
	
	public EnemyFactory getEnemyFactory() {
		return enemyFactory;
	}
	
	public MapProperties getGlobalMapProperties() {
		return map.getProperties();
	}
	
	public float getMapWidth() {
		return map.getProperties().get(GlobalMapPropertyKeys.MAP_WIDTH_IN_TILE_DIMENSIONS.getKey(), Integer.class) //
				* map.getProperties().get(GlobalMapPropertyKeys.MAP_TILE_WIDTH_IN_PIXELS.getKey(), Integer.class);
	}
	public float getMapHeight() {
		return map.getProperties().get(GlobalMapPropertyKeys.MAP_HEIGHT_IN_TILE_DIMENSIONS.getKey(), Integer.class) //
				* map.getProperties().get(GlobalMapPropertyKeys.MAP_TILE_HEIGHT_IN_PIXELS.getKey(), Integer.class);
	}
	
	public Object getUnitById(String unitId) {
		for (Enemy enemy : enemies) {
			if (unitId.equals(enemy.getUnitId())) {
				return enemy;
			}
		}
		
		for (GameObject object : objects) {
			if (unitId.equals(object.getUnitId())) {
				return object;
			}
		}
		
		for (Item item : items) {
			if (unitId.equals(item.getUnitId())) {
				return item;
			}
		}
		
		return null;
	}
	
	@Override
	public void dispose() {
		renderer.dispose();
		map.dispose();
		batch.dispose();
	}
}
