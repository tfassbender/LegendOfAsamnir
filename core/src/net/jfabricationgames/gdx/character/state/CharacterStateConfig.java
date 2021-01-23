package net.jfabricationgames.gdx.character.state;

import com.badlogic.gdx.utils.Array;

public class CharacterStateConfig {
	
	public String id;
	public String animation;
	public String attack;
	public String stateEnteringSound;
	
	public boolean endsWithAnimation = true;
	public float changeStateAfterAnimationDelay = 0f;
	public String followingState;
	
	public Array<String> interruptingStates;
	
	public boolean flipAnimationToMovingDirection = true;
	public boolean flipAnimationOnEnteringOnly = false;
	public boolean initialAnimationDirectionRight = true;
}
