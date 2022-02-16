package net.jfabricationgames.gdx.character.ai.implementation;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;

import net.jfabricationgames.gdx.character.ai.ArtificialIntelligence;
import net.jfabricationgames.gdx.character.ai.move.AIPositionChangingMove;
import net.jfabricationgames.gdx.character.ai.move.MoveType;
import net.jfabricationgames.gdx.character.state.CharacterState;

public class RandomMovementAI extends AbstractRelativeMovementAI {
	
	private static final float MAX_TIME_TO_REACH_TARGET = 5f;
	
	private Vector2 targetPosition;
	
	private float maxDistance;
	private float targetTime = 0f;
	
	private boolean changeTargetWhenStaticBodyHit;
	
	public RandomMovementAI(ArtificialIntelligence subAI, CharacterState movingState, CharacterState idleState, float maxDistance,
			float idleTimeBetweenMovements, boolean changeTargetWhenStaticBodyHit) {
		super(subAI, movingState, idleState, idleTimeBetweenMovements);
		this.maxDistance = maxDistance;
		this.changeTargetWhenStaticBodyHit = changeTargetWhenStaticBodyHit;
	}
	
	@Override
	public void calculateMove(float delta) {
		subAI.calculateMove(delta);
		
		if (!absolutePositionSet) {
			updateRelativeZero(character.getPosition());
		}
		
		if (maxDistance > 0) {
			AIPositionChangingMove move = createPositionChangeMove();
			setMove(MoveType.MOVE, move);
		}
	}
	
	protected AIPositionChangingMove createPositionChangeMove() {
		AIPositionChangingMove move = new AIPositionChangingMove(this);
		move.movementTarget = targetPosition;
		return move;
	}
	
	@Override
	public void executeMove(float delta) {
		if (waitBetweenMovements(delta)) {
			return;
		}
		
		AIPositionChangingMove move = getMove(MoveType.MOVE, AIPositionChangingMove.class);
		if (isExecutedByMe(move)) {
			Vector2 targetPoint = move.movementTarget;
			
			if (targetPoint == null) {
				calculateNextTargetPoint();
			}
			else if (!isMovementWouldLeadToNearToPlayer(targetPoint, delta)) {
				if (inMovingState() || changeToMovingState()) {
					if (reachedTargetPoint(targetPoint)) {
						calculateNextTargetPoint();
					}
					else {
						character.moveTo(targetPoint, movementSpeedFactor);
					}
					move.executed();
				}
			}
			
			targetTime += delta;
			if (isMovementTakingTooLong()) {
				calculateNextTargetPoint();
			}
		}
		
		subAI.executeMove(delta);
	}
	
	protected boolean isMovementTakingTooLong() {
		return targetTime > MAX_TIME_TO_REACH_TARGET;
	}
	
	protected void calculateNextTargetPoint() {
		if (relativeZero != null) {
			targetPosition = new Vector2(1, 1);//don't use a null-vector; otherwise setLength will not work
			targetPosition.setLength((float) (Math.random() * maxDistance));
			targetPosition.setAngleDeg((float) (Math.random() * 360f));
			targetPosition.add(relativeZero);
		}
		
		resetIdleTimeBetweenMovements();
		
		targetTime = 0;
	}
	
	protected boolean isTargetPositionSet() {
		return targetPosition != null;
	}
	
	protected void resetTargetPosition() {
		targetPosition = null;
	}
	
	@Override
	public void beginContact(Contact contact) {
		if (changeTargetWhenStaticBodyHit) {
			if (isCharacterInvolved(contact) && isStaticBodyInvolved(contact)) {
				calculateNextTargetPoint();
			}
		}
		
		super.beginContact(contact);
	}
	
	private boolean isCharacterInvolved(Contact contact) {
		return contact.getFixtureA().getBody().getUserData() == character || contact.getFixtureB().getBody().getUserData() == character;
	}
	
	private boolean isStaticBodyInvolved(Contact contact) {
		return contact.getFixtureA().getBody().getType() == BodyType.StaticBody || contact.getFixtureB().getBody().getType() == BodyType.StaticBody;
	}
}
