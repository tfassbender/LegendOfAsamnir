package net.jfabricationgames.gdx.character.animal.ai;

import java.util.Random;

import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;

import net.jfabricationgames.gdx.character.ai.AbstractArtificialIntelligence;
import net.jfabricationgames.gdx.character.ai.ArtificialIntelligence;
import net.jfabricationgames.gdx.character.ai.ArtificialIntelligenceConfig.StateConfig;
import net.jfabricationgames.gdx.character.ai.move.AIActionMove;
import net.jfabricationgames.gdx.character.ai.move.AIMove;
import net.jfabricationgames.gdx.character.ai.move.MoveType;
import net.jfabricationgames.gdx.character.state.CharacterState;

public class RandomIdleStatesAI extends AbstractArtificialIntelligence {
	
	private ObjectMap<CharacterState, StateConfig> idleStateProbabilities;
	
	private Random random;
	
	public RandomIdleStatesAI(ArtificialIntelligence subAI, ObjectMap<CharacterState, StateConfig> idleStateProbabilities) {
		super(subAI);
		this.idleStateProbabilities = idleStateProbabilities;
		
		random = new Random();
	}
	
	@Override
	public void calculateMove(float delta) {
		subAI.calculateMove(delta);
		
		if (!inIdleState() || (inIdleState() && !isOverridingFollowingStateSet())) {
			AIMove move = new AIActionMove(this);
			//use the MoveType MOVE to overwrite moving actions of sub AIs and be overwritten by super AIs moving actions
			setMove(MoveType.MOVE, move);
		}
	}
	
	private boolean inIdleState() {
		return idleStateProbabilities.containsKey(stateMachine.getCurrentState());
	}
	
	private boolean isOverridingFollowingStateSet() {
		return stateMachine.isOverridingFollowingStateSet();
	}
	
	@Override
	public void executeMove(float delta) {
		AIActionMove aiMove = getMove(MoveType.MOVE, AIActionMove.class);
		if (isExecutedByMe(aiMove)) {
			if (inIdleState()) {
				if (!isOverridingFollowingStateSet()) {
					setOverridingFollowingState();
				}
			}
			else {
				changeToIdleState();
			}
		}
		
		subAI.executeMove(delta);
	}
	
	private void setOverridingFollowingState() {
		Entry<CharacterState, StateConfig> randomIdleState = getRandomIdleState();
		int repetitions = getRandomRepetitions(randomIdleState.value);
		
		stateMachine.setOverridingFollowingState(randomIdleState.key, repetitions);
	}
	
	private Entry<CharacterState, StateConfig> getRandomIdleState() {
		float randomValue = random.nextFloat();
		float summedProbabilities = 0f;
		
		for (Entry<CharacterState, StateConfig> idleStateProbability : idleStateProbabilities) {
			summedProbabilities += idleStateProbability.value.probability;
			if (randomValue < summedProbabilities) {
				return idleStateProbability;
			}
		}
		
		throw new IllegalStateException("No random idle state was chosen. Maybe the probabilities don't sum up to 1?");
	}
	
	private int getRandomRepetitions(StateConfig config) {
		if (config.minRepetitions == config.maxRepetitions) {
			return config.minRepetitions;
		}
		
		return config.minRepetitions + random.nextInt(config.maxRepetitions - config.minRepetitions);
	}
	
	private void changeToIdleState() {
		Entry<CharacterState, StateConfig> randomIdleState = getRandomIdleState();
		int repetitions = getRandomRepetitions(randomIdleState.value);
		
		stateMachine.setState(randomIdleState.key);
		stateMachine.setOverridingFollowingState(randomIdleState.key, repetitions - 1);
	}
}
