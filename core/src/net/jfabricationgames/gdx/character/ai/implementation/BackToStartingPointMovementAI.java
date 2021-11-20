package net.jfabricationgames.gdx.character.ai.implementation;

import com.badlogic.gdx.math.Vector2;

import net.jfabricationgames.gdx.character.ai.ArtificialIntelligence;
import net.jfabricationgames.gdx.character.state.CharacterState;

public class BackToStartingPointMovementAI extends PreDefinedMovementAI {
	
	public BackToStartingPointMovementAI(ArtificialIntelligence subAI, CharacterState movingState, CharacterState idleState,
			float idleTimeBetweenMovements) {
		super(subAI, movingState, idleState, true, idleTimeBetweenMovements, new Vector2(0, 0));
	}
}
