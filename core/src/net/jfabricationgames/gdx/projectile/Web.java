package net.jfabricationgames.gdx.projectile;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Fixture;

import net.jfabricationgames.gdx.animation.AnimationDirector;
import net.jfabricationgames.gdx.character.CharacterPhysicsUtil;
import net.jfabricationgames.gdx.map.GameMapGroundType;
import net.jfabricationgames.gdx.map.GameMapGroundTypeContainer;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyProperties;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyShape;
import net.jfabricationgames.gdx.physics.PhysicsCollisionType;

public class Web extends Projectile implements GameMapGroundTypeContainer {
	
	private static final String GROUND_TYPE_WEB = "web";
	
	private boolean touchingPlayer;
	
	public Web(ProjectileTypeConfig typeConfig, AnimationDirector<TextureRegion> animation, ProjectileMap gameMap) {
		super(typeConfig, animation, gameMap);
		setImageOffset(0f, 0.5f);
	}
	
	@Override
	protected PhysicsBodyProperties createShapePhysicsBodyProperties() {
		return new PhysicsBodyProperties().setPhysicsBodyShape(PhysicsBodyShape.CIRCLE).setRadius(0.1f);
	}
	
	@Override
	protected void addAdditionalPhysicsParts() {
		CharacterPhysicsUtil.addCircularGroundFixture(body, 1f, GROUND_TYPE_WEB);
	}
	
	@Override
	protected void stopProjectileAfterObjectHit() {
		super.stopProjectileAfterObjectHit();
		changeBodyToSensor();
	}
	
	@Override
	protected void changeBodyToSensor() {
		if (hasBody()) {
			for (Fixture fixture : body.getFixtureList()) {
				if (!isMapGroundFixture(fixture)) {
					fixture.setSensor(true);
				}
			}
		}
	}
	
	private boolean isMapGroundFixture(Fixture fixture) {
		return fixture.getFilterData().categoryBits == PhysicsCollisionType.MAP_GROUND.category;
	}
	
	@Override
	public void remove() {
		if (!touchingPlayer) {
			super.remove();
		}
	}
	
	@Override
	public GameMapGroundType getGameMapGroundType() {
		return gameMap.getGroundTypeByName(GROUND_TYPE_WEB);
	}
}
