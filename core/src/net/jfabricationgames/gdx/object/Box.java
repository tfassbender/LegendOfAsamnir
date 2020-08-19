package net.jfabricationgames.gdx.object;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapProperties;

import net.jfabricationgames.gdx.animation.AnimationDirector;

public class Box extends DestroyableObject {
	
	public Box(ObjectType type, Sprite sprite, MapProperties properties) {
		super(type, sprite, properties);
		health = 20f;
		hitSound = "wood_hit_1";
		destroySound = "wood_break";
	}

	@Override
	protected AnimationDirector<TextureRegion> getHitAnimation() {
		return animationManager.getAnimationDirector("box_hit");
	}
	
	@Override
	protected AnimationDirector<TextureRegion> getDestroyAnimation() {
		return animationManager.getAnimationDirector("box_break");
	}
}
