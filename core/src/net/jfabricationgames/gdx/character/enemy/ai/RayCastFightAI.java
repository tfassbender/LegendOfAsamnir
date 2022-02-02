package net.jfabricationgames.gdx.character.enemy.ai;

import net.jfabricationgames.gdx.character.ai.ArtificialIntelligence;
import net.jfabricationgames.gdx.character.ai.util.raycast.RayCastAiCallback;
import net.jfabricationgames.gdx.character.ai.util.timer.AttackTimer;
import net.jfabricationgames.gdx.character.state.CharacterState;
import net.jfabricationgames.gdx.physics.PhysicsWorld;

public class RayCastFightAI extends FightAI {
	
	private RayCastAiCallback rayCastCallback = new RayCastAiCallback();
	
	public RayCastFightAI(ArtificialIntelligence subAI, CharacterState attackState, AttackTimer attackTimer, float attackDistance) {
		super(subAI, attackState, attackTimer, attackDistance);
	}
	
	@Override
	protected boolean canSeeTarget() {
		rayCastCallback.reset();
		rayCastCallback.setTarget(targetingPlayer);
		PhysicsWorld.getInstance().rayCast(rayCastCallback, character.getPosition(), targetingPlayer.getPosition());
		
		//the canSeeTarget field will be set in the reportRayFixture method of the rayCastCallback
		return rayCastCallback.isTargetVisible();
	}
}
