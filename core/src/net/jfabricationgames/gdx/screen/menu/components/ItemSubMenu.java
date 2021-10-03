package net.jfabricationgames.gdx.screen.menu.components;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

import net.jfabricationgames.gdx.texture.TextureLoader;

public abstract class ItemSubMenu extends MenuBox {
	
	private static final String selectionTextureConfigName = "selected";
	private static final String hoverTextureConfigName = "hover";
	private static final String itemTextureConfigFile = "config/menu/items/items.json";
	
	private int numItemsPerLine;
	private int numItemLines;
	
	private int selectedIndex;//-1 for none
	private int hoveredIndex;//-1 for none
	
	private TextureRegion selectionTexture;
	private TextureRegion hoverTexture;
	
	private TextureLoader itemTextureLoader;
	
	private Array<String> items;
	private Array<TextureRegion> itemTextures;
	
	public ItemSubMenu(int numItemsPerLine, int numItemRows, Array<String> items) {
		super(numItemsPerLine + 2, numItemRows + 2, TextureType.INVENTORY);
		this.numItemsPerLine = numItemsPerLine;
		this.numItemLines = numItemRows;
		this.items = items;
		
		selectedIndex = -1;
		hoveredIndex = -1;
		selectionTexture = textureLoader.loadTexture(selectionTextureConfigName);
		hoverTexture = textureLoader.loadTexture(hoverTextureConfigName);
		
		itemTextureLoader = new TextureLoader(itemTextureConfigFile);
		loadItemTextures();
		updateStateAfterMenuShown();
	}
	
	public void updateStateAfterMenuShown() {}
	
	private void loadItemTextures() {
		itemTextures = new Array<>(items.size);
		for (String item : items) {
			if (item != null) {
				itemTextures.add(itemTextureLoader.loadTexture(item));
			}
			else {
				itemTextures.add(null);
			}
		}
	}
	
	@Override
	protected TextureRegion getTextureRegion(int x, int y) {
		int itemIndex = calculateItemIndex(x, y);
		if (itemIndex >= 0) {
			if (itemIndex == hoveredIndex) {
				return hoverTexture;
			}
			else if (itemIndex == selectedIndex) {
				return selectionTexture;
			}
		}
		return super.getTextureRegion(x, y);
	}
	
	@Override
	protected void drawOnMenuField(SpriteBatch batch, int x, int y, float posX, float posY, float scaledWidth, float scaledHeight) {
		int index = calculateItemIndex(x, y);
		float borderFactor = 0.2f;
		float sizeFactor = (1f - (2f * borderFactor));
		
		if (index >= 0 && items.size > index) {
			TextureRegion itemTexture = itemTextures.get(index);
			if (itemTexture != null && isItemKnown(index)) {
				drawItem(batch, posX, posY, scaledWidth, scaledHeight, borderFactor, sizeFactor, itemTexture, index);
			}
		}
	}
	
	protected boolean isItemKnown(int index) {
		return true;
	}
	
	protected void drawItem(SpriteBatch batch, float posX, float posY, float scaledWidth, float scaledHeight, float borderFactor, float sizeFactor,
			TextureRegion itemTexture, int index) {
		batch.draw(itemTexture, posX + scaledWidth * borderFactor, posY + scaledHeight * borderFactor, scaledWidth * sizeFactor,
				scaledHeight * sizeFactor);
	}
	
	private int calculateItemIndex(int x, int y) {
		if (y == 0 || y == numItemLines + 1 || x == 0 || x == numItemsPerLine + 1) {
			//border indices
			return -1;
		}
		
		int lineOffset = (numItemsPerLine * (numItemLines - y));//y increases from bottom to top
		int indexInLine = x - 1;
		
		return lineOffset + indexInLine;
	}
	
	public void selectHoveredItem() {
		selectedIndex = hoveredIndex;
	}
	
	public String getSelectedItem() {
		if (selectedIndex >= 0 && items.size > selectedIndex) {
			return items.get(selectedIndex);
		}
		return null;
	}
	public void setSelectedIndex(int selectedIndex) {
		this.selectedIndex = selectedIndex;
	}
	
	public int getHoveredIndex() {
		return hoveredIndex;
	}
	public void setHoveredIndex(int hoveredIndex) {
		this.hoveredIndex = hoveredIndex;
	}
}
