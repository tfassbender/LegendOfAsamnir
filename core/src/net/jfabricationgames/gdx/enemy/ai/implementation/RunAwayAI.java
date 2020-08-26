package net.jfabricationgames.gdx.enemy.ai.implementation;

import com.badlogic.gdx.physics.box2d.Contact;

import net.jfabricationgames.gdx.character.PlayableCharacter;
import net.jfabricationgames.gdx.enemy.ai.AbstractArtificialIntelligence;
import net.jfabricationgames.gdx.enemy.ai.ArtificialIntelligence;
import net.jfabricationgames.gdx.enemy.ai.move.AIPositionChangingMove;
import net.jfabricationgames.gdx.enemy.ai.move.MoveType;

public class RunAwayAI extends AbstractArtificialIntelligence implements ArtificialIntelligence {
	
	private PlayableCharacter player;
	
	private float distanceToKeepFromPlayer = 5f;
	private float distanceToStopRunning = 2f;
	
	public RunAwayAI(ArtificialIntelligence subAI) {
		super(subAI);
	}
	
	@Override
	public void calculateMove(float delta) {
		subAI.calculateMove(delta);
		
		if (player != null) {
			float distanceToPlayer = enemy.getPosition().sub(player.getPosition()).len();
			if (distanceToPlayer < distanceToKeepFromPlayer && distanceToPlayer > distanceToStopRunning) {
				AIPositionChangingMove move = new AIPositionChangingMove(this);
				move.movementDirection = enemy.getPosition().sub(player.getPosition());
				setMove(MoveType.MOVE, move);
			}
		}
	}
	
	@Override
	public void executeMove() {
		AIPositionChangingMove move = getMove(MoveType.MOVE, AIPositionChangingMove.class);
		if (isExecutedByMe(move)) {
			enemy.moveToDirection(move.movementDirection);
			move.executed();
		}
		
		subAI.executeMove();
	}
	
	@Override
	public void beginContact(Contact contact) {
		PlayableCharacter collidingPlayer = getObjectCollidingWithEnemySensor(contact, PlayableCharacter.class);
		// if the sensor touches a PlayableCharacter -> start following him
		if (collidingPlayer != null) {
			runFromPlayer(collidingPlayer);
		}
		
		subAI.beginContact(contact);
	}
	
	@Override
	public void endContact(Contact contact) {
		PlayableCharacter collidingPlayer = getObjectCollidingWithEnemySensor(contact, PlayableCharacter.class);
		if (collidingPlayer != null) {
			stopRunningPlayer();
		}
		
		subAI.endContact(contact);
	}
	
	private void runFromPlayer(PlayableCharacter player) {
		this.player = player;
	}
	
	private void stopRunningPlayer() {
		player = null;
	}
}
