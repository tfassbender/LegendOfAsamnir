package net.jfabricationgames.gdx.cutscene.function;

import java.util.function.Function;

import com.badlogic.gdx.utils.Array;

public class IsUnitMovingFunction implements Function<String, Boolean> {
	
	private Array<? extends CutsceneFunctionAction> executedActions;
	
	public IsUnitMovingFunction(Array<? extends CutsceneFunctionAction> executedActions) {
		this.executedActions = executedActions;
	}
	
	@Override
	public Boolean apply(String controlledUnitId) {
		for (int i = 0; i < executedActions.size; i++) {// don't use a for-each loop here, because the iterator won't work (because of the nested for loop)
			CutsceneFunctionAction action = executedActions.get(i);
			if (action.isMoveAction()) {
				if (controlledUnitId.equals(action.getControlledUnitId())) {
					return true;
				}
			}
		}
		return false;
	}
}
