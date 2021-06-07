package net.jfabricationgames.gdx.object.movable;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapProperties;

import net.jfabricationgames.gdx.object.GameObject;
import net.jfabricationgames.gdx.object.GameObjectTypeConfig;

public class MovableObject extends GameObject {
	
	public MovableObject(GameObjectTypeConfig typeConfig, Sprite sprite, MapProperties mapProperties) {
		super(typeConfig, sprite, mapProperties);
	}
	
	@Override
	protected void createPhysicsBody(float x, float y) {
		super.createPhysicsBody(x, y);
	}
	
	@Override
	public void draw(float delta, SpriteBatch batch) {
		if (animation != null && !animation.isAnimationFinished()) {
			animation.increaseStateTime(delta);
			TextureRegion region = animation.getKeyFrame();
			animation.getSpriteConfig().setX((body.getPosition().x - region.getRegionWidth() * 0.5f))
					.setY((body.getPosition().y - region.getRegionHeight() * 0.5f));
			animation.draw(batch);
		}
		else {
			sprite.setX((body.getPosition().x - sprite.getRegionWidth() * 0.5f));
			sprite.setY((body.getPosition().y - sprite.getRegionHeight() * 0.5f));
			sprite.draw(batch);
		}
	}
}
