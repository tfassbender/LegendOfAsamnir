package net.jfabricationgames.gdx.object;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.MapProperties;

public abstract class DestroyableObject extends GameObject {
	
	protected float health;
	protected boolean destroyed;
	
	protected String destroySound;
	
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
			playHitSound();
		}
	}
	
	public void destroy() {
		destroyed = true;
		//TODO destroy animation before removing
		playDestroySound();
		remove();
	}
	
	private void playDestroySound() {
		if (destroySound != null) {
			soundSet.playSound(destroySound);
		}
	}
}
