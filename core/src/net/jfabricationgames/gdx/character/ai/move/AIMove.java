package net.jfabricationgames.gdx.character.ai.move;

public abstract class AIMove {
	
	private boolean executed;
	private Object creatingAi;
	
	public AIMove(Object creatingAi) {
		this.creatingAi = creatingAi;
	}
	
	public boolean isExecuted() {
		return executed;
	}
	public void executed() {
		executed = true;
	}
	
	public boolean isCreatingAi(Object executingAi) {
		return creatingAi == executingAi;
	}
}
