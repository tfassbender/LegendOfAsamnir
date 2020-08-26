package net.jfabricationgames.gdx.enemy.ai.move;

import com.badlogic.gdx.math.Vector2;

import net.jfabricationgames.gdx.enemy.ai.ArtificialIntelligence;

public class AIPositionChangingMove extends AIMove {
	
	public Vector2 movementTarget;
	public Vector2 movementDirection;
	
	public AIPositionChangingMove(ArtificialIntelligence creatingAi) {
		super(creatingAi);
	}
}
