package net.jfabricationgames.gdx.enemy.state;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectSet;

import net.jfabricationgames.gdx.animation.AnimationManager;
import net.jfabricationgames.gdx.attack.AttackCreator;

public class EnemyStateMachine {
	
	public static final float ANGLE_FLIP_THRESHOLD_DEGREES = 10f;
	
	private AnimationManager animationManager;
	
	private EnemyState currentState;
	private ArrayMap<String, EnemyState> states;
	
	private AttackCreator attackCreator;
	
	private String configFileName;
	
	public EnemyStateMachine(String stateConfigFile, String initialState, AttackCreator attackCreator) {
		this.attackCreator = attackCreator;
		animationManager = AnimationManager.getInstance();
		
		FileHandle stateConfigFileHandle = Gdx.files.internal(stateConfigFile);
		configFileName = stateConfigFileHandle.name();
		
		Json json = new Json();
		@SuppressWarnings("unchecked")
		Array<EnemyStateConfig> config = json.fromJson(Array.class, EnemyStateConfig.class, stateConfigFileHandle);
		loadStates(config);
		
		currentState = states.get(initialState);
		if (currentState == null) {
			throw new IllegalStateException("The initialState '" + initialState + "' is unknown.");
		}
	}
	
	private void loadStates(Array<EnemyStateConfig> stateConfig) {
		states = new ArrayMap<>();
		
		initializeStates(stateConfig);
		linkStates(stateConfig);
	}
	
	private void initializeStates(Array<EnemyStateConfig> stateConfig) {
		for (EnemyStateConfig config : stateConfig) {
			EnemyState state = new EnemyState(animationManager.getAnimationDirector(config.animation), config, attackCreator);
			states.put(config.id, state);
		}
	}
	
	private void linkStates(Array<EnemyStateConfig> stateConfig) {
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
		if (currentState.getAnimation().isAnimationFinished() && currentState.config.endsWithAnimation) {
			setState(currentState.followingState);
		}
	}
	
	public boolean setState(String id) {
		return setState(getState(id));
	}
	public boolean setState(EnemyState state) {
		if (currentState.interruptingStates.contains(state) || followsOnCurrentState(state)) {
			EnemyState leavingState = currentState;
			leavingState.leaveState();
			currentState = state;
			currentState.enterState(leavingState);
			return true;
		}
		return false;
	}
	
	private boolean followsOnCurrentState(EnemyState state) {
		return currentState.config.endsWithAnimation && currentState.animation.isAnimationFinished() && currentState.followingState == state;
	}
	
	public void flipTextureToMovementDirection(TextureRegion region, Vector2 movingDirection) {
		if (isFlipTextureToMovingDirection() && movingDirection.len2() > 1e-3) {
			float angleDegrees = movingDirection.angle();
			boolean flipToLeft = angleDegrees > 90 + ANGLE_FLIP_THRESHOLD_DEGREES && angleDegrees < 270 - ANGLE_FLIP_THRESHOLD_DEGREES;
			boolean flipToRight = angleDegrees < 90 - ANGLE_FLIP_THRESHOLD_DEGREES || angleDegrees > 270 + ANGLE_FLIP_THRESHOLD_DEGREES;
			if ((flipToLeft && isTextureRight(region)) || (flipToRight && isTextureLeft(region))) {
				region.flip(true, false);
			}
		}
	}
	
	private boolean isFlipTextureToMovingDirection() {
		return currentState.config.flipAnimationToMovingDirection;
	}
	
	private boolean isTextureRight(TextureRegion texture) {
		//animationRight && !texture.flip || !animationRight && texture.flip
		return currentState.config.initialAnimationDirectionRight != texture.isFlipX();
	}
	
	private boolean isTextureLeft(TextureRegion texture) {
		//animationRight && texture.flip || !animationRight && !texture.flip
		return currentState.config.initialAnimationDirectionRight == texture.isFlipX();
	}
	
	public EnemyState getState(String id) {
		EnemyState state = states.get(id);
		if (state == null) {
			throw new IllegalArgumentException("The state '" + id + "' doesn't exist in this config (config file is '" + configFileName + "')");
		}
		return state;
	}
	
	public EnemyState getCurrentState() {
		return currentState;
	}
}
