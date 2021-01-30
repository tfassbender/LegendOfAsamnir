package net.jfabricationgames.gdx.character.npc;

public class NonPlayableCharacterGraphicsConfig {
	
	public String animationsConfig;//the animation config file that is to be loaded (by the factory)
	
	public String stateConfig;//the state config file that is to be loaded (by the EnemyStateMachine)
	public String initialState;
	
	public float bodyWidth = 0.7f;
	public float bodyHeight = 0.8f;
	
	public float interactionMarkerOffsetX;
	public float interactionMarkerOffsetY;
	
	public float imageOffsetX;
	public float imageOffsetY;
}
