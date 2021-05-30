package net.jfabricationgames.gdx.character.npc;

import net.jfabricationgames.gdx.character.ai.config.ArtificialIntelligenceConfig;

public class NonPlayableCharacterTypeConfig {
	
	public ArtificialIntelligenceConfig aiConfig;
	
	// the graphics configuration can either be done in the main config file, or in a separated file that is referenced by the graphicsConfigFile field.
	// the graphics configuration is loaded automatically in both cases.
	public String graphicsConfigFile;
	public NonPlayableCharacterGraphicsConfig graphicsConfig;
	
	public boolean interactByContact = false;
	public String interactionEventId;
	
	public boolean addSensor = true;
	public float sensorRadius = 1f;
	
	public boolean interactionPossible = true;
	public float movingSpeed = 1f;
}
