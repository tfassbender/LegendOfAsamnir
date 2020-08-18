package net.jfabricationgames.gdx.object;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.MapProperties;

import net.jfabricationgames.gdx.attributes.Hittable;

public abstract class DestroyableObject extends GameObject implements Hittable {
	
	protected float health;
	
	protected boolean destroyed;
	
	public DestroyableObject(ObjectType type, Sprite sprite, MapProperties properties) {
		super(type, sprite, properties);
		destroyed = false;
	}
	
	public void takeDamage(float damage) {
		health -= damage;
		
		if (health <= 0) {
			destroy();
		}
		else {
			//TODO animate hit			
		}
	}
	
	public void destroy() {
		destroyed = true;
		//TODO destroy animation before removing
		remove();
	}
}
