package net.jfabricationgames.gdx.screens.menu.components;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class MenuBackground extends MenuBox {
	
	public MenuBackground(int partsX, int partsY, TextureType type) {
		super(partsX, partsY, type);
	}
	
	@Override
	protected void drawOnMenuField(SpriteBatch batch, int x, int y, float posX, float posY, float scaledWidth, float scaledHeight) {}
}
