package net.jfabricationgames.gdx.cutscene.action;

import net.jfabricationgames.gdx.character.enemy.Enemy;
import net.jfabricationgames.gdx.character.state.CharacterState;
import net.jfabricationgames.gdx.cutscene.CutsceneControlledActionConfig;

public class CutsceneChangeStateAction extends AbstractCutsceneAction {
	
	public CutsceneChangeStateAction(CutsceneControlledActionConfig actionConfig) {
		super(actionConfig);
	}
	
	@Override
	public void execute(float delta) {
		Enemy enemy = getControlledUnitAs(Enemy.class);
		CharacterState state = enemy.getStateMachine().getState(actionConfig.controlledUnitState);
		state.setAttackDirection(actionConfig.controlledUnitAttackTargetDirection);
		enemy.getStateMachine().setState(state);
	}
	
	@Override
	public boolean isExecutionFinished() {
		return true;
	}
}
