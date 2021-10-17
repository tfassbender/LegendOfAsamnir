package net.jfabricationgames.gdx.character.ai;

import com.badlogic.gdx.math.Vector2;

import net.jfabricationgames.gdx.character.state.CharacterStateMachine;

public interface ArtificialIntelligenceCharacter {
	
	public CharacterStateMachine getStateMachine();
	
	public Vector2 getPosition();
	public float getMovingSpeed();
	
	public void moveTo(Vector2 targetPosition);
	public void moveTo(Vector2 targetPoint, float movementSpeedFactor);
	public void moveToDirection(Vector2 movementDirection, float movementSpeedFactor);
	
	public String getTypeAndPositionAsString();
	
	public boolean isRemovedFromMap();
}
