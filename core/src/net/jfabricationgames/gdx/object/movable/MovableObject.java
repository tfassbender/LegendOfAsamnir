package net.jfabricationgames.gdx.object.movable;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.utils.Array;

import net.jfabricationgames.gdx.map.GameMapManager;
import net.jfabricationgames.gdx.object.GameObject;
import net.jfabricationgames.gdx.object.GameObjectTypeConfig;

public class MovableObject extends GameObject {
	
	static {
		GameMapManager.getInstance().getMap().addPostAddObjectProcessing(MovableObject::sortMovableGameObjectsLast);
	}
	
	/**
	 * Sort movable game objects to the end of the list, to make them drawn on top of other objects.
	 */
	private static Array<GameObject> sortMovableGameObjectsLast(Array<GameObject> objects) {
		int listSize = objects.size;
		for (int i = 0; i < listSize; i++) {
			GameObject object = objects.get(i);
			if (object instanceof MovableObject) {
				objects.removeIndex(i);
				listSize--;
				i--;
				objects.add(object);
			}
		}
		return objects;
	}
	
	public MovableObject(GameObjectTypeConfig typeConfig, Sprite sprite, MapProperties mapProperties) {
		super(typeConfig, sprite, mapProperties);
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
