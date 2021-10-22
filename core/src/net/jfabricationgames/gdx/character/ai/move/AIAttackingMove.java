package net.jfabricationgames.gdx.character.ai.move;

import com.badlogic.gdx.math.Vector2;

import net.jfabricationgames.gdx.character.state.CharacterState;

public class AIAttackingMove extends AIMove {
	
	public AIAttackingMove(Object creatingAi) {
		super(creatingAi);
	}
	
	public Vector2 targetPosition;
	public CharacterState attack;
}
