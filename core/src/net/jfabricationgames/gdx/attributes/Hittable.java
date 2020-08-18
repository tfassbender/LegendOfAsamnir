package net.jfabricationgames.gdx.attributes;

/**
 * Indicates that the implementing class' objects can be hit and can take damage.
 */
public interface Hittable {
	
	public void takeDamage(float damage);
}
