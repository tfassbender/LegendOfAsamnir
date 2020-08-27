package net.jfabricationgames.gdx.enemy.state;

import com.badlogic.gdx.utils.Array;

public class EnemyStateConfig {
	
	public String id;
	public String animation;
	public String soundOnEntering;
	
	public boolean endsWithAnimation = true;
	public String followingState;
	
	public Array<String> interruptingStates;
}
