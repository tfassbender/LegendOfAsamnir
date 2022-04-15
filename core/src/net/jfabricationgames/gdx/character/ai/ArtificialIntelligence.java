package net.jfabricationgames.gdx.character.ai;

import com.badlogic.gdx.physics.box2d.ContactListener;

import net.jfabricationgames.gdx.attack.AttackHandler;
import net.jfabricationgames.gdx.character.ai.move.AIMove;
import net.jfabricationgames.gdx.character.ai.move.MoveType;

/**
 * An Artificial Intelligence that is build using a decorator pattern, so multiple AI functions can be combined into one.<br>
 * 
 * NOTE: for the decorator pattern to work, the calculateMove method of the decorated (sub) AI has to be called BEFORE calculating the move of the
 * docorating (super) AI. The same way the executeMove() method of the decorated (sub) AI has to be called right AFTER executing the move of the
 * decorating (super) AI.
 */
public interface ArtificialIntelligence extends ContactListener {
	
	/**
	 * Calculate a move and add store it using setMove(MoveType, AIMove).
	 */
	public void calculateMove(float delta);
	/**
	 * Request a Move using the getMove(MoveType) method and execute it (if it has not yet been executed).
	 */
	public void executeMove(float delta);
	
	public void setMove(MoveType moveType, AIMove aiMove);
	public <T extends AIMove> T getMove(MoveType moveType, Class<T> clazz);
	
	public void setCharacter(ArtificialIntelligenceCharacter character);
	
	public default void setAttackHandler(AttackHandler attackHandler) {}
	
	public default void characterRemovedFromMap() {}
}
