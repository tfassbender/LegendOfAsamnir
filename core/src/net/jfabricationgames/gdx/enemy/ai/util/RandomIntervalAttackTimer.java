package net.jfabricationgames.gdx.enemy.ai.util;

import java.util.Random;

public class RandomIntervalAttackTimer implements AttackTimer {
	
	private float minTimeBetweenAttacks;
	private float maxTimeBetweenAttacks;
	
	private Random random;
	
	public RandomIntervalAttackTimer(float minTimeBetweenAttacks, float maxTimeBetweenAttacks) {
		this.minTimeBetweenAttacks = minTimeBetweenAttacks;
		this.maxTimeBetweenAttacks = maxTimeBetweenAttacks;
		
		random = new Random();
	}
	
	@Override
	public float getTimeTillNextAttack() {
		return minTimeBetweenAttacks + ((maxTimeBetweenAttacks - minTimeBetweenAttacks) * random.nextFloat());
	}
}
