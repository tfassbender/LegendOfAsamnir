package net.jfabricationgames.gdx.cutscene.action;

import com.badlogic.gdx.math.Vector2;

import net.jfabricationgames.gdx.cutscene.CutsceneControlledActionConfig;

public abstract class AbstractCutsceneMoveAction extends AbstractCutsceneAction {
	
	public static final float MAX_DISTANCE_TO_TARGET_POINT = 0.1f;
	
	protected Vector2 target;
	
	public AbstractCutsceneMoveAction(CutsceneControlledActionConfig actionConfig) {
		super(actionConfig);
	}
	
	protected void findTarget() {
		if (actionConfig.controlledUnitId != null && actionConfig.controlledUnitId.equals(actionConfig.targetPositionRelativeToUnitId)
				&& actionConfig.updatePositionRelativeToTarget) {
			throw new IllegalStateException(
					"The target position can't be relative to the controlled unit AND be updated. The unit will never reach the target.");
		}
		
		CutscenePositioningUnit unit;
		if (actionConfig.targetPositionRelativeToUnitId != null) {
			unit = getUnitAs(actionConfig.targetPositionRelativeToUnitId, CutscenePositioningUnit.class);
		}
		else {
			unit = getControlledUnitAs(CutscenePositioningUnit.class);
		}
		
		target = unit.getPosition().cpy().add(actionConfig.controlledUnitTarget);
	}
}