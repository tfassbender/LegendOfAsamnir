package net.jfabricationgames.gdx.enemy.state;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectSet;

import net.jfabricationgames.gdx.animation.AnimationManager;

public class EnemyStateMachine {
	
	private AnimationManager animationManager;
	
	private EnemyState currentState;
	private ArrayMap<String, EnemyState> states;
	
	public EnemyStateMachine(FileHandle stateConfig, String initialState) {
		animationManager = AnimationManager.getInstance();
		
		Json json = new Json();
		@SuppressWarnings("unchecked")
		Array<EnemyStateConfig> config = json.fromJson(Array.class, EnemyStateConfig.class, stateConfig);
		loadStates(config);
		
		currentState = states.get(initialState);
		if (currentState == null) {
			throw new IllegalStateException("The initialState '" + initialState + "' is unknown.");
		}
	}
	
	private void loadStates(Array<EnemyStateConfig> stateConfig) {
		states = new ArrayMap<>();
		
		//initialize all states
		for (EnemyStateConfig config : stateConfig) {
			EnemyState state = new EnemyState(animationManager.getAnimationDirector(config.animation), config.endsWithAnimation);
			states.put(config.id, state);
		}
		
		//link the states
		for (EnemyStateConfig config : stateConfig) {
			EnemyState state = states.get(config.id);
			if (config.followingState != null) {
				EnemyState followingState = states.get(config.followingState);
				if (followingState == null) {
					throw new IllegalStateException(
							"The followingState '" + config.followingState + "' of the state '" + config.id + "' is unknown.");
				}
				state.followingState = followingState;
			}
			else {
				if (config.endsWithAnimation) {
					throw new IllegalStateException("An EnemyStateConfig that sets 'endsWithAnimation' to true must define a 'followingState'. "
							+ "This config ('" + config.id + "') doesn't.");
				}
			}
			
			state.interruptingStates = new ObjectSet<>();
			for (String interruptingStates : config.interruptingStates) {
				EnemyState interruptingState = states.get(interruptingStates);
				if (interruptingState == null) {
					throw new IllegalStateException("The interruptingState '" + interruptingState + "' of the state '" + config.id + "' is unknown.");
				}
				state.interruptingStates.add(interruptingState);
			}
		}
	}
	
	/**
	 * Change states that end on the animations end (if the animation has ended).
	 */
	public void updateState() {
		if (currentState.getAnimation().isAnimationFinished() && currentState.endsWithAnimation) {
			setState(currentState.followingState);
		}
	}
	
	public boolean setState(String id) {
		return setState(getEnemyState(id));
	}
	public boolean setState(EnemyState state) {
		if (currentState.interruptingStates.contains(state) || followsOnCurrentState(state)) {
			currentState.leaveState();
			currentState = state;
			currentState.enterState();
			return true;
		}
		return false;
	}
	
	private boolean followsOnCurrentState(EnemyState state) {
		return currentState.endsWithAnimation && currentState.animation.isAnimationFinished() && currentState.followingState == state;
	}
	
	public EnemyState getEnemyState(String id) {
		return states.get(id);
	}
	
	public EnemyState getCurrentState() {
		return currentState;
	}
}
