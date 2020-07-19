package net.jfabricationgames.gdx.character;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import net.jfabricationgames.gdx.character.animation.AnimationDirector;
import net.jfabricationgames.gdx.character.animation.CharacterAnimationAssetManager;
import net.jfabricationgames.gdx.character.animation.MovingDirection;
import net.jfabricationgames.gdx.screens.GameScreen;

public class Dwarf {
	
	private static final String assetConfigFileName = "dwarf";
	
	private CharacterAnimationAssetManager assetManager;
	
	private Vector2 position;
	private Action action;
	private MovingDirection direction;
	
	private AnimationDirector<TextureRegion> animation;
	private Sprite dwarfSprite;
	
	public enum Action {
		
		IDLE("dwarf_idle_"), //
		RUN("dwarf_run_"), //
		JUMP("dwarf_jump_"), //
		ATTACK("dwarf_attack_"), //
		ATTACK_JUMP("dwarf_attack_jump_"), //
		ATTACK_SPIN("dwarf_spin_"), //
		HIT("dwarf_hit_"), //
		DIE("dwarf_die_");
		
		private final String animationPrefix;
		
		private Action(String animationPrefix) {
			this.animationPrefix = animationPrefix;
		}
		
		public String getAnimationName(MovingDirection direction) {
			return animationPrefix + direction.getAnimationDirectionPostfix();
		}
	}
	
	public Dwarf() {
		assetManager = CharacterAnimationAssetManager.getInstance();
		assetManager.loadAnimations(assetConfigFileName);
		
		position = new Vector2(0, 0);
		action = Action.IDLE;
		direction = MovingDirection.RIGHT;
		
		animation = getAnimation();
		dwarfSprite = getSprite();
	}
	
	private AnimationDirector<TextureRegion> getAnimation() {
		return getAnimation(action, direction);
	}
	private AnimationDirector<TextureRegion> getAnimation(Action action, MovingDirection movingDirection) {
		return assetManager.getAnimationDirector(getAnimationName(action, movingDirection));
	}
	
	private Sprite getSprite() {
		return new Sprite(animation.getAnimation().getKeyFrame(0));
	}
	
	private String getAnimationName(Action action, MovingDirection direction) {
		return action.getAnimationName(direction);
	}
	
	public void render(float delta, SpriteBatch batch) {
		TextureRegion frame = animation.getKeyFrame(delta);
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
				GameScreen.WORLD_TO_SCREEN, // scaleX
				GameScreen.WORLD_TO_SCREEN, // scaleY
				0.0f); // rotation
	}
}
