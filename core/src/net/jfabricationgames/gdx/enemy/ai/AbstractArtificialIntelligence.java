package net.jfabricationgames.gdx.enemy.ai;

import net.jfabricationgames.gdx.enemy.ai.move.AIMove;
import net.jfabricationgames.gdx.enemy.ai.move.MoveType;

public abstract class AbstractArtificialIntelligence implements ArtificialIntelligence {
	
	/** The next sub-AI in the decorator chain */
	protected ArtificialIntelligence subAI;
	
	/**
	 * Get a move of a specific type in this AI-decorator-chain.<br>
	 * 
	 * The call is passed to the next subAI. The lowest AI in the subAI-list is a BaseAI, that overwrites this method and handles the request for all
	 * decorating AIs.
	 */
	@Override
	public AIMove getMove(MoveType moveType) {
		return subAI.getMove(moveType);
	}

	/**
	 * Set a move of a specific type in this AI-decorator-chain.<br>
	 * 
	 * The call is passed to the next subAI. The lowest AI in the subAI-list is a BaseAI, that overwrites this method and handles the request for all
	 * decorating AIs.
	 */
	@Override
	public void setMove(MoveType moveType, AIMove aiMove) {
		subAI.setMove(moveType, aiMove);
	}
}
