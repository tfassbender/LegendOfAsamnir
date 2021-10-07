package net.jfabricationgames.gdx.character.state;

import com.badlogic.gdx.math.Vector2;

import net.jfabricationgames.gdx.attack.Attack;

public interface CharacterStateAttackHandler {
	
	public Attack startAttack(String attack, Vector2 directionToTarget);
}
