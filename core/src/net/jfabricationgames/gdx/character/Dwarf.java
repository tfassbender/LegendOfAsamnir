package net.jfabricationgames.gdx.character;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import net.jfabricationgames.gdx.character.animation.CharacterAnimationAssetManager;
import net.jfabricationgames.gdx.character.animation.MovingDirection;

public class Dwarf {
	
	private Vector2 position;
	private CharacterAnimationAssetManager assetManager;
	
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
			return animationPrefix + direction.getAnimationPostfix();
		}
	}
	
	public Dwarf() {
		assetManager = CharacterAnimationAssetManager.getInstance();
	}
	
	public void render(float delta, SpriteBatch batch) {
		
	}
	
	public Animation<TextureRegion> getAnimation(Action action, MovingDirection movingDirection) {
		return assetManager.getAnimation(action.getAnimationName(movingDirection));
	}
}
