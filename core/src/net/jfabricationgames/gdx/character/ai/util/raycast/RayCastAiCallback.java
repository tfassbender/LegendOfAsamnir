package net.jfabricationgames.gdx.character.ai.util.raycast;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;

import net.jfabricationgames.gdx.character.player.PlayableCharacter;
import net.jfabricationgames.gdx.item.Item;

public class RayCastAiCallback implements RayCastCallback {
	
	private static final float RAYCAST_CONTINUE_QUERYING = -1;
	
	private boolean canSeeTarget = true;
	private PlayableCharacter targetToFollow;
	
	/**
	 * Needs to be called before calling the rayCast method (using this object as callback).
	 */
	public void reset() {
		canSeeTarget = true;
	}
	
	public void setTarget(PlayableCharacter target) {
		targetToFollow = target;
	}
	
	public boolean isTargetVisible() {
		return canSeeTarget;
	}
	
	@Override
	public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
		canSeeTarget &= canSeeThroughFixture(fixture);
		
		return RAYCAST_CONTINUE_QUERYING;
	}
	
	private boolean canSeeThroughFixture(Fixture fixture) {
		return fixture.isSensor() || isTargetFraction(fixture) || isItemFixture(fixture);
	}
	
	private boolean isTargetFraction(Fixture fixture) {
		return fixture.getBody().getUserData() == targetToFollow;
	}
	
	private boolean isItemFixture(Fixture fixture) {
		return fixture.getBody().getUserData() instanceof Item;
	}
}
