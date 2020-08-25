package net.jfabricationgames.gdx.enemy.ai;

import com.badlogic.gdx.utils.ArrayMap;

import net.jfabricationgames.gdx.enemy.ai.move.AIMove;
import net.jfabricationgames.gdx.enemy.ai.move.MoveType;

/**
 * The basis for the subAI field of the {@link AbstractArtificialIntelligence} class. <br>
 * The {@link BaseAI} is the lowest AI that doesn't do anything, but provides the moves map.
 */
public class BaseAI extends AbstractArtificialIntelligence implements ArtificialIntelligence {
	
	private ArrayMap<MoveType, AIMove> moves;
	
	public BaseAI() {
		moves = new ArrayMap<>();
	}
	
	@Override
	public AIMove getMove(MoveType moveType) {
		return moves.get(moveType);
	}
	
	@Override
	public void setMove(MoveType moveType, AIMove aiMove) {
		moves.put(moveType, aiMove);
	}
	
	@Override
	public void calculateMove() {}
	
	@Override
	public void executeMove() {}
}
