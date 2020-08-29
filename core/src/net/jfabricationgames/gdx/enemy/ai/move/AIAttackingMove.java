package net.jfabricationgames.gdx.enemy.ai.move;

import com.badlogic.gdx.math.Vector2;

import net.jfabricationgames.gdx.enemy.ai.ArtificialIntelligence;

public class AIAttackingMove extends AIMove {
	
	public AIAttackingMove(ArtificialIntelligence creatingAi) {
		super(creatingAi);
	}
	
	public Vector2 targetPosition;
}
