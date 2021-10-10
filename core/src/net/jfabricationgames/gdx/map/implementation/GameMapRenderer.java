package net.jfabricationgames.gdx.map.implementation;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Json;

import net.jfabricationgames.gdx.character.animal.Animal;
import net.jfabricationgames.gdx.character.enemy.Enemy;
import net.jfabricationgames.gdx.character.npc.NonPlayableCharacter;
import net.jfabricationgames.gdx.constants.Constants;
import net.jfabricationgames.gdx.data.handler.GlobalValuesDataHandler;
import net.jfabricationgames.gdx.item.Item;
import net.jfabricationgames.gdx.object.GameObject;
import net.jfabricationgames.gdx.projectile.Projectile;
import net.jfabricationgames.gdx.rune.RuneType;

class GameMapRenderer implements Disposable {
	
	public static final String MAP_PROPERTY_KEY_BACKGROUND_LAYERS = "background_layers";
	public static final String MAP_PROPERTY_KEY_INVISIBLE_PATHS_LAYER = "invisible_paths_layer";
	public static final String MAP_PROPERTY_KEY_ABOVE_PLAYER_LAYERS = "above_player_layers";
	public static final String MAP_PROPERTY_KEY_SHADOW_LAYERS = "shadow_layers";
	
	private static final float INVISIBLE_PATH_OPACITY_MIN = 0.4f;
	private static final float INVISIBLE_PATH_OPACITY_MAX = 0.8f;
	private static final float INVISIBLE_PATH_OPACITY_CHANGE_ABSOLUT = 0.2f;
	
	private GameMapImplementation gameMap;
	private TiledMap tiledMap;
	
	private OrthographicCamera camera;
	private OrthogonalTiledMapRenderer renderer;
	
	private SpriteBatch batch;
	private ShapeRenderer shapeRenderer;
	
	private float invisiblePathOpacity = INVISIBLE_PATH_OPACITY_MIN;
	private float invisiblePathOpacityChange = INVISIBLE_PATH_OPACITY_CHANGE_ABSOLUT;
	
	private int[] backgroundLayers;
	private int[] invisiblePathsLayer;
	private int[] abovePlayerLayers;
	private int[] shadowLayers;
	
	public GameMapRenderer(GameMapImplementation gameMap, OrthographicCamera camera) {
		this.gameMap = gameMap;
		this.camera = camera;
		
		batch = new SpriteBatch();
		batch.enableBlending();
		shapeRenderer = new ShapeRenderer();
	}
	
	public void changeMap(TiledMap map) {
		this.tiledMap = map;
		renderer = new OrthogonalTiledMapRenderer(map, Constants.WORLD_TO_SCREEN, batch);
		renderer.setView(camera);
		loadLayersFromMapProperties(map.getProperties());
	}
	
	private void loadLayersFromMapProperties(MapProperties properties) {
		String backgroundLayersJson = properties.get(MAP_PROPERTY_KEY_BACKGROUND_LAYERS, String.class);
		String invisiblePathsLayerJson = properties.get(MAP_PROPERTY_KEY_INVISIBLE_PATHS_LAYER, String.class);
		String abovePlayerLayersJson = properties.get(MAP_PROPERTY_KEY_ABOVE_PLAYER_LAYERS, String.class);
		String shadowLayersJson = properties.get(MAP_PROPERTY_KEY_SHADOW_LAYERS, String.class);
		
		Json json = new Json();
		if (backgroundLayersJson != null) {
			backgroundLayers = json.fromJson(int[].class, backgroundLayersJson);
		}
		else {
			backgroundLayers = new int[0];
		}
		
		if (invisiblePathsLayerJson != null) {
			invisiblePathsLayer = json.fromJson(int[].class, invisiblePathsLayerJson);
		}
		else {
			invisiblePathsLayer = new int[0];
		}
		
		if (abovePlayerLayersJson != null) {
			abovePlayerLayers = json.fromJson(int[].class, abovePlayerLayersJson);
		}
		else {
			abovePlayerLayers = new int[0];
		}
		
		if (shadowLayersJson != null) {
			shadowLayers = json.fromJson(int[].class, shadowLayersJson);
		}
		else {
			shadowLayers = new int[0];
		}
	}
	
	public void updateCamera() {
		renderer.setView(camera);
	}
	
	public void renderBackground(float delta) {
		renderer.render(backgroundLayers);
		if (invisiblePathsRuneCollected()) {
			updateAndRenderInvisiblePaths(delta);
		}
	}
	
	private void updateAndRenderInvisiblePaths(float delta) {
		updateInvisiblePathsOpacity(delta);
		
		//batch begin and end only needs to be called when using the renderTileLayer method
		batch.begin();
		
		for (int layer : invisiblePathsLayer) {
			MapLayer mapLayer = tiledMap.getLayers().get(layer);
			mapLayer.setOpacity(invisiblePathOpacity);
			if (mapLayer instanceof TiledMapTileLayer) {
				//render the layers as TiledMapTileLayer, to be able to set their opacity
				renderer.renderTileLayer((TiledMapTileLayer) mapLayer);
			}
		}
		
		batch.end();
	}
	
	private boolean invisiblePathsRuneCollected() {
		return RuneType.ALGIZ.isCollected();
	}
	
	private void updateInvisiblePathsOpacity(float delta) {
		invisiblePathOpacity += invisiblePathOpacityChange * delta;
		if (invisiblePathOpacity > INVISIBLE_PATH_OPACITY_MAX) {
			invisiblePathOpacity = INVISIBLE_PATH_OPACITY_MAX;
			invisiblePathOpacityChange *= -1;
		}
		if (invisiblePathOpacity < INVISIBLE_PATH_OPACITY_MIN) {
			invisiblePathOpacity = INVISIBLE_PATH_OPACITY_MIN;
			invisiblePathOpacityChange *= -1;
		}
	}
	
	public void renderAbovePlayer() {
		renderer.render(abovePlayerLayers);
	}
	
	public void renderShadows() {
		renderer.render(shadowLayers);
	}
	
	public void renderDarknessArroundPlayer() {
		if (gameMap.isDungeonMap() && !isLanternUsed()) {
			shapeRenderer.setProjectionMatrix(camera.combined);
			gameMap.player.renderDarkness(batch, shapeRenderer);
		}
	}
	
	private boolean isLanternUsed() {
		return GlobalValuesDataHandler.getInstance().getAsBoolean(Constants.GLOBAL_VALUE_KEY_LANTERN_USED);
	}
	
	public void beginBatch() {
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
	}
	
	public void endBatch() {
		batch.end();
	}
	
	public void renderItems(float delta) {
		for (Item item : gameMap.items) {
			item.draw(delta, batch);
		}
	}
	
	public void renderObjects(float delta) {
		for (GameObject object : gameMap.objects) {
			object.draw(delta, batch);
		}
	}
	
	public void renderItemsAboveGameObjects(float delta) {
		for (Item item : gameMap.itemsAboveGameObjects) {
			item.draw(delta, batch);
		}
	}
	
	public void renderEnemies(float delta) {
		for (Enemy enemy : gameMap.enemies) {
			enemy.draw(delta, batch);
		}
	}
	
	public void renderNpcs(float delta) {
		for (NonPlayableCharacter npc : gameMap.nonPlayableCharacters) {
			npc.draw(delta, batch);
		}
	}
	
	public void renderAnimals(float delta) {
		for (Animal animal : gameMap.animals) {
			animal.draw(delta, batch);
		}
	}
	
	public void renderProjectiles(float delta) {
		for (Projectile projectile : gameMap.projectiles) {
			projectile.draw(delta, batch);
		}
	}
	
	public void renderPlayer(float delta) {
		gameMap.player.render(delta, batch);
	}
	
	public void beginShapeRenderer() {
		shapeRenderer.setProjectionMatrix(camera.combined);
		shapeRenderer.begin(ShapeType.Filled);
	}
	
	public void renderEnemyHealthBars() {
		for (Enemy enemy : gameMap.enemies) {
			enemy.drawHealthBar(shapeRenderer);
		}
	}
	
	public void endShapeRenderer() {
		shapeRenderer.end();
	}
	
	@Override
	public void dispose() {
		renderer.dispose();
		batch.dispose();
		shapeRenderer.dispose();
	}
}
