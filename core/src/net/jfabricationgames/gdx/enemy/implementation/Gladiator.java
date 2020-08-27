package net.jfabricationgames.gdx.enemy.implementation;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import net.jfabricationgames.gdx.enemy.Enemy;
import net.jfabricationgames.gdx.enemy.EnemyPhysicsUtil;
import net.jfabricationgames.gdx.enemy.EnemyTypeConfig;
import net.jfabricationgames.gdx.enemy.ai.BaseAI;
import net.jfabricationgames.gdx.enemy.ai.implementation.FollowAI;
import net.jfabricationgames.gdx.enemy.ai.implementation.PreDefinedMovementAI;
import net.jfabricationgames.gdx.enemy.state.EnemyState;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyProperties;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyShape;

public class Gladiator extends Enemy {
	
	public Gladiator(EnemyTypeConfig typeConfig, MapProperties properties) {
		super(typeConfig, properties);
	}
	
	@Override
	protected PhysicsBodyProperties definePhysicsBodyProperties() {
		return getDefaultPhysicsBodyProperties().setRadius(0.3f).setDensity(10f).setLinearDamping(10f).setPhysicsBodyShape(PhysicsBodyShape.CIRCLE);
	}
	
	@Override
	protected void addAdditionalPhysicsParts() {
		EnemyPhysicsUtil.addSensor(body, 4f);
	}
	
	@Override
	protected void createAI() {
		Array<Vector2> positions = loadPositionsFromMapProperties();
		EnemyState movingState = stateMachine.getState("move");
		
		ai = new BaseAI();
		ai = new PreDefinedMovementAI(ai, movingState, true, positions);
		ai = new FollowAI(ai, movingState);
	}
}
