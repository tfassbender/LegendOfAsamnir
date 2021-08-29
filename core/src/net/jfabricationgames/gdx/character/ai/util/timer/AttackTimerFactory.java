package net.jfabricationgames.gdx.character.ai.util.timer;

public class AttackTimerFactory {
	
	private AttackTimerFactory() {}
	
	public static AttackTimer createAttackTimer(AttackTimerConfig config) {
		switch (config.type) {
			case FIXED:
				return new FixedAttackTimer(config.fixedTime);
			case RANDOM_INTERVAL:
				return new RandomIntervalAttackTimer(config.minTimeBetweenAttacks, config.maxTimeBetweenAttacks);
			default:
				throw new IllegalStateException("Unexpected attack timer type: " + config.type);
		}
	}
}
