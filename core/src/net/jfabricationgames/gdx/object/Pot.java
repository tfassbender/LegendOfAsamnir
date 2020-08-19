package net.jfabricationgames.gdx.object;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapProperties;

import net.jfabricationgames.gdx.animation.AnimationDirector;

public class Pot extends DestroyableObject {
	
	public Pot(ObjectType type, Sprite sprite, MapProperties properties) {
		super(type, sprite, properties);
		health = 5f;
		destroySound = "glass_break";
	}

	@Override
	protected AnimationDirector<TextureRegion> getHitAnimation() {
		return animationManager.getAnimationDirector("pot_hit");
	}
	
	@Override
	protected AnimationDirector<TextureRegion> getDestroyAnimation() {
		return animationManager.getAnimationDirector("pot_break");
	}
}
