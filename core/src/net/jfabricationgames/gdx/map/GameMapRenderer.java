package net.jfabricationgames.gdx.map;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Json;

import net.jfabricationgames.gdx.character.animal.Animal;
import net.jfabricationgames.gdx.character.enemy.Enemy;
import net.jfabricationgames.gdx.character.npc.NonPlayableCharacter;
import net.jfabricationgames.gdx.data.handler.GlobalValuesDataHandler;
import net.jfabricationgames.gdx.item.Item;
import net.jfabricationgames.gdx.object.GameObject;
import net.jfabricationgames.gdx.projectile.Projectile;
import net.jfabricationgames.gdx.screens.game.GameScreen;

class GameMapRenderer implements Disposable {
	
	public static final String MAP_PROPERTY_KEY_BACKGROUND_LAYERS = "background_layers";
	public static final String MAP_PROPERTY_KEY_ABOVE_PLAYER_LAYERS = "above_player_layers";
	public static final String MAP_PROPERTY_KEY_SHADOW_LAYERS = "shadow_layers";
	
	public static final int[] BACKGROUND_LAYERS_DEFAULT = new int[] {0, 1};
	public static final int[] ABOVE_PLAYER_LAYERS_DEFAULT = new int[] {2};
	public static final int[] SHADOW_LAYERS_DEFAULT = new int[] {};
	
	private GameMap gameMap;
	
	private OrthographicCamera camera;
	private OrthogonalTiledMapRenderer renderer;
	
	private SpriteBatch batch;
	private ShapeRenderer shapeRenderer;
	
	private int[] backgroundLayers;
	private int[] abovePlayerLayers;
	private int[] shadowLayers;
	
	public GameMapRenderer(GameMap gameMap, OrthographicCamera camera) {
		this.gameMap = gameMap;
		this.camera = camera;
		
		batch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();
	}
	
	public void changeMap(TiledMap map) {
		renderer = new OrthogonalTiledMapRenderer(map, GameScreen.WORLD_TO_SCREEN, batch);
		renderer.setView(camera);
		loadLayersFromMapProperties(map.getProperties());
	}
	
	private void loadLayersFromMapProperties(MapProperties properties) {
		String backgroundLayersJson = properties.get(MAP_PROPERTY_KEY_BACKGROUND_LAYERS, String.class);
		String abovePlayerLayersJson = properties.get(MAP_PROPERTY_KEY_ABOVE_PLAYER_LAYERS, String.class);
		String shadowLayersJson = properties.get(MAP_PROPERTY_KEY_SHADOW_LAYERS, String.class);
		
		Json json = new Json();
		if (backgroundLayersJson != null) {
			backgroundLayers = json.fromJson(int[].class, backgroundLayersJson);
		}
		else {
			backgroundLayers = BACKGROUND_LAYERS_DEFAULT;
		}
		if (abovePlayerLayersJson != null) {
			abovePlayerLayers = json.fromJson(int[].class, abovePlayerLayersJson);
		}
		else {
			abovePlayerLayers = ABOVE_PLAYER_LAYERS_DEFAULT;
		}
		if (shadowLayersJson != null) {
			shadowLayers = json.fromJson(int[].class, shadowLayersJson);
		}
		else {
			shadowLayers = SHADOW_LAYERS_DEFAULT;
		}
	}
	
	public void updateCamera() {
		renderer.setView(camera);
	}
	
	public void renderBackground() {
		renderer.render(backgroundLayers);
	}
	
	public void renderAbovePlayer() {
		renderer.render(abovePlayerLayers);
	}
	
	public void renderShadows() {
		if (shadowLayers.length > 0) {
			renderer.render(shadowLayers);
		}
	}
	
	public void renderDarknessArroundPlayer() {
		if (gameMap.isDungeonMap() && !isLanternUsed()) {
			shapeRenderer.setProjectionMatrix(camera.combined);
			gameMap.player.renderDarkness(batch, shapeRenderer);
		}
	}
	
	private boolean isLanternUsed() {
		return GlobalValuesDataHandler.getInstance().getAsBoolean(GameMap.GLOBAL_VALUE_KEY_LANTERN_USED);
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
