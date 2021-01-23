package net.jfabricationgames.gdx.character.state;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectSet;

import net.jfabricationgames.gdx.animation.AnimationDirector;
import net.jfabricationgames.gdx.attack.Attack;
import net.jfabricationgames.gdx.attack.AttackCreator;
import net.jfabricationgames.gdx.sound.SoundManager;
import net.jfabricationgames.gdx.sound.SoundSet;

public class CharacterState {
	
	private static final SoundSet soundSet = SoundManager.getInstance().loadSoundSet("enemy");
	
	protected AnimationDirector<TextureRegion> animation;
	
	protected CharacterStateConfig config;
	
	protected CharacterState followingState;
	protected ObjectSet<CharacterState> interruptingStates;
	
	protected AttackCreator attackCreator;
	private Array<Attack> attacks;
	
	private Array<CharacterStateListener> stateListeners;
	
	private Vector2 directionToTarget;
	
	public CharacterState(AnimationDirector<TextureRegion> animation, CharacterStateConfig config, AttackCreator attackCreator) {
		this.animation = animation;
		this.config = config;
		this.attackCreator = attackCreator;
		attacks = new Array<>();
		stateListeners = new Array<>();
	}
	
	public void addStateListener(CharacterStateListener listener) {
		stateListeners.add(listener);
	}
	public void removeStateListener(CharacterStateListener listener) {
		stateListeners.removeValue(listener, false);
	}
	
	public AnimationDirector<TextureRegion> getAnimation() {
		return animation;
	}
	
	public void leaveState() {
		for (CharacterStateListener listener : stateListeners) {
			listener.leavingState(this);
		}
		
		abortAttacks();
	}
	
	private void abortAttacks() {
		for (Attack attack : attacks) {
			attack.abort();
		}
		attacks.clear();
	}
	
	public void enterState(CharacterState previousState) {
		for (CharacterStateListener listener : stateListeners) {
			listener.enteringState(this);
		}
		
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
		Attack attack = attackCreator.startAttack(config.attack, directionToTarget);
		attacks.add(attack);
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
		float directionAngle = direction.angle();
		boolean directionRight = directionAngle < 90 || directionAngle > 270;
		boolean animationRight = config.initialAnimationDirectionRight != animation.getKeyFrame().isFlipX();
		if (directionRight != animationRight) {
			animation.flip(true, false);
		}
	}
	
	private void playSound() {
		if (config.stateEnteringSound != null) {
			soundSet.playSound(config.stateEnteringSound);
		}
	}
	
	/**
	 * Set the direction to the target (BEFORE changing to this state), to creating an attack, that aims in the correct direction
	 */
	public void setAttackDirection(Vector2 directionToTarget) {
		this.directionToTarget = directionToTarget;
	}
}
