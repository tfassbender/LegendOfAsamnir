package net.jfabricationgames.gdx.enemy.state;

import com.badlogic.gdx.utils.Array;

public class EnemyStateConfig {
	
	public String id;
	public String animation;
	public String attack;
	public String stateEnteringSound;
	
	public boolean endsWithAnimation = true;
	public String followingState;
	
	public Array<String> interruptingStates;
	
	public boolean flipAnimationToMovingDirection = true;
	public boolean flipAnimationOnEnteringOnly = false;
	public boolean initialAnimationDirectionRight = true;
}
