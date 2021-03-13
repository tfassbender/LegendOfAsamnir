package net.jfabricationgames.gdx.interaction;

import com.badlogic.gdx.physics.box2d.ContactListener;

import net.jfabricationgames.gdx.character.player.PlayableCharacter;

public interface Interactive extends ContactListener {
	
	public void interact();
	
	public boolean interactionCanBeExecuted();
	
	public float getDistanceToPlayer(PlayableCharacter character);
}
