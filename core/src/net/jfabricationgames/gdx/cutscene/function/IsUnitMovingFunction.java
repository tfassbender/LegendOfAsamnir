package net.jfabricationgames.gdx.cutscene.function;

import java.util.function.Function;

import com.badlogic.gdx.utils.Array;

import net.jfabricationgames.gdx.cutscene.action.AbstractCutsceneAction;
import net.jfabricationgames.gdx.cutscene.action.CutsceneMoveAction;

public class IsUnitMovingFunction implements Function<String, Boolean> {
	
	private Array<AbstractCutsceneAction> executedActions;
	
	public IsUnitMovingFunction(Array<AbstractCutsceneAction> executedActions) {
		this.executedActions = executedActions;
	}
	
	@Override
	public Boolean apply(String controlledUnitId) {
		for (int i = 0; i < executedActions.size; i++) {// don't use a for-each loop here, because the iterator won't work (because of the nested for loop)
			AbstractCutsceneAction action = executedActions.get(i);
			if (action instanceof CutsceneMoveAction) {
				if (controlledUnitId.equals(action.getActionConfig().controlledUnitId)) {
					return true;
				}
			}
		}
		return false;
	}
}
