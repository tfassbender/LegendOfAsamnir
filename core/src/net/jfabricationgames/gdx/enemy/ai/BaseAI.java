package net.jfabricationgames.gdx.enemy.ai;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.utils.ArrayMap;

import net.jfabricationgames.gdx.enemy.Enemy;
import net.jfabricationgames.gdx.enemy.ai.move.AIMove;
import net.jfabricationgames.gdx.enemy.ai.move.MoveType;

/**
 * The basis for the subAI field of the {@link AbstractArtificialIntelligence} class. <br>
 * The {@link BaseAI} is the lowest AI that doesn't do anything, but provides the moves map.
 */
public class BaseAI extends AbstractArtificialIntelligence implements ArtificialIntelligence {
	
	private ArrayMap<MoveType, AIMove> moves;
	
	public BaseAI() {
		super(null);
		moves = new ArrayMap<>();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T extends AIMove> T getMove(MoveType moveType, Class<T> clazz) {
		AIMove move = moves.get(moveType);
		if (move != null && move.getClass().isAssignableFrom(clazz)) {
			return (T) move;
		}
		return null;
	}
	
	@Override
	public void setMove(MoveType moveType, AIMove aiMove) {
		moves.put(moveType, aiMove);
	}
	
	@Override
	public void calculateMove(float delta) {}
	
	@Override
	public void executeMove() {}
	
	@Override
	public void setEnemy(Enemy enemy) {}
	
	@Override
	public void beginContact(Contact contact) {}
	
	@Override
	public void endContact(Contact contact) {}
	
	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {}
	
	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {}
}
