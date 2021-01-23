package net.jfabricationgames.gdx.character.ai.move;

import net.jfabricationgames.gdx.character.ai.ArtificialIntelligence;

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
