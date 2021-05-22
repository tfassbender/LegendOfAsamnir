package net.jfabricationgames.gdx.character.ai.implementation;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;

import net.jfabricationgames.gdx.character.ai.ArtificialIntelligence;
import net.jfabricationgames.gdx.character.ai.move.AIPositionChangingMove;
import net.jfabricationgames.gdx.character.ai.move.MoveType;
import net.jfabricationgames.gdx.character.state.CharacterState;
import net.jfabricationgames.gdx.physics.PhysicsWorld;

public class RayCastFollowAI extends FollowAI {
	
	private RayCastFollowAiCallback rayCastCallback = new RayCastFollowAiCallback();
	
	private boolean canSeeTarget;
	
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
		PhysicsWorld.getInstance().rayCast(rayCastCallback, character.getPosition(), targetToFollow.getPosition());
		
		//the canSeeTarget field will be set in the reportRayFixture method of the rayCastCallback
		return canSeeTarget;
	}
	
	private class RayCastFollowAiCallback implements RayCastCallback {
		
		private static final float RAYCAST_STOP_QUERYING = 0;
		
		@Override
		public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
			canSeeTarget = fixture.getBody().getUserData() == targetToFollow;
			
			return RAYCAST_STOP_QUERYING;
		}
	}
}
