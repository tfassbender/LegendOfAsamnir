package net.jfabricationgames.gdx.enemy.state;

public interface EnemyStateListener {
	
	public void enteringState(EnemyState state);
	
	public void leavingState(EnemyState state);
}
