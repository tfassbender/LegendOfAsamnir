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
	
	public AnimationSpriteConfig setX(float x) {
		this.x = x;
		return this;
	}
	
	public AnimationSpriteConfig setY(float y) {
		this.y = y;
		return this;
	}
	
	public AnimationSpriteConfig setWidth(float width) {
		this.width = width;
		return this;
	}
	
	public AnimationSpriteConfig setHeight(float height) {
		this.height = height;
		return this;
	}
}
