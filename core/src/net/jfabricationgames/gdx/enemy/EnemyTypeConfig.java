package net.jfabricationgames.gdx.enemy;

import java.util.HashMap;

public class EnemyTypeConfig {
	
	public HashMap<String, String> animations;
	public String animationsConfig;//the animation config file that is to be loaded (by the factory)
	
	public HashMap<String, String> sounds;
	
	public float health;
	
	//TODO more parameters
}
