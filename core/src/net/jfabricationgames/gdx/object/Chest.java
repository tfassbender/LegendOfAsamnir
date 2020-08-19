package net.jfabricationgames.gdx.object;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapProperties;

import net.jfabricationgames.gdx.animation.AnimationDirector;

public class Chest extends GameObject {
	
	public Chest(ObjectType type, Sprite sprite, MapProperties properties) {
		super(type, sprite, properties);
		hitSound = "wood_knock";
	}
	
	@Override
	protected AnimationDirector<TextureRegion> getHitAnimation() {
		return animationManager.getAnimationDirector("chest_hit");
	}
}
