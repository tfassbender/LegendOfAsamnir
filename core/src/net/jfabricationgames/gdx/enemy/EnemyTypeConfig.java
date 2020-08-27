package net.jfabricationgames.gdx.enemy;

public class EnemyTypeConfig {
	
	public String animationsConfig;//the animation config file that is to be loaded (by the factory)
	public String stateConfig;//the state config file that is to be loaded (by the EnemyStateMachine)
	public String initialState;
	
	public float health;
	public float movingSpeed;
}
