package net.jfabricationgames.gdx.object;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapProperties;

import net.jfabricationgames.gdx.animation.AnimationDirector;

public class Barrel extends DestroyableObject {
	
	public Barrel(ObjectType type, Sprite sprite, MapProperties properties) {
		super(type, sprite, properties);
		health = 30f;
		hitSound = "wood_hit_2";
		destroySound = "wood_break";
	}
	
	@Override
	protected AnimationDirector<TextureRegion> getHitAnimation() {
		return animationManager.getAnimationDirector("barrel_hit");
	}
	
	@Override
	protected AnimationDirector<TextureRegion> getDestroyAnimation() {
		return animationManager.getAnimationDirector("barrel_break");
	}
}
