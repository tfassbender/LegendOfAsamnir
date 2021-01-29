package net.jfabricationgames.gdx.character.ai;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.SerializationException;

import net.jfabricationgames.gdx.character.AbstractCharacter;
import net.jfabricationgames.gdx.character.ai.implementation.ActionAI;
import net.jfabricationgames.gdx.character.ai.implementation.FollowAI;
import net.jfabricationgames.gdx.character.ai.implementation.PreDefinedMovementAI;
import net.jfabricationgames.gdx.character.ai.implementation.RunAwayAI;
import net.jfabricationgames.gdx.character.ai.util.timer.AttackTimer;
import net.jfabricationgames.gdx.character.ai.util.timer.AttackTimerConfig;
import net.jfabricationgames.gdx.character.ai.util.timer.AttackTimerFactory;
import net.jfabricationgames.gdx.character.enemy.ai.FastAttackFightAI;
import net.jfabricationgames.gdx.character.enemy.ai.FightAI;
import net.jfabricationgames.gdx.character.enemy.ai.MimicSurpriseAI;
import net.jfabricationgames.gdx.character.npc.ai.PlayerContactAI;
import net.jfabricationgames.gdx.character.npc.ai.PlayerInteractionAI;
import net.jfabricationgames.gdx.character.state.CharacterState;
import net.jfabricationgames.gdx.character.state.CharacterStateMachine;
import net.jfabricationgames.gdx.map.TiledMapLoader;

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
			
			return new FollowAI(subAI, movingState, idleState);
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
			
			return new PreDefinedMovementAI(subAI, movingState, idleState, aiConfig.useRelativePositions, positions);
		}
	},
	RUN_AWAY_AI {
		
		@Override
		public ArtificialIntelligence buildAI(ArtificialIntelligenceConfig aiConfig, CharacterStateMachine stateMachine,
				MapProperties mapProperties) {
			ArtificialIntelligence subAI = aiConfig.subAI.buildAI(stateMachine, mapProperties);
			CharacterState movingState = stateMachine.getState(aiConfig.stateNameMove);
			CharacterState idleState = stateMachine.getState(aiConfig.stateNameIdle);
			
			return new RunAwayAI(subAI, movingState, idleState);
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
			
			return new FightAI(subAI, attackState, attackTimer, aiConfig.attackDistance);
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
	
	//*****************************************************
	//*** NPC AIs
	//*****************************************************
	
	PLAYER_CONTACT_AI {
		
		@Override
		public ArtificialIntelligence buildAI(ArtificialIntelligenceConfig aiConfig, CharacterStateMachine stateMachine,
				MapProperties mapProperties) {
			ArtificialIntelligence subAI = aiConfig.subAI.buildAI(stateMachine, mapProperties);
			
			return new PlayerContactAI(subAI);
		}
	},
	PLAYER_INTERACTION_AI {
		
		@Override
		public ArtificialIntelligence buildAI(ArtificialIntelligenceConfig aiConfig, CharacterStateMachine stateMachine,
				MapProperties mapProperties) {
			ArtificialIntelligence subAI = aiConfig.subAI.buildAI(stateMachine, mapProperties);
			
			return new PlayerInteractionAI(subAI);
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
				return (Array<Vector2>) json.fromJson(Array.class, Vector2.class, predefinedMovingPositions);
			}
			catch (SerializationException e) {
				throw new IllegalStateException("A predefined movement string could not be parsed: \"" + predefinedMovingPositions
						+ "\". Complete map properties: " + TiledMapLoader.mapPropertiesToString(mapProperties, true), e);
			}
		}
		return null;
	}
	
	private static AttackTimer createAttackTimer(AttackTimerConfig config) {
		return AttackTimerFactory.createAttackTimer(config);
	}
}
