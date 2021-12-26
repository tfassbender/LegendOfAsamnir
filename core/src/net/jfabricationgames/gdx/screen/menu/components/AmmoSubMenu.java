package net.jfabricationgames.gdx.screen.menu.components;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;

import net.jfabricationgames.gdx.character.player.PlayableCharacter;
import net.jfabricationgames.gdx.item.ItemAmmoType;
import net.jfabricationgames.gdx.screen.menu.InGameMenuScreen;
import net.jfabricationgames.gdx.text.ScreenTextWriter;
import net.jfabricationgames.gdx.texture.TextureLoader;

public class AmmoSubMenu extends SingleLineMenuBox {
	
	private static final String itemTextureConfigFile = "config/menu/items/items.json";
	
	private static final int NUM_ITEMS = 2;
	
	private class AmmoText {
		
		public String text;
		public float x;
		public float y;
		public float width;
		
		public AmmoText(String text, float x, float y, float width) {
			this.text = text;
			this.x = x;
			this.y = y;
			this.width = width;
		}
	}
	
	private Array<ItemAmmoType> items;
	private ArrayMap<ItemAmmoType, TextureRegion> itemTextures;
	private PlayableCharacter character;
	private ArrayMap<ItemAmmoType, AmmoText> ammoTextPositions;
	
	private TextureLoader itemTextureLoader;
	private ScreenTextWriter screenTextWriter;
	
	public AmmoSubMenu(PlayableCharacter character) {
		super(NUM_ITEMS, SingleLineTextureType.INVENTORY);
		this.character = character;
		
		items = new Array<>(new ItemAmmoType[] {ItemAmmoType.ARROW, ItemAmmoType.BOMB});
		ammoTextPositions = new ArrayMap<>();
		
		initializeScreenTextWriter();
		
		itemTextureLoader = new TextureLoader(itemTextureConfigFile);
		loadItemTextures();
	}
	
	private void initializeScreenTextWriter() {
		screenTextWriter = new ScreenTextWriter();
		screenTextWriter.setFont(InGameMenuScreen.FONT_NAME);
	}
	
	private void loadItemTextures() {
		itemTextures = new ArrayMap<>(items.size);
		for (ItemAmmoType item : items) {
			itemTextures.put(item, itemTextureLoader.loadTexture(item.name().toLowerCase()));
		}
	}
	
	public void setBatchProjectionMatrix(Matrix4 cameraMatrix) {
		screenTextWriter.setBatchProjectionMatrix(cameraMatrix);
	}
	
	@Override
	protected void drawOnMenuField(SpriteBatch batch, int x, int y, float posX, float posY, float scaledWidth, float scaledHeight) {
		int index = x;
		float borderFactor = 0.3f;
		float sizeFactor = (1f - (2f * borderFactor));
		float outerBorderOffset = 0;
		if (x == 0) {
			outerBorderOffset = 0.12f * scaledWidth;
		}
		else if (x == items.size - 1) {
			outerBorderOffset = -0.12f * scaledWidth;
		}
		
		if (index >= 0 && items.size > index) {
			ItemAmmoType ammoType = items.get(index);
			TextureRegion itemTexture = itemTextures.get(ammoType);
			batch.draw(itemTexture, posX + scaledWidth * borderFactor + outerBorderOffset, posY + scaledHeight * borderFactor,
					scaledWidth * sizeFactor, scaledHeight * sizeFactor);
			
			String ammo = Integer.toString(character.getAmmo(ammoType.name()));
			float textPosX = posX + scaledWidth * 0.4f + outerBorderOffset;
			float textPosY = posY + scaledHeight * 0.38f;
			ammoTextPositions.put(ammoType, new AmmoText(ammo, textPosX, textPosY, scaledWidth * sizeFactor));
		}
	}
	
	public void drawAmmoTexts() {
		screenTextWriter.setColor(Color.BLACK);
		screenTextWriter.setScale(0.8f);
		for (AmmoText ammoText : ammoTextPositions.values()) {
			screenTextWriter.drawText(ammoText.text, ammoText.x, ammoText.y, ammoText.width, Align.right, false);
		}
	}
}
