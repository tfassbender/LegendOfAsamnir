package net.jfabricationgames.gdx.enemy.ai;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Manifold;

import net.jfabricationgames.gdx.enemy.Enemy;
import net.jfabricationgames.gdx.enemy.ai.move.AIMove;
import net.jfabricationgames.gdx.enemy.ai.move.MoveType;

public abstract class AbstractArtificialIntelligence implements ArtificialIntelligence {
	
	/** The next sub-AI in the decorator chain */
	protected ArtificialIntelligence subAI;
	protected Enemy enemy;
	
	public AbstractArtificialIntelligence(ArtificialIntelligence subAI) {
		this.subAI = subAI;
	}
	
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
	
	public void setEnemy(Enemy enemy) {
		this.enemy = enemy;
		subAI.setEnemy(enemy);
	}
	
	@Override
	public void beginContact(Contact contact) {
		//delegate the event to the underlying AI where it might be handled
		subAI.beginContact(contact);
	}
	
	@Override
	public void endContact(Contact contact) {
		//delegate the event to the underlying AI where it might be handled
		subAI.endContact(contact);
	}
	
	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		//delegate the event to the underlying AI where it might be handled
		subAI.preSolve(contact, oldManifold);
	}
	
	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		//delegate the event to the underlying AI where it might be handled
		subAI.postSolve(contact, impulse);
	}
}
