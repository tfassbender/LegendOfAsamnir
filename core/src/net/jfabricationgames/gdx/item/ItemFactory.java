package net.jfabricationgames.gdx.item;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.physics.box2d.World;

import net.jfabricationgames.gdx.assets.AssetGroupManager;
import net.jfabricationgames.gdx.physics.PhysicsWorld;
import net.jfabricationgames.gdx.screens.GameScreen;

public class ItemFactory {
	
	private static final String ITEM_ATLAS = "packed/items/items.atlas";
	
	private TextureAtlas atlas;
	private World world;
	
	public ItemFactory() {
		AssetGroupManager assetManager = AssetGroupManager.getInstance();
		atlas = assetManager.get(ITEM_ATLAS);
		world = PhysicsWorld.getInstance().getWorld();
	}
	
	public Item createItem(String string, float x, float y, MapProperties properties) {
		Sprite sprite = new Sprite(atlas.findRegion(string));
		sprite.setX(x * GameScreen.WORLD_TO_SCREEN - sprite.getWidth() * 0.5f);
		sprite.setY(y * GameScreen.WORLD_TO_SCREEN - sprite.getHeight() * 0.5f);
		sprite.setScale(GameScreen.WORLD_TO_SCREEN);
		
		Item item = new Item(string, sprite, properties);
		item.createPhysicsBody(world, x * GameScreen.WORLD_TO_SCREEN, y * GameScreen.WORLD_TO_SCREEN);
		
		return item;
	}
}
