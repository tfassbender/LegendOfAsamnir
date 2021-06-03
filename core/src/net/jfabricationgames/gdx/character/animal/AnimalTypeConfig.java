package net.jfabricationgames.gdx.character.animal;

import net.jfabricationgames.gdx.character.CharacterTypeConfig;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyShape;

public class AnimalTypeConfig extends CharacterTypeConfig {
	
	public String animationsConfig;//the animation config file that is to be loaded (by the factory)
	public String stateConfig;//the state config file that is to be loaded (by the StateMachine)
	public String initialState;
	public String aiConfig;
	
	public float movingSpeed = 1f;
	
	public float imageOffsetX;
	public float imageOffsetY;
	
	public PhysicsBodyShape bodyShape = PhysicsBodyShape.CIRCLE;
	public float bodyRadius;
	public float bodyWidth;
	public float bodyHeight;
	
	public boolean addSensor = true;
	public float sensorRadius;
}
