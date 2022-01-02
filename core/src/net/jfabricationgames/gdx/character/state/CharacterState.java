package net.jfabricationgames.gdx.character.state;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectSet;

import net.jfabricationgames.gdx.animation.AnimationDirector;
import net.jfabricationgames.gdx.cutscene.action.CutsceneControlledState;
import net.jfabricationgames.gdx.sound.SoundHandler;
import net.jfabricationgames.gdx.sound.SoundManager;
import net.jfabricationgames.gdx.sound.SoundSet;

public class CharacterState implements CutsceneControlledState {
	
	private static final SoundSet SOUND_SET = SoundManager.getInstance().loadSoundSet("enemy");
	
	protected AnimationDirector<TextureRegion> animation;
	
	protected CharacterStateConfig config;
	
	protected CharacterState followingState;
	protected ObjectSet<CharacterState> interruptingStates;
	
	protected CharacterStateAttackHandler attackHandler;
	private Array<CharacterStateAttack> attacks;
	
	private Vector2 directionToTarget;
	
	private SoundHandler sound;
	
	public CharacterState(AnimationDirector<TextureRegion> animation, CharacterStateConfig config, CharacterStateAttackHandler attackHandler) {
		this.animation = animation;
		this.config = config;
		this.attackHandler = attackHandler;
		attacks = new Array<>();
	}
	
	public AnimationDirector<TextureRegion> getAnimation() {
		return animation;
	}
	
	public void leaveState() {
		abortAttacks();
		abortSound();
	}
	
	private void abortAttacks() {
		for (CharacterStateAttack attack : attacks) {
			attack.abort();
		}
		attacks.clear();
	}
	
	private void abortSound() {
		if (config.abortSoundWhenStateInterrupted && sound != null) {
			sound.stop();
		}
	}
	
	public void enterState(CharacterState previousState) {
		if (config.flipAnimationOnEnteringOnly) {
			flipAnimationToMovementDirection(previousState);
		}
		if (config.attack != null) {
			startAttack();
		}
		
		animation.resetStateTime();
		playSound();
	}
	
	private void startAttack() {
		if (directionToTarget == null) {
			throw new IllegalStateException("The direction for the attack has not been set. "
					+ "Use the setAttackDirection(Vector2) method to set the direction BEFORE changing to this state.");
		}
		CharacterStateAttack attack = attackHandler.startAttack(config.attack, directionToTarget);
		attacks.add(attack);
	}
	
	public boolean allAttacksFinished() {
		boolean allAttacksFinished = !attacks.isEmpty();
		for (CharacterStateAttack attack : attacks) {
			allAttacksFinished &= attack.isExecuted();
		}
		return allAttacksFinished;
	}
	
	/**
	 * Flip the whole animation into the right direction, according to the last image, that was drawn of the previous state.
	 */
	private void flipAnimationToMovementDirection(CharacterState previousState) {
		boolean lastImageRight = previousState.config.initialAnimationDirectionRight && !previousState.animation.getKeyFrame().isFlipX();
		boolean animationRight = config.initialAnimationDirectionRight != animation.getKeyFrame().isFlipX();
		if (lastImageRight != animationRight) {
			animation.flip(true, false);
		}
	}
	
	public void flipAnimationToDirection(Vector2 direction) {
		float directionAngle = direction.angleDeg();
		boolean directionRight = directionAngle < 90 || directionAngle > 270;
		boolean animationRight = config.initialAnimationDirectionRight != animation.getKeyFrame().isFlipX();
		if (directionRight != animationRight) {
			animation.flip(true, false);
		}
	}
	
	private void playSound() {
		if (config.stateEnteringSound != null) {
			sound = SOUND_SET.playSound(config.stateEnteringSound);
		}
	}
	
	/**
	 * Set the direction to the target (BEFORE changing to this state), to creating an attack, that aims in the correct direction
	 */
	@Override
	public void setAttackDirection(Vector2 directionToTarget) {
		this.directionToTarget = directionToTarget;
	}
}
