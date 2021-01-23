package net.jfabricationgames.gdx.character.ai.implementation;

import net.jfabricationgames.gdx.character.ai.AbstractArtificialIntelligence;
import net.jfabricationgames.gdx.character.ai.ArtificialIntelligence;
import net.jfabricationgames.gdx.character.state.CharacterState;

public abstract class AbstractMovementAI extends AbstractArtificialIntelligence implements ArtificialIntelligence {
	
	protected CharacterState movingState;
	protected CharacterState idleState;
	
	public AbstractMovementAI(ArtificialIntelligence subAI, CharacterState movingState, CharacterState idleState) {
		super(subAI);
		this.movingState = movingState;
		this.idleState = idleState;
	}
	
	protected boolean inMovingState() {
		return stateMachine.getCurrentState() == movingState;
	}
	protected boolean changeToMovingState() {
		return stateMachine.setState(movingState);
	}
	
	protected boolean inIdleState() {
		return stateMachine.getCurrentState() == idleState;
	}
	protected boolean changeToIdleState() {
		return stateMachine.setState(idleState);
	}
}
