package net.jfabricationgames.gdx.character.ai.implementation;

import net.jfabricationgames.gdx.character.ai.AbstractArtificialIntelligence;
import net.jfabricationgames.gdx.character.ai.ArtificialIntelligence;
import net.jfabricationgames.gdx.character.state.CharacterState;

public abstract class AbstractMovementAI extends AbstractArtificialIntelligence {
	
	protected CharacterState movingState;
	protected CharacterState idleState;
	
	protected float movementSpeedFactor = 1f;
	
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
	
	public void setMovementSpeedFactor(float movementSpeedFactor) {
		this.movementSpeedFactor = movementSpeedFactor;
	}
}
