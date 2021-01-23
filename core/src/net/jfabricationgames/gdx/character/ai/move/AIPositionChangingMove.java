package net.jfabricationgames.gdx.character.ai.move;

import com.badlogic.gdx.math.Vector2;

import net.jfabricationgames.gdx.character.ai.ArtificialIntelligence;

public class AIPositionChangingMove extends AIMove {
	
	public Vector2 movementTarget;
	public Vector2 movementDirection;
	
	public AIPositionChangingMove(ArtificialIntelligence creatingAi) {
		super(creatingAi);
	}
}
