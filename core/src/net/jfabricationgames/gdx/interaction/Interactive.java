package net.jfabricationgames.gdx.interaction;

import com.badlogic.gdx.physics.box2d.ContactListener;

import net.jfabricationgames.gdx.character.PlayableCharacter;

public interface Interactive extends ContactListener {
	
	/**
	 * Execute the interaction.
	 */
	public void interact();
	
	/**
	 * Get the distance to the playable character to determine which interactive object will be executed (the nearest).
	 * 
	 * @param character The playable character that executes an interaction.
	 * 
	 * @return The distance to the playable character.
	 */
	public float getDistanceFromDwarf(PlayableCharacter character);
}
