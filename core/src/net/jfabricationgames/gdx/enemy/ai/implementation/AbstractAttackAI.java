package net.jfabricationgames.gdx.enemy.ai.implementation;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;

import net.jfabricationgames.gdx.character.PlayableCharacter;
import net.jfabricationgames.gdx.enemy.ai.AbstractArtificialIntelligence;
import net.jfabricationgames.gdx.enemy.ai.ArtificialIntelligence;
import net.jfabricationgames.gdx.enemy.ai.util.AttackTimer;
import net.jfabricationgames.gdx.enemy.state.EnemyState;

public abstract class AbstractAttackAI extends AbstractArtificialIntelligence implements ArtificialIntelligence {
	
	protected EnemyState attackState;
	protected PlayableCharacter targetingPlayer;
	protected AttackTimer attackTimer;
	
	protected float timeTillNextAttack;
	protected float timeSinceLastAttack;
	
	/** The distance till which the enemy follows the player (to not push him if to near) */
	protected float minDistanceToTargetPlayer = 1f;
	
	public AbstractAttackAI(ArtificialIntelligence subAI, EnemyState attackState, AttackTimer attackTimer) {
		super(subAI);
		this.attackState = attackState;
		this.attackTimer = attackTimer;
		
		updateTimeTillNextAttack();
		timeSinceLastAttack = timeTillNextAttack;
	}
	
	protected boolean changeToAttackState() {
		if (timeToAttack()) {
			attackState.setAttackDirection(directionToTarget());
			boolean changedState = stateMachine.setState(attackState);
			if (changedState) {
				timeSinceLastAttack = 0;
				updateTimeTillNextAttack();
			}
			
			return changedState;
		}
		return false;
	}
	
	protected void updateTimeTillNextAttack() {
		timeTillNextAttack = attackTimer.getTimeTillNextAttack();
	}
	
	protected boolean timeToAttack() {
		return timeSinceLastAttack >= timeTillNextAttack;
	}
	
	protected boolean inAttackState() {
		return stateMachine.getCurrentState() == attackState;
	}
	
	protected boolean targetInRange(float attackDistance) {
		return targetingPlayer != null && targetingPlayer.getPosition().cpy().sub(enemy.getPosition()).len() <= attackDistance;
	}
	
	protected Vector2 directionToTarget() {
		if (targetingPlayer == null) {
			return Vector2.Zero;
		}
		return targetingPlayer.getPosition().sub(enemy.getPosition());
	}
	
	protected float distanceToTarget() {
		if (targetingPlayer == null) {
			return Float.MAX_VALUE;
		}
		return enemy.getPosition().sub(targetingPlayer.getPosition()).len();
	}
	
	@Override
	public void calculateMove(float delta) {
		if (!inAttackState()) {
			timeSinceLastAttack += delta;
		}
	}
	
	@Override
	public void beginContact(Contact contact) {
		PlayableCharacter collidingPlayer = getObjectCollidingWithEnemySensor(contact, PlayableCharacter.class);
		if (collidingPlayer != null) {
			targetingPlayer = collidingPlayer;
		}
		
		subAI.beginContact(contact);
	}
	
	@Override
	public void endContact(Contact contact) {
		PlayableCharacter collidingPlayer = getObjectCollidingWithEnemySensor(contact, PlayableCharacter.class);
		if (collidingPlayer != null) {
			targetingPlayer = null;
		}
		
		subAI.endContact(contact);
	}
	
	public void setMinDistanceToTargetPlayer(float minDistanceToTargetPlayer) {
		this.minDistanceToTargetPlayer = minDistanceToTargetPlayer;
	}
}
