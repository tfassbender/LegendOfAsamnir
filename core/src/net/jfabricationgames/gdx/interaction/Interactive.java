package net.jfabricationgames.gdx.interaction;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.ContactListener;

public interface Interactive extends ContactListener {
	
	public void interact();
	
	public boolean interactionCanBeExecuted();
	
	public float getDistanceToPlayer(Vector2 playerPosition);
}
