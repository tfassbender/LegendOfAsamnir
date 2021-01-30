package net.jfabricationgames.gdx.character.npc;

import net.jfabricationgames.gdx.character.ai.ArtificialIntelligenceConfig;

public class NonPlayableCharacterTypeConfig {
	
	public String animationsConfig;//the animation config file that is to be loaded (by the factory)
	public String stateConfig;//the state config file that is to be loaded (by the EnemyStateMachine)
	public String initialState;
	public ArtificialIntelligenceConfig aiConfig;
	
	public float bodyWidth = 0.7f;
	public float bodyHeight = 0.8f;
	public float movingSpeed = 1f;
	
	public boolean interactByContact = false;
	public float interactionMarkerOffsetX;
	public float interactionMarkerOffsetY;
	public String interactionEventId;
	
	public boolean addSensor = true;
	public float sensorRadius = 1f;
	
	public float imageOffsetX;
	public float imageOffsetY;
}
