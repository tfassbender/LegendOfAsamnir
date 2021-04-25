package net.jfabricationgames.gdx.character.state;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectSet;

import net.jfabricationgames.gdx.animation.AnimationDirector;
import net.jfabricationgames.gdx.animation.AnimationManager;
import net.jfabricationgames.gdx.animation.DummyAnimationDirector;
import net.jfabricationgames.gdx.attack.AttackCreator;

public class CharacterStateMachine {
	
	public static final float ANGLE_FLIP_THRESHOLD_DEGREES = 10f;
	public static final CharacterState END_STATE = new CharacterState(new DummyAnimationDirector<TextureRegion>(), new CharacterStateConfig(), null);
	public static final String END_STATE_NAME = "END";
	
	private float timeSinceAnimationEnded;
	
	private AnimationManager animationManager;
	
	private CharacterState currentState;
	private ArrayMap<String, CharacterState> states;
	
	private AttackCreator attackCreator;
	
	private String configFileName;
	
	public CharacterStateMachine(String stateConfigFile, String initialState, AttackCreator attackCreator) {
		this.attackCreator = attackCreator;
		animationManager = AnimationManager.getInstance();
		
		FileHandle stateConfigFileHandle = Gdx.files.internal(stateConfigFile);
		configFileName = stateConfigFileHandle.name();
		
		Json json = new Json();
		@SuppressWarnings("unchecked")
		Array<CharacterStateConfig> config = json.fromJson(Array.class, CharacterStateConfig.class, stateConfigFileHandle);
		loadStates(config);
		
		currentState = states.get(initialState);
		if (currentState == null) {
			throw new IllegalStateException("The initialState '" + initialState + "' is unknown.");
		}
	}
	
	private void loadStates(Array<CharacterStateConfig> stateConfig) {
		states = new ArrayMap<>();
		
		initializeStates(stateConfig);
		linkStates(stateConfig);
	}
	
	private void initializeStates(Array<CharacterStateConfig> stateConfig) {
		for (CharacterStateConfig config : stateConfig) {
			CharacterState state = new CharacterState(animationManager.getAnimationDirector(config.animation), config, attackCreator);
			states.put(config.id, state);
		}
		
		if (states.containsKey(END_STATE_NAME)) {
			throw new IllegalStateException("The state config must not contain a state named 'END', because thats a special state.");
		}
		states.put(END_STATE_NAME, END_STATE);
	}
	
	private void linkStates(Array<CharacterStateConfig> stateConfig) {
		for (CharacterStateConfig config : stateConfig) {
			CharacterState state = states.get(config.id);
			
			if (config.followingState != null) {
				CharacterState followingState = states.get(config.followingState);
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
			for (String interruptingStateName : config.interruptingStates) {
				CharacterState interruptingState = states.get(interruptingStateName);
				if (interruptingState == null) {
					throw new IllegalStateException(
							"The interruptingState '" + interruptingStateName + "' of the state '" + config.id + "' is unknown.");
				}
				state.interruptingStates.add(interruptingState);
			}
		}
	}
	
	/**
	 * Change states that end on the animations end (if the animation has ended).
	 */
	public void updateState(float delta) {
		if (isStateEnded(delta)) {
			if (currentState.followingState.config.attack != null) {
				currentState.followingState.setAttackDirection(new Vector2(0, 0));
			}
			setState(currentState.followingState);
		}
	}
	
	private boolean isStateEnded(float delta) {
		return isChangeStateAfterAnimation(delta) || isChangeStateAfterAttackFinishes(delta);
	}
	
	private boolean isChangeStateAfterAnimation(float delta) {
		return currentState.config.endsWithAnimation && currentState.getAnimation().isAnimationFinished() && afterAnimationDelayEnded(delta);
	}
	
	private boolean isChangeStateAfterAttackFinishes(float delta) {
		return currentState.config.endsAfterAttackFinishes && currentState.allAttacksFinished();
	}
	
	private boolean afterAnimationDelayEnded(float delta) {
		timeSinceAnimationEnded += delta;
		return timeSinceAnimationEnded > currentState.config.changeStateAfterAnimationDelay;
	}
	
	public boolean isInState(String stateId) {
		return currentState == getState(stateId);
	}
	
	public boolean isInEndState() {
		return currentState == END_STATE;
	}
	
	/**
	 * Changes the state to the given state id without checking if this is possible in the state configuration.<br>
	 * Should only be used in Cutscenes.
	 */
	public void forceStateChange(String id) {
		changeState(getState(id));
	}
	
	public boolean setState(String id) {
		return setState(getState(id));
	}
	public boolean setState(CharacterState state) {
		if (isStateChangeAllowed(state)) {
			changeState(state);
			return true;
		}
		return false;
	}
	
	private boolean isStateChangeAllowed(CharacterState state) {
		return currentState.interruptingStates.contains(state) || isStateEnded(0f) && followsOnCurrentState(state);
	}
	
	private void changeState(CharacterState state) {
		CharacterState leavingState = currentState;
		leavingState.leaveState();
		currentState = state;
		currentState.enterState(leavingState);
		
		timeSinceAnimationEnded = 0;
	}
	
	private boolean followsOnCurrentState(CharacterState state) {
		return (currentState.config.endsWithAnimation || currentState.config.endsAfterAttackFinishes) && currentState.animation.isAnimationFinished()
				&& currentState.followingState == state;
	}
	
	public void flipAnimationTexturesToMovementDirection(AnimationDirector<TextureRegion> animation, Vector2 movingDirection) {
		if (isFlipTextureToMovingDirection() && movingDirection.len2() > 0.1f) {
			float angleDegrees = movingDirection.angleDeg();
			boolean flipToLeft = angleDegrees > 90 + ANGLE_FLIP_THRESHOLD_DEGREES && angleDegrees < 270 - ANGLE_FLIP_THRESHOLD_DEGREES;
			boolean flipToRight = angleDegrees < 90 - ANGLE_FLIP_THRESHOLD_DEGREES || angleDegrees > 270 + ANGLE_FLIP_THRESHOLD_DEGREES;
			if ((flipToLeft && isTextureRight(animation)) || (flipToRight && isTextureLeft(animation))) {
				animation.flip(true, false);
			}
		}
	}
	
	private boolean isFlipTextureToMovingDirection() {
		return currentState.config.flipAnimationToMovingDirection;
	}
	
	private boolean isTextureRight(AnimationDirector<TextureRegion> animation) {
		return AnimationDirector.isTextureRight(currentState.config.initialAnimationDirectionRight, animation);
	}
	
	private boolean isTextureLeft(AnimationDirector<TextureRegion> animation) {
		return AnimationDirector.isTextureLeft(currentState.config.initialAnimationDirectionRight, animation);
	}
	
	public CharacterState getState(String id) {
		CharacterState state = states.get(id);
		if (state == null) {
			throw new IllegalArgumentException("The state '" + id + "' doesn't exist in this config (config file is '" + configFileName + "')");
		}
		return state;
	}
	
	public CharacterState getCurrentState() {
		return currentState;
	}
}
