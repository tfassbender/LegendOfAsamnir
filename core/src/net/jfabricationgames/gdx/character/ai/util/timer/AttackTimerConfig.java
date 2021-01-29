package net.jfabricationgames.gdx.character.ai.util.timer;

public class AttackTimerConfig {
	
	public enum Type {
		FIXED, //
		RANDOM_INTERVAL;
	}
	
	public Type type;
	
	public float fixedTime;
	public float minTimeBetweenAttacks;
	public float maxTimeBetweenAttacks;
}
