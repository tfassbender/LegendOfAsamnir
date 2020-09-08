package net.jfabricationgames.gdx.animation;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class AnimationFrame {
	
	public static AnimationFrame getAnimationFrame(String texture) {
		int frameIndexDelimiter = texture.lastIndexOf('_');
		if (frameIndexDelimiter != -1) {
			String textureBaseName = texture.substring(0, frameIndexDelimiter);
			String indexText = texture.substring(frameIndexDelimiter + 1);
			try {
				int frameIndex = Integer.parseInt(indexText);
				return new AnimationFrame(textureBaseName, frameIndex);
			}
			catch (NumberFormatException nfe) {
				//not handled; just return the default AnimationFrame
			}
		}
		return new AnimationFrame(texture, -1);
	}
	
	public String textureBaseName;
	public int index;
	
	private AnimationFrame(String textureBaseName, int index) {
		this.textureBaseName = textureBaseName;
		this.index = index;
	}
	
	public TextureRegion findRegion(TextureAtlas atlas) {
		if (index != -1) {
			return atlas.findRegion(textureBaseName, index);
		}
		else {
			return atlas.findRegion(textureBaseName);
		}
	}
}
