package net.jfabricationgames.gdx.character.player;

import net.jfabricationgames.gdx.character.player.implementation.Dwarf;

/**
 * Wrapper for singleton implementation of {@link Dwarf}
 */
public class Player {
	
	private static PlayableCharacter instance;
	
	public static synchronized PlayableCharacter getInstance() {
		if (instance == null) {
			instance = new Dwarf();
		}
		return instance;
	}
	
	private Player() {}
}
