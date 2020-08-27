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
	protected boolean endsWithAnimation;
	protected String stateEnteringSound;
	
	protected EnemyState followingState;
	protected ObjectSet<EnemyState> interruptingStates;
	
	private Array<EnemyStateListener> stateListeners;
	
	public EnemyState(AnimationDirector<TextureRegion> animation, boolean endsWithAnimation) {
		this.animation = animation;
		this.endsWithAnimation = endsWithAnimation;
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
	
	public void enterState() {
		for (EnemyStateListener listener : stateListeners) {
			listener.enteringState(this);
		}
		animation.resetStateTime();
		playSound();
	}
	
	private void playSound() {
		if (stateEnteringSound != null) {
			soundSet.playSound(stateEnteringSound);
		}
	}
}
