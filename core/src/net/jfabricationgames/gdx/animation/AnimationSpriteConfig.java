package net.jfabricationgames.gdx.animation;

import com.badlogic.gdx.graphics.g2d.Sprite;

public class AnimationSpriteConfig {
	
	public static AnimationSpriteConfig fromSprite(Sprite sprite) {
		AnimationSpriteConfig spriteConfig = new AnimationSpriteConfig();
		spriteConfig.width = sprite.getWidth();
		spriteConfig.height = sprite.getHeight();
		spriteConfig.x = sprite.getX();
		spriteConfig.y = sprite.getY();
		
		return spriteConfig;
	}
	
	public float x;
	public float y;
	public float width;
	public float height;
}
