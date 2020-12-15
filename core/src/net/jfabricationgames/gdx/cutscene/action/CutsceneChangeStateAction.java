package net.jfabricationgames.gdx.cutscene.action;

import net.jfabricationgames.gdx.cutscene.CutsceneControlledActionConfig;
import net.jfabricationgames.gdx.enemy.Enemy;
import net.jfabricationgames.gdx.enemy.state.EnemyState;

public class CutsceneChangeStateAction extends AbstractCutsceneAction {
	
	public CutsceneChangeStateAction(CutsceneControlledActionConfig actionConfig) {
		super(actionConfig);
	}
	
	@Override
	public void execute() {
		Enemy enemy = getControlledUnitAs(Enemy.class);
		EnemyState state = enemy.getStateMachine().getState(actionConfig.controlledUnitState);
		state.setAttackDirection(actionConfig.controlledUnitAttackTargetDirection);
		enemy.getStateMachine().setState(state);
	}
	
	@Override
	public boolean isExecutionFinished() {
		return true;
	}
}
