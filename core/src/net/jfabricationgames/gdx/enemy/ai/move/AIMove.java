package net.jfabricationgames.gdx.enemy.ai.move;

import net.jfabricationgames.gdx.enemy.ai.ArtificialIntelligence;

public abstract class AIMove {
	
	private boolean executed;
	private ArtificialIntelligence creatingAi;
	
	public AIMove(ArtificialIntelligence creatingAi) {
		this.creatingAi = creatingAi;
	}
	
	public boolean isExecuted() {
		return executed;
	}
	public void executed() {
		executed = true;
	}
	
	public boolean isCreatingAi(ArtificialIntelligence executingAi) {
		return creatingAi == executingAi;
	}
}
