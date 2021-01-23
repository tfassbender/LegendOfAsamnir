package net.jfabricationgames.gdx.character.player;

import net.jfabricationgames.gdx.character.player.implementation.Dwarf;

public abstract class PlayerFactory {
	
	public static PlayableCharacter createPlayer() {
		return new Dwarf();
	}
}
