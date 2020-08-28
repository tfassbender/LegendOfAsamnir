package net.jfabricationgames.gdx.enemy.state;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectSet;

import net.jfabricationgames.gdx.animation.AnimationDirector;
import net.jfabricationgames.gdx.sound.SoundManager;
import net.jfabricationgames.gdx.sound.SoundSet;

public class EnemyState {
	
	private static final SoundSet soundSet = SoundManager.getInstance().loadSoundSet("enemy");
	
	protected AnimationDirector<TextureRegion> animation;
	
	protected EnemyStateConfig config;
	
	protected EnemyState followingState;
	protected ObjectSet<EnemyState> interruptingStates;
	
	private Array<EnemyStateListener> stateListeners;
	
	public EnemyState(AnimationDirector<TextureRegion> animation, EnemyStateConfig config) {
		this.animation = animation;
		this.config = config;
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
	
	private void playSound() {
		if (config.stateEnteringSound != null) {
			soundSet.playSound(config.stateEnteringSound);
		}
	}
}
