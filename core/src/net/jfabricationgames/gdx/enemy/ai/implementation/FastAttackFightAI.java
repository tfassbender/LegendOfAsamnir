package net.jfabricationgames.gdx.enemy.ai.implementation;

import com.badlogic.gdx.math.Vector2;

import net.jfabricationgames.gdx.enemy.ai.ArtificialIntelligence;
import net.jfabricationgames.gdx.enemy.ai.util.AttackTimer;
import net.jfabricationgames.gdx.enemy.state.EnemyState;

public class FastAttackFightAI extends FightAI {
	
	private float attackSpeedFactor;
	private float attackSpeedDelay;
	private float timeSinceAttackStateEntered;
	
	public FastAttackFightAI(ArtificialIntelligence subAI, EnemyState attackState, AttackTimer attackTimer, float attackDistance,
			float attackSpeedFactor, float attackSpeedDelay) {
		super(subAI, attackState, attackTimer, attackDistance);
		this.attackSpeedFactor = attackSpeedFactor;
		this.attackSpeedDelay = attackSpeedDelay;
	}
	
	@Override
	public void calculateMove(float delta) {
		super.calculateMove(delta);
		if (inAttackState()) {
			timeSinceAttackStateEntered += delta;
		}
	}
	
	@Override
	protected boolean changeToAttackState() {
		boolean changePossible = super.changeToAttackState();
		if (changePossible) {
			timeSinceAttackStateEntered = 0;
		}
		return changePossible;
	}
	
	@Override
	protected void attackMoveTo(Vector2 targetPosition) {
		if (timeSinceAttackStateEntered >= attackSpeedDelay) {
			enemy.moveTo(targetPosition, attackSpeedFactor);
		}
		else {
			super.attackMoveTo(targetPosition);
		}
	}
}
