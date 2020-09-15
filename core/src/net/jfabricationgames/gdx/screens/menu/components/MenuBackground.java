package net.jfabricationgames.gdx.screens.menu.components;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ObjectMap;

import net.jfabricationgames.gdx.texture.TextureLoader;

public class MenuBackground {
	
	public enum TextureType {
		
		GREEN_BOARD("config/menu/backgrounds/green_board_textures.json");
		
		public final String configFile;
		
		private TextureType(String configFile) {
			this.configFile = configFile;
		}
	}
	
	public enum Part {
		
		UP_LEFT("up_left"), //
		UP("up"), //
		UP_RIGHT("up_right"), //
		RIGHT("right"), //
		DOWN_RIGHT("down_right"), //
		DOWN("down"), //
		DOWN_LEFT("down_left"), //
		LEFT("left"), //
		MID("mid"); //
		
		public final String textureSuffix;
		
		private Part(String textureSuffix) {
			this.textureSuffix = textureSuffix;
		}
	}
	
	private int partsX;
	private int partsY;
	private TextureType type;
	
	private ObjectMap<Part, TextureRegion> textureParts;
	
	public MenuBackground(int partsX, int partsY, TextureType type) {
		if (partsX < 2 || partsY < 2) {
			throw new IllegalArgumentException("Minimum parts (for both directions) is 2 but was: partsX: " + partsX + " partsY: " + partsY);
		}
		
		this.partsX = partsX;
		this.partsY = partsY;
		this.type = type;
		
		loadTextureParts();
	}
	
	private void loadTextureParts() {
		textureParts = new ObjectMap<>();
		TextureLoader loader = new TextureLoader(type.configFile);
		for (Part part : Part.values()) {
			TextureRegion texture = loader.loadTexture(part.textureSuffix);
			textureParts.put(part, texture);
		}
	}
	
	public void draw(SpriteBatch batch, float x, float y, float width, float height) {
		float widthPerPart = width / (partsX + 1);
		float heightPerPart = height / (partsY + 1);
		
		for (int i = 0; i < partsY; i++) {
			for (int j = 0; j < partsX; j++) {
				TextureRegion texture = getTextureRegion(j, i);
				batch.draw(texture, x + widthPerPart * j, y + heightPerPart * i, widthPerPart, heightPerPart);
			}
		}
	}
	
	private TextureRegion getTextureRegion(int x, int y) {
		return textureParts.get(getPartForPosition(x, y));
	}
	
	private Part getPartForPosition(int x, int y) {
		if (x == 0) {
			if (y == 0) {
				return Part.DOWN_LEFT;
			}
			else if (y == partsY - 1) {
				return Part.UP_LEFT;
			}
			else {
				return Part.LEFT;
			}
		}
		else if (x == partsX - 1) {
			if (y == 0) {
				return Part.DOWN_RIGHT;
			}
			else if (y == partsY - 1) {
				return Part.UP_RIGHT;
			}
			else {
				return Part.RIGHT;
			}
		}
		else {
			if (y == 0) {
				return Part.DOWN;
			}
			else if (y == partsY - 1) {
				return Part.UP;
			}
			else {
				return Part.MID;
			}
		}
	}
}
