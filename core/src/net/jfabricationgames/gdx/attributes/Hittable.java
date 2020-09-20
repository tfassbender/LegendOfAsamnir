package net.jfabricationgames.gdx.attributes;

import com.badlogic.gdx.math.Vector2;

/**
 * Indicates that the implementing class' objects can be hit and can take damage.
 */
public interface Hittable {
	
	public void takeDamage(float damage);
	
	public void pushByHit(Vector2 hitCenter, float force, boolean blockAffected);
	
	public default Vector2 getPushDirection(Vector2 position, Vector2 hitCenter) {
		return position.cpy().sub(hitCenter).nor();
	}
}
