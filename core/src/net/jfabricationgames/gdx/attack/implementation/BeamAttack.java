package net.jfabricationgames.gdx.attack.implementation;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;

import net.jfabricationgames.gdx.animation.AnimationDirector;
import net.jfabricationgames.gdx.animation.AnimationManager;
import net.jfabricationgames.gdx.attack.Attack;
import net.jfabricationgames.gdx.attack.AttackConfig;
import net.jfabricationgames.gdx.attack.AttackType;
import net.jfabricationgames.gdx.attack.Hittable;
import net.jfabricationgames.gdx.physics.CollisionUtil;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyProperties;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyShape;
import net.jfabricationgames.gdx.physics.PhysicsCollisionType;
import net.jfabricationgames.gdx.physics.PhysicsWorld;

/**
 * The cyclops' beam attack.
 */
public class BeamAttack extends Attack {
	
	private static final String CYCLOPS_BEAM_ANIMATION_NAME = "cyclops_beam";
	
	private static final float CYCLOPS_EYE_OFFSET_X = 0.1f;
	private static final float CYCLOPS_EYE_OFFSET_Y = 0.34f;
	private static final float CYCLOPS_BEAM_WIDTH = 20.6f;
	private static final float CYCLOPS_BEAM_HEIGHT = 0.15f;
	
	private static final float CYCLOPS_BEAM_ANIMATION_TIME_TILL_FIRE = 0.3f;
	
	private static final float CYCLOPS_LONG_BEAM_SPRITE_OFFSET = 1.2f;
	private static final int CYCLOPS_LONG_BEAM_SPRITE_REPETITIONS = 16;
	
	private static final float CYCLOPS_BEAM_REPETITION_STATE_TIME_START = 0.4f;
	private static final float CYCLOPS_BEAM_REPETITION_STATE_TIME_END = 0.6f;
	
	private AnimationDirector<TextureRegion> beamAnimation;
	
	private boolean directionIsRight;
	
	public BeamAttack(AttackConfig config, Vector2 direction, Body body, PhysicsCollisionType collisionType) {
		super(config, direction, body, collisionType);
		
		directionIsRight = direction.x > 0;
		hitFixtureProperties = new PhysicsBodyProperties().setBody(body).setCollisionType(collisionType).setSensor(true)
				.setPhysicsBodyShape(PhysicsBodyShape.RECTANGLE).setWidth(CYCLOPS_BEAM_WIDTH).setHeight(CYCLOPS_BEAM_HEIGHT)
				.setFixturePosition(getFixturePosition(direction));
		beamAnimation = AnimationManager.getInstance().getAnimationDirectorCopy(CYCLOPS_BEAM_ANIMATION_NAME);
		updateTextureDirection(beamAnimation);
	}
	
	private Vector2 getFixturePosition(Vector2 direction) {
		float directionOffsetX = CYCLOPS_BEAM_WIDTH / 2f + CYCLOPS_EYE_OFFSET_X;
		float directionOffsetY = CYCLOPS_EYE_OFFSET_Y / 2f;
		if (!directionIsRight) {
			directionOffsetX *= -1;
		}
		return new Vector2(directionOffsetX, directionOffsetY);
	}
	
	private void updateTextureDirection(AnimationDirector<TextureRegion> animation) {
		if ((!directionIsRight && AnimationDirector.isTextureRight(true, animation))
				|| (directionIsRight && AnimationDirector.isTextureLeft(true, animation))) {
			animation.flip(true, false);
		}
	}
	
	@Override
	protected void render(float delta, SpriteBatch batch) {
		if (isStartBeam()) {
			beamAnimation.increaseStateTime(delta);
			
			if (isRepeatBeamAnimation()) {
				beamAnimation.setStateTime(CYCLOPS_BEAM_REPETITION_STATE_TIME_START);
			}
			
			TextureRegion region = beamAnimation.getKeyFrame();
			
			if (!beamAnimation.isAnimationFinished()) {
				if (isLongBeamAnimationKeyFrame()) {
					for (int i = CYCLOPS_LONG_BEAM_SPRITE_REPETITIONS-1; i >= 0; i--) {
						drawBeam(batch, region, CYCLOPS_LONG_BEAM_SPRITE_OFFSET * (i + 1));
					}
				}
				
				drawBeam(batch, region, 0f);
			}
		}
	}
	
	private boolean isStartBeam() {
		return timer > config.delay - CYCLOPS_BEAM_ANIMATION_TIME_TILL_FIRE;
	}
	
	private boolean isRepeatBeamAnimation() {
		return timer < config.delay + config.duration - (beamAnimation.getAnimationDuration() - CYCLOPS_BEAM_REPETITION_STATE_TIME_END) && //
				beamAnimation.getStateTime() > CYCLOPS_BEAM_REPETITION_STATE_TIME_END;
	}
	
	private boolean isLongBeamAnimationKeyFrame() {
		return beamAnimation.getStateTime() > CYCLOPS_BEAM_ANIMATION_TIME_TILL_FIRE;
	}
	
	private void drawBeam(SpriteBatch batch, TextureRegion region, float offsetX) {
		float directionFactorX = directionIsRight ? 1 : -1;
		beamAnimation.getSpriteConfig()
				.setX((body.getPosition().x - region.getRegionWidth() * 0.5f + (CYCLOPS_EYE_OFFSET_X + 0.05f + offsetX) * directionFactorX))
				.setY((body.getPosition().y - region.getRegionHeight() * 0.5f + CYCLOPS_EYE_OFFSET_Y));
		beamAnimation.draw(batch);
	}
	
	@Override
	protected void start() {
		hitFixture = PhysicsBodyCreator.addFixture(hitFixtureProperties);
		started = true;
	}
	
	@Override
	protected void remove() {
		if (hitFixture != null) {
			PhysicsWorld.getInstance().removeFixture(hitFixture, hitFixtureProperties.body);
		}
	}
	
	@Override
	protected void dealAttackDamage(Contact contact) {
		Fixture fixtureA = contact.getFixtureA();
		Fixture fixtureB = contact.getFixtureB();
		
		if (CollisionUtil.containsCollisionType(collisionType, fixtureA, fixtureB)) {
			Object attackUserData = CollisionUtil.getCollisionTypeUserData(collisionType, fixtureA, fixtureB);
			Object attackedObjectUserData = CollisionUtil.getOtherTypeUserData(collisionType, fixtureA, fixtureB);
			
			if (attackedObjectUserData != null && attackUserData == hitFixtureProperties.body.getUserData()
					&& attackedObjectUserData instanceof Hittable) {
				Hittable attackedObject = ((Hittable) attackedObjectUserData);
				attackedObject.pushByHit(hitFixture.getBody().getPosition(), config.pushForce, config.pushForceAffectedByBlock);
				attackedObject.takeDamage(config.damage, AttackType.BEAM);
			}
		}
	}
}
