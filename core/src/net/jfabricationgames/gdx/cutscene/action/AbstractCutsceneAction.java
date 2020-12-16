package net.jfabricationgames.gdx.cutscene.action;

import com.badlogic.gdx.utils.Array;

import net.jfabricationgames.gdx.cutscene.CutsceneControlledActionConfig;
import net.jfabricationgames.gdx.map.GameMap;

public abstract class AbstractCutsceneAction {
	
	public static final String CONTROLLED_UNIT_ID_PLAYER = "PLAYER";
	
	protected static GameMap gameMap;
	
	public static void setGameMap(GameMap map) {
		gameMap = map;
	}
	
	protected CutsceneControlledActionConfig actionConfig;
	protected float executionTimeInSeconds = 0f;
	
	public AbstractCutsceneAction(CutsceneControlledActionConfig actionConfig) {
		this.actionConfig = actionConfig;
	}
	
	public abstract void execute();
	public abstract boolean isExecutionFinished();
	
	public void increaseExecutionTime(float delta) {
		executionTimeInSeconds += delta;
	}
	
	public boolean isExecutionDelayPassed() {
		return executionTimeInSeconds >= actionConfig.executionDelayInSeconds;
	}
	
	public Array<String> getFollowingActions() {
		return actionConfig.executes;
	}
	
	protected <T> T getControlledUnitAs(Class<T> clazz) {
		return getUnitAs(actionConfig.controlledUnitId, clazz);
	}
	
	protected <T> T getUnitAs(String unitId, Class<T> clazz) {
		Object controlledUnit = null;
		if (unitId.equals(CONTROLLED_UNIT_ID_PLAYER)) {
			controlledUnit = gameMap.getPlayer();
		}
		else {
			controlledUnit = gameMap.getUnitById(unitId);
		}
		
		if (controlledUnit == null) {
			throw new IllegalStateException("The controlled unit with the id '" + unitId + "' was not found.");
		}
		if (!clazz.isAssignableFrom(controlledUnit.getClass())) {
			throw new IllegalStateException(
					"The controlled unit with the id '" + unitId + "' cannot be cast to the requested class '" + clazz.getSimpleName() + "'");
		}
		return clazz.cast(controlledUnit);
	}
}
