package net.jfabricationgames.gdx.character;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;

import net.jfabricationgames.gdx.character.animation.AnimationDirector;
import net.jfabricationgames.gdx.character.animation.CharacterAnimationManager;
import net.jfabricationgames.gdx.character.animation.DummyAnimationDirector;
import net.jfabricationgames.gdx.screens.GameScreen;
import net.jfabricationgames.gdx.sound.SoundManager;
import net.jfabricationgames.gdx.sound.SoundSet;

public class Dwarf implements Disposable {
	
	public static final float MOVING_SPEED = 200f;
	public static final float JUMPING_SPEED = 300f;
	public static final float TIME_TILL_IDLE_ANIMATION = 4.0f;
	public static final float SCALE_FACTOR = 1f;
	
	private static final String assetConfigFileName = "config/animation/dwarf.json";
	private static final String soundSetKey = "dwarf";
	
	private CharacterAnimationManager assetManager;
	
	private Vector2 position;
	private CharacterAction action;
	
	private CharacterInputMovementHandler movementHandler;
	
	private AnimationDirector<TextureRegion> animation;
	private Sprite idleDwarfSprite;
	
	private SoundSet soundSet;
	
	public Dwarf() {
		assetManager = CharacterAnimationManager.getInstance();
		assetManager.loadAnimations(assetConfigFileName);
		
		position = new Vector2(0, 0);
		action = CharacterAction.NONE;
		
		idleDwarfSprite = getIdleSprite();
		animation = getAnimation();
		
		soundSet = SoundManager.getInstance().loadSoundSet(soundSetKey);
		
		movementHandler = new CharacterInputMovementHandler(this);
	}
	
	private Sprite getIdleSprite() {
		return new Sprite(getAnimation(CharacterAction.IDLE).getAnimation().getKeyFrame(0));
	}
	
	public void changeAction(CharacterAction action) {
		this.action = action;
		this.animation = getAnimation();
		this.animation.resetStateTime();
		
		playSound(action);
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
		
		movementHandler.handleInputs(delta);
		movementHandler.move(delta);
		
		draw(batch);
	}
	
	private void updateAction(float delta) {
		animation.increaseStateTime(delta);
		if (animation.isAnimationFinished()) {
			changeAction(CharacterAction.NONE);
		}
	}
	
	private void draw(SpriteBatch batch) {
		TextureRegion frame = action != CharacterAction.NONE ? animation.getKeyFrame() : idleDwarfSprite;
		
		if (movementHandler.isDrawDirectionRight() == frame.isFlipX()) {
			frame.flip(true, false);
		}
		
		drawDwarf(batch, frame);
	}
	
	private void drawDwarf(SpriteBatch batch, TextureRegion frame) {
		int width = frame.getRegionWidth();
		int height = frame.getRegionHeight();
		float originX = 0.5f * width;
		float originY = 0.5f * height;
		float x = position.x - originX;
		float y = position.y - originY;
		
		//		batch.draw(dwarfSprite, // sprite
		//				x, y, //x, y
		//				originX, originY, // originX, originY
		//				width, height, // width, height
		//				GameScreen.WORLD_TO_SCREEN, // scaleX
		//				GameScreen.WORLD_TO_SCREEN, // scaleY
		//				0); // rotation
		
		batch.draw(frame, // textureRegion
				x, y, // x, y
				originX, originY, //originX, originY
				width, height, // width, height
				GameScreen.WORLD_TO_SCREEN * SCALE_FACTOR, // scaleX
				GameScreen.WORLD_TO_SCREEN * SCALE_FACTOR, // scaleY
				0.0f); // rotation
	}
	
	public CharacterAction getCurrentAction() {
		return action;
	}
	
	public float getMovingSpeed() {
		return MOVING_SPEED;
	}
	public float getJumpingSpeed() {
		return JUMPING_SPEED;
	}
	
	public void move(float deltaX, float deltaY) {
		position.x += deltaX;
		position.y += deltaY;
	}
	
	public float getTimeTillIdleAnimation() {
		return TIME_TILL_IDLE_ANIMATION;
	}
	
	public boolean isAnimationFinished() {
		return animation.isAnimationFinished();
	}

	public Vector2 getPosition() {
		return position;
	}

	@Override
	public void dispose() {
		soundSet.dispose();
	}
}
