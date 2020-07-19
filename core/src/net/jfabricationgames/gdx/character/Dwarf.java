package net.jfabricationgames.gdx.character;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import net.jfabricationgames.gdx.character.animation.AnimationDirector;
import net.jfabricationgames.gdx.character.animation.CharacterAnimationAssetManager;
import net.jfabricationgames.gdx.character.animation.DummyAnimationDirector;
import net.jfabricationgames.gdx.character.animation.MovingDirection;
import net.jfabricationgames.gdx.screens.GameScreen;

public class Dwarf {
	
	public static final float MOVING_SPEED = 0.5f;
	public static final float JUMPING_SPEED = 0.75f;
	
	private static final String assetConfigFileName = "dwarf";
	
	private CharacterAnimationAssetManager assetManager;
	
	private Vector2 position;
	private CharacterAction action;
	
	private CharacterInputMovementHandler movementHandler;
	
	private AnimationDirector<TextureRegion> animation;
	private Sprite idleDwarfSprite;
	
	public Dwarf() {
		assetManager = CharacterAnimationAssetManager.getInstance();
		assetManager.loadAnimations(assetConfigFileName);
		
		position = new Vector2(0, 0);
		action = CharacterAction.NONE;
		
		movementHandler = new CharacterInputMovementHandler(this);
		
		idleDwarfSprite = getIdleSprite();
		animation = getAnimation();
	}
	
	private Sprite getIdleSprite() {
		return new Sprite(getAnimation(CharacterAction.IDLE, movementHandler.getDirection()).getAnimation().getKeyFrame(0));
	}
	
	public void changeAction(CharacterAction action) {
		this.action = action;
		this.animation = getAnimation();
		this.animation.resetStateTime();
	}
	
	private AnimationDirector<TextureRegion> getAnimation() {
		if (action != CharacterAction.NONE) {
			return getAnimation(action, movementHandler.getDirection());
		}
		else {
			return new DummyAnimationDirector<TextureRegion>();
		}
	}
	private AnimationDirector<TextureRegion> getAnimation(CharacterAction action, MovingDirection movingDirection) {
		return assetManager.getAnimationDirector(getAnimationName(action, movingDirection));
	}
	private String getAnimationName(CharacterAction action, MovingDirection direction) {
		return action.getAnimationName(direction);
	}
	
	public void render(float delta, SpriteBatch batch) {
		movementHandler.handleInputs(delta);
		movementHandler.move(delta);
		
		updateAction(delta);
		
		draw(batch);
	}
	
	private void updateAction(float delta) {
		animation.increaseStateTime(delta);
		if (animation.isAnimationFinished()) {
			changeAction(CharacterAction.NONE);
		}
	}
	
	private void draw(SpriteBatch batch) {
		if (action != CharacterAction.NONE) {
			TextureRegion frame = animation.getKeyFrame();
			drawDwarf(batch, frame);
		}
		else {
			if (movementHandler.getDirection().isDrawingDirectionRight() == idleDwarfSprite.isFlipX()) {
				idleDwarfSprite.flip(true, false);
			}
			drawDwarf(batch, idleDwarfSprite);
		}
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
				GameScreen.WORLD_TO_SCREEN, // scaleX
				GameScreen.WORLD_TO_SCREEN, // scaleY
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
}
