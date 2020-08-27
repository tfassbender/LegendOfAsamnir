package net.jfabricationgames.gdx.enemy.ai.implementation;

import net.jfabricationgames.gdx.enemy.ai.AbstractArtificialIntelligence;
import net.jfabricationgames.gdx.enemy.ai.ArtificialIntelligence;
import net.jfabricationgames.gdx.enemy.state.EnemyState;

public abstract class AbstractMovementAI extends AbstractArtificialIntelligence implements ArtificialIntelligence {
	
	protected EnemyState movingState;
	
	public AbstractMovementAI(ArtificialIntelligence subAI, EnemyState movingState) {
		super(subAI);
		this.movingState = movingState;
	}
	
	protected boolean inMovingState() {
		return stateMachine.getCurrentState() == movingState;
	}
	protected boolean changeToMovingState() {
		return stateMachine.setState(movingState);
	}
}
