package net.jfabricationgames.gdx.enemy;

public class EnemyTypeConfig {
	
	public String animationsConfig;//the animation config file that is to be loaded (by the factory)
	public String stateConfig;//the state config file that is to be loaded (by the EnemyStateMachine)
	public String initialState;
	public String attackConfig;
	
	public float health;
	public float movingSpeed;
	
	public float pushForceDamage;//the force that is applied to the enemy when he takes damage (multiplied with the body mass times 10)
	public float pushForceHit;//the force that is applied to the player when the enemy hits him (multiplied with the body mass times 10)
}
