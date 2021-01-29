package net.jfabricationgames.gdx.character.npc;

import net.jfabricationgames.gdx.character.ai.ArtificialIntelligenceConfig;

public class NonPlayableCharacterTypeConfig {
	
	public String animationsConfig;//the animation config file that is to be loaded (by the factory)
	public String stateConfig;//the state config file that is to be loaded (by the EnemyStateMachine)
	public String initialState;
	public ArtificialIntelligenceConfig aiConfig;
	
	public float bodyWidth = 1f;
	public float bodyHeight = 1.5f;
}
