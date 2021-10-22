package net.jfabricationgames.gdx.character.state;

import com.badlogic.gdx.math.Vector2;

public interface CharacterStateAttackHandler {
	
	public CharacterStateAttack startAttack(String attack, Vector2 directionToTarget);
}
