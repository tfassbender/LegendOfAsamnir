package net.jfabricationgames.gdx.enemy.state;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectSet;

import net.jfabricationgames.gdx.animation.AnimationDirector;
import net.jfabricationgames.gdx.attack.AttackCreator;
import net.jfabricationgames.gdx.sound.SoundManager;
import net.jfabricationgames.gdx.sound.SoundSet;

public class EnemyState {
	
	private static final SoundSet soundSet = SoundManager.getInstance().loadSoundSet("enemy");
	
	protected AnimationDirector<TextureRegion> animation;
	
	protected EnemyStateConfig config;
	
	protected EnemyState followingState;
	protected ObjectSet<EnemyState> interruptingStates;
	
	protected AttackCreator attackCreator;
	
	private Array<EnemyStateListener> stateListeners;
	
	private Vector2 directionToTarget;
	
	public EnemyState(AnimationDirector<TextureRegion> animation, EnemyStateConfig config, AttackCreator attackCreator) {
		this.animation = animation;
		this.config = config;
		this.attackCreator = attackCreator;
		stateListeners = new Array<>();
	}
	
	public void addStateListener(EnemyStateListener listener) {
		stateListeners.add(listener);
	}
	public void removeStateListener(EnemyStateListener listener) {
		stateListeners.removeValue(listener, false);
	}
	
	public AnimationDirector<TextureRegion> getAnimation() {
		return animation;
	}
	
	public void leaveState() {
		for (EnemyStateListener listener : stateListeners) {
			listener.leavingState(this);
		}
	}
	
	public void enterState(EnemyState previousState) {
		for (EnemyStateListener listener : stateListeners) {
			listener.enteringState(this);
		}
		if (config.flipAnimationOnEnteringOnly) {
			flipAnimationToMovementDirection(previousState);
		}
		if (config.attack != null) {
			if (directionToTarget == null) {
				throw new IllegalStateException("The direction for the attack has not been set. "
						+ "Use the setAttackDirection(Vector2) method to set the direction BEFORE changing to this state.");
			}
			attackCreator.startAttack(config.attack, directionToTarget);
		}
		animation.resetStateTime();
		playSound();
	}
	
	/**
	 * Flip the whole animation into the right direction, according to the last image, that was drawn of the previous state.
	 */
	private void flipAnimationToMovementDirection(EnemyState previousState) {
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
