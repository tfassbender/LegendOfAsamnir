package net.jfabricationgames.gdx.character.ai;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.SerializationException;

import net.jfabricationgames.gdx.character.AbstractCharacter;
import net.jfabricationgames.gdx.character.ai.config.ArtificialIntelligenceConfig;
import net.jfabricationgames.gdx.character.ai.config.ArtificialIntelligenceConfig.StateConfig;
import net.jfabricationgames.gdx.character.ai.implementation.BackToStartingPointMovementAI;
import net.jfabricationgames.gdx.character.ai.implementation.FollowAI;
import net.jfabricationgames.gdx.character.ai.implementation.PreDefinedMovementAI;
import net.jfabricationgames.gdx.character.ai.implementation.RandomMovementAI;
import net.jfabricationgames.gdx.character.ai.implementation.RayCastFollowAI;
import net.jfabricationgames.gdx.character.ai.implementation.RunAwayAI;
import net.jfabricationgames.gdx.character.ai.util.timer.AttackTimer;
import net.jfabricationgames.gdx.character.ai.util.timer.AttackTimerConfig;
import net.jfabricationgames.gdx.character.ai.util.timer.AttackTimerFactory;
import net.jfabricationgames.gdx.character.animal.ai.ChangeStateWhenPlayerNearAI;
import net.jfabricationgames.gdx.character.animal.ai.RandomIdleStatesAI;
import net.jfabricationgames.gdx.character.animal.ai.RandomIdleStatesMovementAI;
import net.jfabricationgames.gdx.character.enemy.ai.ActionAI;
import net.jfabricationgames.gdx.character.enemy.ai.FastAttackFightAI;
import net.jfabricationgames.gdx.character.enemy.ai.FightAI;
import net.jfabricationgames.gdx.character.enemy.ai.MimicSurpriseAI;
import net.jfabricationgames.gdx.character.enemy.ai.TeamMovementAI;
import net.jfabricationgames.gdx.character.state.CharacterState;
import net.jfabricationgames.gdx.character.state.CharacterStateMachine;
import net.jfabricationgames.gdx.util.MapUtil;

public enum ArtificialIntelligenceType {
	
	//*****************************************************
	//*** Common AIs
	//*****************************************************
	
	BASE_AI {
		
		@Override
		public ArtificialIntelligence buildAI(ArtificialIntelligenceConfig aiConfig, CharacterStateMachine stateMachine,
				MapProperties mapProperties) {
			return new BaseAI();
		}
	},
	ACTION_AI {
		
		@Override
		public ArtificialIntelligence buildAI(ArtificialIntelligenceConfig aiConfig, CharacterStateMachine stateMachine,
				MapProperties mapProperties) {
			ArtificialIntelligence subAI = aiConfig.subAI.buildAI(stateMachine, mapProperties);
			CharacterState actionState = stateMachine.getState(aiConfig.stateNameAction);
			
			return new ActionAI(subAI, actionState, aiConfig.minDistToEnemy, aiConfig.maxDistToEnemy, aiConfig.timeBetweenActions);
		}
	},
	FOLLOW_AI {
		
		@Override
		public ArtificialIntelligence buildAI(ArtificialIntelligenceConfig aiConfig, CharacterStateMachine stateMachine,
				MapProperties mapProperties) {
			ArtificialIntelligence subAI = aiConfig.subAI.buildAI(stateMachine, mapProperties);
			CharacterState movingState = stateMachine.getState(aiConfig.stateNameMove);
			CharacterState idleState = stateMachine.getState(aiConfig.stateNameIdle);
			
			FollowAI ai = new FollowAI(subAI, movingState, idleState);
			ai.setMinDistanceToTarget(aiConfig.minDistanceToTargetPlayer);
			ai.setMaxDistanceFromStart(aiConfig.maxMoveDistance);
			ai.setMovementSpeedFactor(aiConfig.movementSpeedFactor);
			return ai;
		}
	},
	RAY_CAST_FOLLOW_AI {
		
		@Override
		public ArtificialIntelligence buildAI(ArtificialIntelligenceConfig aiConfig, CharacterStateMachine stateMachine,
				MapProperties mapProperties) {
			ArtificialIntelligence subAI = aiConfig.subAI.buildAI(stateMachine, mapProperties);
			CharacterState movingState = stateMachine.getState(aiConfig.stateNameMove);
			CharacterState idleState = stateMachine.getState(aiConfig.stateNameIdle);
			
			RayCastFollowAI ai = new RayCastFollowAI(subAI, movingState, idleState);
			ai.setMinDistanceToTarget(aiConfig.minDistanceToTargetPlayer);
			ai.setMovementSpeedFactor(aiConfig.movementSpeedFactor);
			return ai;
		}
	},
	PRE_DEFINED_MOVEMENT_AI {
		
		@Override
		public ArtificialIntelligence buildAI(ArtificialIntelligenceConfig aiConfig, CharacterStateMachine stateMachine,
				MapProperties mapProperties) {
			ArtificialIntelligence subAI = aiConfig.subAI.buildAI(stateMachine, mapProperties);
			CharacterState movingState = stateMachine.getState(aiConfig.stateNameMove);
			CharacterState idleState = stateMachine.getState(aiConfig.stateNameIdle);
			Array<Vector2> positions = loadPositionsFromMapProperties(mapProperties);
			
			PreDefinedMovementAI ai = new PreDefinedMovementAI(subAI, movingState, idleState, aiConfig.useRelativePositions, positions);
			ai.setDistanceToKeepFromPlayer(aiConfig.distanceToKeepFromPlayer);
			ai.setMovementSpeedFactor(aiConfig.movementSpeedFactor);
			return ai;
		}
	},
	RANDOM_MOVEMENT_AI {
		
		@Override
		public ArtificialIntelligence buildAI(ArtificialIntelligenceConfig aiConfig, CharacterStateMachine stateMachine,
				MapProperties mapProperties) {
			ArtificialIntelligence subAI = aiConfig.subAI.buildAI(stateMachine, mapProperties);
			CharacterState movingState = stateMachine.getState(aiConfig.stateNameMove);
			CharacterState idleState = stateMachine.getState(aiConfig.stateNameIdle);
			float maxDistance = aiConfig.maxMoveDistance;
			
			String maxDistanceString = mapProperties.get(AbstractCharacter.MAP_PROPERTIES_KEY_MAX_MOVE_DISTANCE, String.class);
			if (maxDistanceString != null && !maxDistanceString.isEmpty()) {
				maxDistance = Float.parseFloat(maxDistanceString);
			}
			
			RandomMovementAI ai = new RandomMovementAI(subAI, movingState, idleState, maxDistance);
			ai.setDistanceToKeepFromPlayer(aiConfig.distanceToKeepFromPlayer);
			ai.setMovementSpeedFactor(aiConfig.movementSpeedFactor);
			return ai;
		}
	},
	BACK_TO_STARTING_POSITION_MOVEMENT_AI {
		
		@Override
		public ArtificialIntelligence buildAI(ArtificialIntelligenceConfig aiConfig, CharacterStateMachine stateMachine,
				MapProperties mapProperties) {
			ArtificialIntelligence subAI = aiConfig.subAI.buildAI(stateMachine, mapProperties);
			CharacterState movingState = stateMachine.getState(aiConfig.stateNameMove);
			CharacterState idleState = stateMachine.getState(aiConfig.stateNameIdle);
			
			BackToStartingPointMovementAI ai = new BackToStartingPointMovementAI(subAI, movingState, idleState);
			ai.setMovementSpeedFactor(aiConfig.movementSpeedFactor);
			return ai;
		}
	},
	RUN_AWAY_AI {
		
		@Override
		public ArtificialIntelligence buildAI(ArtificialIntelligenceConfig aiConfig, CharacterStateMachine stateMachine,
				MapProperties mapProperties) {
			ArtificialIntelligence subAI = aiConfig.subAI.buildAI(stateMachine, mapProperties);
			CharacterState movingState = stateMachine.getState(aiConfig.stateNameMove);
			CharacterState idleState = stateMachine.getState(aiConfig.stateNameIdle);
			
			RunAwayAI ai = new RunAwayAI(subAI, movingState, idleState);
			ai.setDistanceToStopRunning(aiConfig.distanceToStopRunning);
			ai.setDistanceToKeepFromPlayer(aiConfig.distanceToKeepFromPlayer);
			ai.setMovementSpeedFactor(aiConfig.movementSpeedFactor);
			return ai;
		}
	},
	
	//*****************************************************
	//*** Enemy AIs
	//*****************************************************
	
	FAST_ATTACK_FIGHT_AI {
		
		@Override
		public ArtificialIntelligence buildAI(ArtificialIntelligenceConfig aiConfig, CharacterStateMachine stateMachine,
				MapProperties mapProperties) {
			ArtificialIntelligence subAI = aiConfig.subAI.buildAI(stateMachine, mapProperties);
			CharacterState attackState = stateMachine.getState(aiConfig.stateNameAttack);
			AttackTimer attackTimer = createAttackTimer(aiConfig.attackTimerConfig);
			
			return new FastAttackFightAI(subAI, attackState, attackTimer, aiConfig.attackDistance, aiConfig.attackSpeedFactor,
					aiConfig.attackSpeedDelay);
		}
	},
	FIGHT_AI {
		
		@Override
		public ArtificialIntelligence buildAI(ArtificialIntelligenceConfig aiConfig, CharacterStateMachine stateMachine,
				MapProperties mapProperties) {
			ArtificialIntelligence subAI = aiConfig.subAI.buildAI(stateMachine, mapProperties);
			CharacterState attackState = stateMachine.getState(aiConfig.stateNameAttack);
			AttackTimer attackTimer = createAttackTimer(aiConfig.attackTimerConfig);
			
			FightAI ai = new FightAI(subAI, attackState, attackTimer, aiConfig.attackDistance);
			ai.setMinDistanceToTargetPlayer(aiConfig.minDistanceToTargetPlayer);
			return ai;
		}
	},
	MIMIC_SUPRISE_AI {
		
		@Override
		public ArtificialIntelligence buildAI(ArtificialIntelligenceConfig aiConfig, CharacterStateMachine stateMachine,
				MapProperties mapProperties) {
			ArtificialIntelligence subAI = aiConfig.subAI.buildAI(stateMachine, mapProperties);
			CharacterState waitingState = stateMachine.getState(aiConfig.stateNameWait);
			CharacterState surpriseState = stateMachine.getState(aiConfig.stateNameSurprise);
			
			return new MimicSurpriseAI(subAI, waitingState, surpriseState, aiConfig.attackDistance);
		}
	},
	TEAM_MOVEMENT_AI {
		
		@Override
		public ArtificialIntelligence buildAI(ArtificialIntelligenceConfig aiConfig, CharacterStateMachine stateMachine,
				MapProperties mapProperties) {
			ArtificialIntelligence subAI = aiConfig.subAI.buildAI(stateMachine, mapProperties);
			CharacterState movingState = stateMachine.getState(aiConfig.stateNameMove);
			CharacterState idleState = stateMachine.getState(aiConfig.stateNameIdle);
			String teamId = mapProperties.get("teamId", String.class);
			
			TeamMovementAI ai = new TeamMovementAI(subAI, movingState, idleState, aiConfig.distanceToInformTeamMates, teamId);
			ai.setMovementSpeedFactor(aiConfig.movementSpeedFactor);
			return ai;
		}
	},
	
	//*****************************************************
	//*** Animal AIs
	//*****************************************************
	
	RANDOM_IDLE_STATES_AI {
		
		@Override
		public ArtificialIntelligence buildAI(ArtificialIntelligenceConfig aiConfig, CharacterStateMachine stateMachine,
				MapProperties mapProperties) {
			ArtificialIntelligence subAI = aiConfig.subAI.buildAI(stateMachine, mapProperties);
			ObjectMap<CharacterState, StateConfig> idleStates = new ObjectMap<>();
			for (String stateName : aiConfig.idleStates.keys()) {
				idleStates.put(stateMachine.getState(stateName), aiConfig.idleStates.get(stateName));
			}
			
			return new RandomIdleStatesAI(subAI, idleStates);
		}
		
	},
	RANDOM_IDLE_STATES_MOVEMENT_AI {
		
		@Override
		public ArtificialIntelligence buildAI(ArtificialIntelligenceConfig aiConfig, CharacterStateMachine stateMachine,
				MapProperties mapProperties) {
			ArtificialIntelligence subAI = aiConfig.subAI.buildAI(stateMachine, mapProperties);
			ObjectMap<CharacterState, StateConfig> idleStates = new ObjectMap<>();
			for (String stateName : aiConfig.idleStates.keys()) {
				idleStates.put(stateMachine.getState(stateName), aiConfig.idleStates.get(stateName));
			}
			
			CharacterState movingState = stateMachine.getState(aiConfig.stateNameMove);
			CharacterState idleState = stateMachine.getState(aiConfig.stateNameIdle);
			float maxDistance = aiConfig.maxMoveDistance;
			float movementProbability = aiConfig.movementProbability;
			
			String maxDistanceString = mapProperties.get(AbstractCharacter.MAP_PROPERTIES_KEY_MAX_MOVE_DISTANCE, String.class);
			if (maxDistanceString != null && !maxDistanceString.isEmpty()) {
				maxDistance = Float.parseFloat(maxDistanceString);
			}
			
			RandomMovementAI ai = new RandomIdleStatesMovementAI(subAI, idleStates, movementProbability, movingState, idleState, maxDistance);
			ai.setDistanceToKeepFromPlayer(aiConfig.distanceToKeepFromPlayer);
			ai.setMovementSpeedFactor(aiConfig.movementSpeedFactor);
			return ai;
		}
	},
	CHANGE_STATE_WHEN_PLAYER_NEAR_AI {
		
		@Override
		public ArtificialIntelligence buildAI(ArtificialIntelligenceConfig aiConfig, CharacterStateMachine stateMachine,
				MapProperties mapProperties) {
			ArtificialIntelligence subAI = aiConfig.subAI.buildAI(stateMachine, mapProperties);
			CharacterState playerNearState = stateMachine.getState(aiConfig.stateNameAction);
			CharacterState playerLeavingState = null;
			if (aiConfig.stateNameAction2 != null) {
				playerLeavingState = stateMachine.getState(aiConfig.stateNameAction2);
			}
			float distanceToChangeState = aiConfig.minDistanceToTargetPlayer;
			
			return new ChangeStateWhenPlayerNearAI(subAI, playerNearState, playerLeavingState, distanceToChangeState);
		}
	};
	
	public abstract ArtificialIntelligence buildAI(ArtificialIntelligenceConfig aiConfig, CharacterStateMachine stateMachine,
			MapProperties mapProperties);
	
	@SuppressWarnings("unchecked")
	private static Array<Vector2> loadPositionsFromMapProperties(MapProperties mapProperties) {
		String predefinedMovingPositions = mapProperties.get(AbstractCharacter.MAP_PROPERTIES_KEY_PREDEFINED_MOVEMENT_POSITIONS, String.class);
		if (predefinedMovingPositions != null) {
			try {
				Json json = new Json();
				return json.fromJson(Array.class, Vector2.class, predefinedMovingPositions);
			}
			catch (SerializationException e) {
				throw new IllegalStateException("A predefined movement string could not be parsed: \"" + predefinedMovingPositions
						+ "\". Complete map properties: " + MapUtil.mapPropertiesToString(mapProperties, true), e);
			}
		}
		return null;
	}
	
	private static AttackTimer createAttackTimer(AttackTimerConfig config) {
		return AttackTimerFactory.createAttackTimer(config);
	}
}
