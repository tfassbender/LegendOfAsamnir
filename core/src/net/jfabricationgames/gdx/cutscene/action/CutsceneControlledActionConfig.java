package net.jfabricationgames.gdx.cutscene.action;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

import net.jfabricationgames.gdx.condition.choice.PlayerChoice;
import net.jfabricationgames.gdx.condition.execution.ConditionalExecution;
import net.jfabricationgames.gdx.cutscene.CutsceneControlledActionType;
import net.jfabricationgames.gdx.event.EventConfig;

public class CutsceneControlledActionConfig {
	
	public CutsceneControlledActionType type;
	public Array<String> executes;
	
	public boolean startsWithCutscene = false;
	public float executionDelayInSeconds = 0f;
	
	public EventConfig event;
	public String globalEvent;
	public boolean waitForEventToFinish; // only works for events that show an on-screen text; others will wait infinitely
	
	public PlayerChoice choice;
	public Array<String> choiceOptionExecutions;
	
	public ConditionalExecution condition;
	public ObjectMap<String, String> conditionOptionExecutions;
	
	public ObjectMap<String, String> executionParameters;
	
	public String controlledUnitId;
	public String controlledUnitState;
	public Vector2 controlledUnitAttackTargetDirection; // if the state that is activated is an attack state, there needs to be a target direction
	
	public Vector2 controlledUnitTarget;
	public float speedFactor = 1f;
	public String targetPositionRelativeToUnitId; // the id of the unit that the controlledUnitTarget vector is related to
	public boolean updatePositionRelativeToTarget = false;
	
	public boolean cameraFollowsTarget = true;
}
