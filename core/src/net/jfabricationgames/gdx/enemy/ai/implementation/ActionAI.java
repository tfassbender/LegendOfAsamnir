package net.jfabricationgames.gdx.enemy.ai.implementation;

import com.badlogic.gdx.physics.box2d.Contact;

import net.jfabricationgames.gdx.character.PlayableCharacter;
import net.jfabricationgames.gdx.enemy.ai.AbstractArtificialIntelligence;
import net.jfabricationgames.gdx.enemy.ai.ArtificialIntelligence;
import net.jfabricationgames.gdx.enemy.ai.move.AIActionMove;
import net.jfabricationgames.gdx.enemy.ai.move.MoveType;
import net.jfabricationgames.gdx.enemy.state.EnemyState;

public class ActionAI extends AbstractArtificialIntelligence {
	
	protected EnemyState actionState;
	protected PlayableCharacter targetingPlayer;
	
	protected float minDistToEnemy;
	protected float maxDistToEnemy;
	
	protected float timeBetweenActions;
	protected float timeTillLastAction;
	
	public ActionAI(ArtificialIntelligence subAI, EnemyState actionState, float minDistToEnemy, float maxDistToEnemy, float timeBetweenActions) {
		super(subAI);
		this.actionState = actionState;
		this.minDistToEnemy = minDistToEnemy;
		this.maxDistToEnemy = maxDistToEnemy;
		this.timeBetweenActions = timeBetweenActions;
	}
	
	@Override
	public void calculateMove(float delta) {
		subAI.calculateMove(delta);
		
		if (!inActionState()) {
			timeTillLastAction += delta;
		}
		
		if (targetInRange()) {
			AIActionMove move = new AIActionMove(this);
			setMove(MoveType.ATTACK, move);
		}
	}
	
	protected boolean inActionState() {
		return stateMachine.getCurrentState() == actionState;
	}
	
	protected boolean targetInRange() {
		if (targetingPlayer == null) {
			return false;
		}
		
		float distanceToTargetingPlayer = targetingPlayer.getPosition().cpy().sub(enemy.getPosition()).len();
		return distanceToTargetingPlayer >= minDistToEnemy && distanceToTargetingPlayer <= maxDistToEnemy;
	}
	
	@Override
	public void executeMove() {
		AIActionMove move = getMove(MoveType.ATTACK, AIActionMove.class);
		if (isExecutedByMe(move)) {
			if (changeToActionState()) {
				move.executed();
			}
		}
		
		subAI.executeMove();
	}
	
	protected boolean changeToActionState() {
		if (timeTillLastAction >= timeBetweenActions) {
			boolean changedState = stateMachine.setState(actionState);
			if (changedState) {
				timeTillLastAction = 0;
			}
			return changedState;
		}
		return false;
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
}
