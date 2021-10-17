package net.jfabricationgames.gdx.character.animal.ai;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;

import net.jfabricationgames.gdx.character.ai.ArtificialIntelligenceCharacter;
import net.jfabricationgames.gdx.character.ai.config.ArtificialIntelligenceStateConfig;
import net.jfabricationgames.gdx.character.state.CharacterState;
import net.jfabricationgames.gdx.character.state.CharacterStateMachine;

public class RandomIdleStatesHandler {
	
	private ArtificialIntelligenceCharacter character;
	private CharacterStateMachine stateMachine;
	
	private ObjectMap<CharacterState, ArtificialIntelligenceStateConfig> idleStateProbabilities;
	private Random random;
	
	public RandomIdleStatesHandler(ArtificialIntelligenceCharacter character, CharacterStateMachine stateMachine,
			ObjectMap<CharacterState, ArtificialIntelligenceStateConfig> idleStateProbabilities) {
		this.character = character;
		this.stateMachine = stateMachine;
		this.idleStateProbabilities = idleStateProbabilities;
		random = new Random();
	}
	
	public boolean isInRandomIdleState() {
		return idleStateProbabilities.containsKey(stateMachine.getCurrentState());
	}
	
	public boolean isOverridingFollowingStateSet() {
		return stateMachine.isOverridingFollowingStateSet();
	}
	
	public void setOverridingFollowingState() {
		Entry<CharacterState, ArtificialIntelligenceStateConfig> randomIdleState = getRandomIdleState();
		if (randomIdleState != null) {
			int repetitions = getRandomRepetitions(randomIdleState.value);
			
			stateMachine.setOverridingFollowingState(randomIdleState.key, repetitions);
		}
	}
	
	private Entry<CharacterState, ArtificialIntelligenceStateConfig> getRandomIdleState() {
		float randomValue = random.nextFloat();
		float summedProbabilities = 0f;
		
		for (Entry<CharacterState, ArtificialIntelligenceStateConfig> idleStateProbability : idleStateProbabilities) {
			summedProbabilities += idleStateProbability.value.probability;
			if (randomValue < summedProbabilities) {
				return idleStateProbability;
			}
		}
		
		Gdx.app.error(getClass().getSimpleName(),
				"No random idle state was chosen. Maybe the probabilities don't sum up to 1? " + character.getTypeAndPositionAsString());
		return null;
	}
	
	private int getRandomRepetitions(ArtificialIntelligenceStateConfig config) {
		if (config.minRepetitions == config.maxRepetitions) {
			return config.minRepetitions;
		}
		
		return config.minRepetitions + random.nextInt(config.maxRepetitions - config.minRepetitions);
	}
	
	public void changeToRandomIdleState() {
		Entry<CharacterState, ArtificialIntelligenceStateConfig> randomIdleState = getRandomIdleState();
		if (randomIdleState != null) {
			int repetitions = getRandomRepetitions(randomIdleState.value);
			
			stateMachine.setState(randomIdleState.key);
			stateMachine.setOverridingFollowingState(randomIdleState.key, repetitions - 1);
		}
	}
}
