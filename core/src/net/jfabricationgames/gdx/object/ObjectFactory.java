package net.jfabricationgames.gdx.object;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.physics.box2d.World;

import net.jfabricationgames.gdx.animation.AnimationManager;
import net.jfabricationgames.gdx.assets.AssetGroupManager;
import net.jfabricationgames.gdx.map.GameMap;
import net.jfabricationgames.gdx.physics.PhysicsWorld;
import net.jfabricationgames.gdx.screens.GameScreen;

public class ObjectFactory {
	
	private static final String OBJECTS_ATLAS = "packed/objects/objects.atlas";
	private static final String OBJECTS_ANIMATIONS = "config/animation/objects.json";
	
	private TextureAtlas atlas;
	private World world;
	
	private GameMap gameMap;
	
	public ObjectFactory(GameMap gameMap) {
		this.gameMap = gameMap;
		AssetGroupManager assetManager = AssetGroupManager.getInstance();
		AnimationManager.getInstance().loadAnimations(OBJECTS_ANIMATIONS);
		atlas = assetManager.get(OBJECTS_ATLAS);
		world = PhysicsWorld.getInstance().getWorld();
	}
	
	public GameObject createObject(ObjectType type, float x, float y, MapProperties properties) {
		Sprite sprite = new Sprite(atlas.findRegion(type.getTextureName()));
		sprite.setX(x * GameScreen.WORLD_TO_SCREEN - sprite.getWidth() * 0.5f);
		sprite.setY(y * GameScreen.WORLD_TO_SCREEN - sprite.getHeight() * 0.5f);
		sprite.setScale(GameScreen.WORLD_TO_SCREEN);
		
		GameObject object;
		switch (type) {
			case BARREL:
				object = new Barrel(type, sprite, properties);
				break;
			case BOX:
				object = new Box(type, sprite, properties);
				break;
			case CHEST:
				object = new Chest(type, sprite, properties);
				break;
			case POT:
				object = new Pot(type, sprite, properties);
				break;
			default:
				throw new IllegalStateException("Unknown object type: " + type);
		}
		object.setGameMap(gameMap);
		object.createPhysicsBody(world, x * GameScreen.WORLD_TO_SCREEN, y * GameScreen.WORLD_TO_SCREEN);
		
		return object;
	}
}
