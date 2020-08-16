package net.jfabricationgames.gdx.item;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapProperties;

import net.jfabricationgames.gdx.assets.AssetGroupManager;
import net.jfabricationgames.gdx.screens.GameScreen;

public class ItemFactory {
	
	private static final String ITEM_ATLAS = "packed/items/items.atlas";
	
	private TextureAtlas atlas;
	
	public ItemFactory() {
		AssetGroupManager assetManager = AssetGroupManager.getInstance();
		atlas = assetManager.get(ITEM_ATLAS);
	}
	
	public Item createItem(String string, float x, float y, MapProperties properties) {
		Sprite sprite = new Sprite(atlas.findRegion(string));
		sprite.setX(x * GameScreen.WORLD_TO_SCREEN - sprite.getWidth() * 0.5f);
		sprite.setY(y * GameScreen.WORLD_TO_SCREEN - sprite.getHeight() * 0.5f);
		sprite.setScale(GameScreen.WORLD_TO_SCREEN);
		return new Item(string, sprite, properties);
	}
}
