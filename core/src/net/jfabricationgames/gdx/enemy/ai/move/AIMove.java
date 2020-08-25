package net.jfabricationgames.gdx.enemy.ai.move;

import net.jfabricationgames.gdx.enemy.ai.ArtificialIntelligence;

public abstract class AIMove {
	
	private MoveType moveType;
	private boolean executed;
	private ArtificialIntelligence creatingAi;
	
	public AIMove(ArtificialIntelligence creatingAi) {
		this.creatingAi = creatingAi;
	}
	
	public MoveType getMoveType() {
		return moveType;
	}
	public void setMoveType(MoveType moveType) {
		this.moveType = moveType;
	}
	
	public boolean isExecuted() {
		return executed;
	}
	public void setMoveExecuted() {
		executed = true;
	}
	
	public boolean isCreatingAi(ArtificialIntelligence executingAi) {
		return creatingAi == executingAi;
	}
}
