package net.jfabricationgames.gdx.character.ai.implementation;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;

import net.jfabricationgames.gdx.character.ai.AbstractArtificialIntelligence;
import net.jfabricationgames.gdx.character.ai.ArtificialIntelligence;
import net.jfabricationgames.gdx.character.ai.util.timer.AttackTimer;
import net.jfabricationgames.gdx.character.player.PlayableCharacter;
import net.jfabricationgames.gdx.character.state.CharacterState;

public abstract class AbstractAttackAI extends AbstractArtificialIntelligence {
	
	protected CharacterState attackState;
	protected PlayableCharacter targetingPlayer;
	protected AttackTimer attackTimer;
	
	protected float timeTillNextAttack;
	protected float timeSinceLastAttack;
	
	/** The distance till which the enemy follows the player (to not push him if to near) */
	protected float minDistanceToTargetPlayer = 1f;
	
	public AbstractAttackAI(ArtificialIntelligence subAI, CharacterState attackState, AttackTimer attackTimer) {
		super(subAI);
		this.attackState = attackState;
		this.attackTimer = attackTimer;
		
		updateTimeTillNextAttack();
		timeSinceLastAttack = timeTillNextAttack;
	}
	
	protected boolean changeToAttackState() {
		if (timeToAttack() && targetAlive()) {
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

	private boolean targetAlive() {
		return targetingPlayer != null && targetingPlayer.isAlive();
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
