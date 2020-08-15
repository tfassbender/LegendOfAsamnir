package net.jfabricationgames.gdx.character;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;

import net.jfabricationgames.gdx.character.animation.AnimationDirector;
import net.jfabricationgames.gdx.character.animation.CharacterAnimationManager;
import net.jfabricationgames.gdx.character.animation.DummyAnimationDirector;
import net.jfabricationgames.gdx.hud.StatsCharacter;
import net.jfabricationgames.gdx.screens.GameScreen;
import net.jfabricationgames.gdx.sound.SoundManager;
import net.jfabricationgames.gdx.sound.SoundSet;

public class Dwarf implements PlayableCharacter, StatsCharacter, Disposable {
	
	public static final float MOVING_SPEED = 300f;
	public static final float JUMPING_SPEED = 425f;
	public static final float MOVING_SPEED_SPRINT = 425f;
	public static final float MOVING_SPEED_ATTACK = 150;
	public static final float TIME_TILL_IDLE_ANIMATION = 4.0f;
	public static final float SCALE_FACTOR = 1f;
	
	private static final String assetConfigFileName = "config/animation/dwarf.json";
	private static final String soundSetKey = "dwarf";
	
	private CharacterAnimationManager assetManager;
	
	private Body body;
	
	private CharacterAction action;
	
	private float health = 100f;
	private float maxHealth = 100f;
	private float mana = 100f;
	private float maxMana = 100f;
	private float endurance = 100f;
	private float maxEndurance = 100f;
	private float enduranceChargeMoving = 7.5f;
	private float enduranceChargeIdle = 15f;
	private float enduranceCostsSprint = 15f;
	
	private CharacterInputMovementHandler movementHandler;
	
	private AnimationDirector<TextureRegion> animation;
	private Sprite idleDwarfSprite;
	
	private SoundSet soundSet;
	
	public Dwarf(World world) {
		assetManager = CharacterAnimationManager.getInstance();
		assetManager.loadAnimations(assetConfigFileName);
		
		action = CharacterAction.NONE;
		
		idleDwarfSprite = getIdleSprite();
		animation = getAnimation();
		
		body = createPolygon(world, BodyType.DynamicBody, 0, 0, 1f, 0f, 0.8f,
				idleDwarfSprite.getWidth() * GameScreen.WORLD_TO_SCREEN * SCALE_FACTOR * 0.5f,
				idleDwarfSprite.getHeight() * GameScreen.WORLD_TO_SCREEN * SCALE_FACTOR * 0.5f);
		body.setLinearDamping(10f);
		
		soundSet = SoundManager.getInstance().loadSoundSet(soundSetKey);
		
		movementHandler = new CharacterInputMovementHandler(this);
	}
	
	private Body createPolygon(World world, BodyType type, float x, float y, float density, float restitution, float friction, float halfwidth,
			float halfheight) {
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = type;
		bodyDef.position.set(x, y);
		bodyDef.angle = 0;
		bodyDef.fixedRotation = true;
		
		Body square = world.createBody(bodyDef);
		
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.density = density;
		fixtureDef.restitution = restitution;
		fixtureDef.friction = friction;
		fixtureDef.shape = new PolygonShape();
		((PolygonShape) fixtureDef.shape).setAsBox(halfwidth, halfheight);
		
		square.createFixture(fixtureDef);
		fixtureDef.shape.dispose();
		
		return square;
	}
	
	private Sprite getIdleSprite() {
		return new Sprite(getAnimation(CharacterAction.IDLE).getAnimation().getKeyFrame(0));
	}
	
	public boolean changeAction(CharacterAction action) {
		if (endurance >= action.getEnduranceCosts()) {
			this.action = action;
			this.animation = getAnimation();
			this.animation.resetStateTime();
			
			endurance = Math.max(0, endurance - action.getEnduranceCosts());
			
			playSound(action);
			return true;
		}
		return false;
	}
	
	private AnimationDirector<TextureRegion> getAnimation() {
		if (action != CharacterAction.NONE) {
			return getAnimation(action);
		}
		else {
			return new DummyAnimationDirector<TextureRegion>();
		}
	}
	private AnimationDirector<TextureRegion> getAnimation(CharacterAction action) {
		return assetManager.getAnimationDirector(getAnimationName(action));
	}
	private String getAnimationName(CharacterAction action) {
		return action.getAnimationName();
	}
	
	private void playSound(CharacterAction action) {
		if (action.getSound() != null) {
			soundSet.playSound(action.getSound());
		}
	}
	
	public void render(float delta, SpriteBatch batch) {
		updateAction(delta);
		updateStats(delta);
		updateForce();
		
		movementHandler.handleInputs(delta);
		movementHandler.move(delta);
		
		drawDwarf(batch);
	}
	
	private void updateAction(float delta) {
		animation.increaseStateTime(delta);
		if (animation.isAnimationFinished()) {
			changeAction(CharacterAction.NONE);
		}
	}
	
	private void updateStats(float delta) {
		endurance = Math.min(endurance + delta * getEnduranceCharge(), maxEndurance);
	}
	private float getEnduranceCharge() {
		if (action == CharacterAction.NONE || action == CharacterAction.IDLE) {
			return enduranceChargeIdle;
		}
		else {
			return enduranceChargeMoving;
		}
	}
	
	private void updateForce() {
	}
	
	private void drawDwarf(SpriteBatch batch) {
		TextureRegion frame = action != CharacterAction.NONE ? animation.getKeyFrame() : idleDwarfSprite;
		
		if (movementHandler.isDrawDirectionRight() == frame.isFlipX()) {
			frame.flip(true, false);
		}
		
		draw(batch, frame);
	}
	
	private void draw(SpriteBatch batch, TextureRegion frame) {
		int width = frame.getRegionWidth();
		int height = frame.getRegionHeight();
		float originX = 0.5f * width;
		float originY = 0.5f * height;
		float x = body.getPosition().x - originX;
		float y = body.getPosition().y - originY;
		
		batch.draw(frame, // textureRegion
				x, y, // x, y
				originX, originY, //originX, originY
				width, height, // width, height
				GameScreen.WORLD_TO_SCREEN * SCALE_FACTOR, // scaleX
				GameScreen.WORLD_TO_SCREEN * SCALE_FACTOR, // scaleY
				0.0f); // rotation
	}
	
	@Override
	public CharacterAction getCurrentAction() {
		return action;
	}
	
	@Override
	public float getMovingSpeed(boolean sprint) {
		if (action == CharacterAction.JUMP) {
			return JUMPING_SPEED;
		}
		if (action == CharacterAction.ATTACK) {
			return MOVING_SPEED_ATTACK;
		}
		if (sprint) {
			return MOVING_SPEED_SPRINT;
		}
		return MOVING_SPEED;
	}
	
	@Override
	public void reduceEnduranceForSprinting(float delta) {
		endurance -= enduranceCostsSprint * delta;
		if (endurance < 0) {
			endurance = 0;
		}
	}
	@Override
	public boolean isExhausted() {
		return endurance < 1e-5;
	}
	
	@Override
	public void move(float deltaX, float deltaY) {
		float force = 10f * body.getMass();
		body.applyForceToCenter(deltaX * force, deltaY * force, true);
	}
	
	@Override
	public float getTimeTillIdleAnimation() {
		return TIME_TILL_IDLE_ANIMATION;
	}
	
	@Override
	public boolean isAnimationFinished() {
		return animation.isAnimationFinished();
	}
	
	@Override
	public float getHealth() {
		return health / maxHealth;
	}
	
	@Override
	public float getMana() {
		return mana / maxMana;
	}
	
	@Override
	public float getEndurance() {
		return endurance / maxEndurance;
	}
	
	@Override
	public void dispose() {
		soundSet.dispose();
	}
	
	public Vector2 getPosition() {
		return body.getPosition();
	}
	
	public void setPosition(float x, float y) {
		body.setTransform(x, y, 0);
	}
}
