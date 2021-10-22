package net.jfabricationgames.gdx.character.animal.ai;

import com.badlogic.gdx.utils.ObjectMap;

import net.jfabricationgames.gdx.character.ai.AbstractArtificialIntelligence;
import net.jfabricationgames.gdx.character.ai.ArtificialIntelligence;
import net.jfabricationgames.gdx.character.ai.ArtificialIntelligenceCharacter;
import net.jfabricationgames.gdx.character.ai.ArtificialIntelligenceStateConfig;
import net.jfabricationgames.gdx.character.ai.move.AIActionMove;
import net.jfabricationgames.gdx.character.ai.move.AIMove;
import net.jfabricationgames.gdx.character.ai.move.MoveType;
import net.jfabricationgames.gdx.character.state.CharacterState;

public class RandomIdleStatesAI extends AbstractArtificialIntelligence {
	
	private RandomIdleStatesHandler handler;
	private ObjectMap<CharacterState, ArtificialIntelligenceStateConfig> idleStateProbabilities;
	
	public RandomIdleStatesAI(ArtificialIntelligence subAI, ObjectMap<CharacterState, ArtificialIntelligenceStateConfig> idleStateProbabilities) {
		super(subAI);
		this.idleStateProbabilities = idleStateProbabilities;
	}
	
	@Override
	public void setCharacter(ArtificialIntelligenceCharacter character) {
		super.setCharacter(character);
		handler = new RandomIdleStatesHandler(character, stateMachine, idleStateProbabilities);
	}
	
	@Override
	public void calculateMove(float delta) {
		subAI.calculateMove(delta);
		
		if (!handler.isInRandomIdleState() || (handler.isInRandomIdleState() && !handler.isOverridingFollowingStateSet())) {
			AIMove move = new AIActionMove(this);
			//use the MoveType MOVE to overwrite moving actions of sub AIs and be overwritten by super AIs moving actions
			setMove(MoveType.MOVE, move);
		}
	}
	
	@Override
	public void executeMove(float delta) {
		AIActionMove aiMove = getMove(MoveType.MOVE, AIActionMove.class);
		if (isExecutedByMe(aiMove)) {
			if (handler.isInRandomIdleState()) {
				if (!handler.isOverridingFollowingStateSet()) {
					handler.setOverridingFollowingState();
				}
			}
			else {
				handler.changeToRandomIdleState();
			}
		}
		
		subAI.executeMove(delta);
	}
}
