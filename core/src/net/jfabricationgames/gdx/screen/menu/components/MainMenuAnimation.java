package net.jfabricationgames.gdx.screen.menu.components;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import net.jfabricationgames.gdx.animation.AnimationManager;
import net.jfabricationgames.gdx.animation.AnimationDirector;

public class MainMenuAnimation {
	
	private AnimationDirector<TextureRegion> dwarfAnimation;
	private AnimationDirector<TextureRegion> minotaurAnimation;
	private float dwarfStartingPosition = -400f;
	private float dwarfPosition = dwarfStartingPosition;
	private float dwarfPositionChangeToAttackAnimation = 850f;
	private float dwarfPositionMax = 950f;
	private float dwarfMovement = 200f;
	private float minotaurMovement = 170f;
	private float minotaurOffset = 200f;
	private float minotaurPosition = dwarfStartingPosition + minotaurOffset;
	
	private boolean changedToAttackAnimation = false;
	private boolean changedToIdleAnimation = false;
	
	public MainMenuAnimation() {
		AnimationManager.getInstance().loadAnimations("config/animation/menu.json");
		dwarfAnimation = AnimationManager.getInstance().getTextureAnimationDirector("dwarf_run_right");
		minotaurAnimation = AnimationManager.getInstance().getTextureAnimationDirector("minotaur_move");
	}
	
	public void drawAnimation(SpriteBatch batch, float delta) {
		if (dwarfPosition < dwarfPositionMax) {
			dwarfPosition += delta * dwarfMovement;
			minotaurPosition += delta * minotaurMovement;
		}
		
		if (dwarfPosition > dwarfPositionChangeToAttackAnimation && !changedToAttackAnimation) {
			dwarfAnimation = AnimationManager.getInstance().getTextureAnimationDirector("dwarf_attack_jump_right");
			changedToAttackAnimation = true;
		}
		
		if (changedToAttackAnimation && !changedToIdleAnimation && dwarfAnimation.isAnimationFinished()) {
			dwarfAnimation = AnimationManager.getInstance().getTextureAnimationDirector("dwarf_idle_right");
			minotaurAnimation = AnimationManager.getInstance().getTextureAnimationDirector("minotaur_die");
			changedToIdleAnimation = true;
		}
		
		dwarfAnimation.increaseStateTime(delta);
		TextureRegion dwarf = dwarfAnimation.getKeyFrame();
		minotaurAnimation.increaseStateTime(delta);
		TextureRegion minotaur = minotaurAnimation.getKeyFrame();
		
		batch.draw(dwarf, dwarfPosition, 55, 100, 100);
		batch.draw(minotaur, minotaurPosition, -30, 250, 250);
	}
}
