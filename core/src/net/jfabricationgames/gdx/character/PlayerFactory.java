package net.jfabricationgames.gdx.character;

import net.jfabricationgames.gdx.character.implementation.Dwarf;

public abstract class PlayerFactory {
	
	public static PlayableCharacter createPlayer() {
		return new Dwarf();
	}
}
