package net.jfabricationgames.gdx.enemy.ai.implementation;

import net.jfabricationgames.gdx.enemy.ai.AbstractArtificialIntelligence;
import net.jfabricationgames.gdx.enemy.ai.ArtificialIntelligence;
import net.jfabricationgames.gdx.enemy.state.EnemyState;

public abstract class AbstractMovementAI extends AbstractArtificialIntelligence implements ArtificialIntelligence {
	
	protected EnemyState movingState;
	protected EnemyState idleState;
	
	public AbstractMovementAI(ArtificialIntelligence subAI, EnemyState movingState, EnemyState idleState) {
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
