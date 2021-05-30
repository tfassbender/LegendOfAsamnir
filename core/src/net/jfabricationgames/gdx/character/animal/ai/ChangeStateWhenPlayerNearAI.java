package net.jfabricationgames.gdx.character.animal.ai;

import com.badlogic.gdx.physics.box2d.Contact;

import net.jfabricationgames.gdx.character.ai.AbstractArtificialIntelligence;
import net.jfabricationgames.gdx.character.ai.ArtificialIntelligence;
import net.jfabricationgames.gdx.character.ai.move.AIActionMove;
import net.jfabricationgames.gdx.character.ai.move.AIMove;
import net.jfabricationgames.gdx.character.ai.move.MoveType;
import net.jfabricationgames.gdx.character.player.PlayableCharacter;
import net.jfabricationgames.gdx.character.state.CharacterState;

public class ChangeStateWhenPlayerNearAI extends AbstractArtificialIntelligence {
	
	private CharacterState playerNearState;
	private CharacterState playerLeavingState;
	private float distanceToChangeState;
	
	private PlayableCharacter player;
	
	public ChangeStateWhenPlayerNearAI(ArtificialIntelligence subAI, CharacterState playerNearState, CharacterState playerLeavingState,
			float distanceToChangeState) {
		super(subAI);
		this.playerNearState = playerNearState;
		this.playerLeavingState = playerLeavingState;
		this.distanceToChangeState = distanceToChangeState;
	}
	
	@Override
	public void calculateMove(float delta) {
		subAI.calculateMove(delta);
		
		if (player != null) {
			float distanceToPlayer = getDistanceToPlayer();
			if (distanceToPlayer < distanceToChangeState) {
				AIMove move = new AIActionMove(this);
				setMove(MoveType.MOVE, move);//use a MoveType MOVE to override other movement types
			}
		}
		if (inPlayerNearState()) {
			if (isChangeToPlayerLeavingState()) {
				AIMove move = new AIActionMove(this);
				setMove(MoveType.MOVE, move);//use a MoveType MOVE to override other movement types
			}
		}
	}
	
	private float getDistanceToPlayer() {
		return character.getPosition().sub(player.getPosition()).len();
	}
	
	private boolean isChangeToPlayerLeavingState() {
		if (playerLeavingState == null) {
			return false;
		}
		if (player == null) {
			return true;
		}
		
		float distanceToPlayer = getDistanceToPlayer();
		
		return distanceToPlayer > distanceToChangeState;
	}
	
	@Override
	public void executeMove(float delta) {
		AIActionMove move = getMove(MoveType.MOVE, AIActionMove.class);
		if (isExecutedByMe(move)) {
			if (!inPlayerNearState()) {
				changeToPlayerNearState();
				move.executed();
			}
			else if (isChangeToPlayerLeavingState()) {
				changeToPlayerLeavingState();
				move.executed();
			}
		}
		
		subAI.executeMove(delta);
	}
	
	private boolean inPlayerNearState() {
		return stateMachine.getCurrentState() == playerNearState;
	}
	private boolean changeToPlayerNearState() {
		return stateMachine.setState(playerNearState);
	}
	
	private boolean changeToPlayerLeavingState() {
		return stateMachine.setState(playerLeavingState);
	}
	
	@Override
	public void beginContact(Contact contact) {
		PlayableCharacter collidingCharacter = getObjectCollidingWithEnemySensor(contact, PlayableCharacter.class);
		// if the sensor touches a PlayableCharacter -> start following him
		if (collidingCharacter != null) {
			player = collidingCharacter;
		}
		
		subAI.beginContact(contact);
	}
	
	@Override
	public void endContact(Contact contact) {
		PlayableCharacter collidingCharacter = getObjectCollidingWithEnemySensor(contact, PlayableCharacter.class);
		if (collidingCharacter != null) {
			player = null;
		}
		
		subAI.endContact(contact);
	}
}
