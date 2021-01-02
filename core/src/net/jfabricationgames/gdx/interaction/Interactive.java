package net.jfabricationgames.gdx.interaction;

import com.badlogic.gdx.physics.box2d.ContactListener;

import net.jfabricationgames.gdx.character.PlayableCharacter;

public interface Interactive extends ContactListener {
	
	public void interact();
	
	public float getDistanceToPlayer(PlayableCharacter character);
}
