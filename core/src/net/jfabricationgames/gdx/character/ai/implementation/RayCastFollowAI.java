package net.jfabricationgames.gdx.character.ai.implementation;

import net.jfabricationgames.gdx.character.ai.ArtificialIntelligence;
import net.jfabricationgames.gdx.character.ai.move.AIPositionChangingMove;
import net.jfabricationgames.gdx.character.ai.move.MoveType;
import net.jfabricationgames.gdx.character.ai.util.raycast.RayCastAiCallback;
import net.jfabricationgames.gdx.character.state.CharacterState;
import net.jfabricationgames.gdx.physics.PhysicsWorld;

public class RayCastFollowAI extends FollowAI {
	
	private RayCastAiCallback rayCastCallback = new RayCastAiCallback();
	
	public RayCastFollowAI(ArtificialIntelligence subAI, CharacterState movingState, CharacterState idleState) {
		super(subAI, movingState, idleState);
	}
	
	@Override
	public void calculateMove(float delta) {
		subAI.calculateMove(delta);
		
		if (targetToFollow != null) {
			AIPositionChangingMove move = new AIPositionChangingMove(this);
			if (distanceToTarget() > minDistanceToTarget) {
				if (canSeeTarget()) {
					move.movementTarget = targetToFollow.getPosition();
				}
			}
			setMove(MoveType.MOVE, move);
		}
	}
	
	private boolean canSeeTarget() {
		rayCastCallback.reset();
		rayCastCallback.setTarget(targetToFollow);
		PhysicsWorld.getInstance().rayCast(rayCastCallback, character.getPosition(), targetToFollow.getPosition());
		
		//the canSeeTarget field will be set in the reportRayFixture method of the rayCastCallback
		return rayCastCallback.isTargetVisible();
	}
}
