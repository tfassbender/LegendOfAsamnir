package net.jfabricationgames.gdx.cutscene;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class CutsceneControlledActionConfig {
	
	public CutsceneControlledActionType type;
	public Array<String> executes;
	
	public boolean startsWithCutscene = false;
	public float executionDelayInSeconds = 0f;
	
	public String globalEvent;
	
	public String controlledUnitId;
	public String controlledUnitState;
	public Vector2 controlledUnitAttackTargetDirection; // if the state that is activated is an attack state, there needs to be a target direction
	
	public Vector2 controlledUnitTarget;
	public String targetPositionRelativeToUnitId; // the id of the unit that the controlledUnitTarget vector is related to
	public boolean updatePositionRelativeToTarget = false;
}
