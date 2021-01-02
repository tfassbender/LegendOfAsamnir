package net.jfabricationgames.gdx.condition.execution;

import net.jfabricationgames.gdx.event.global.GlobalEventConfig;

public enum ConditionExecutableType {
	
	CONDITION {
		
		@Override
		public void execute(ConditionExecutable conditionExecutable) {
			conditionExecutable.conditionalExecution.execute();
		}
	},
	EVENT {
		
		@Override
		public void execute(ConditionExecutable conditionExecutable) {
			GlobalEventConfig event = conditionExecutable.eventConfig;
			event.executionType.execute(event);
		}
	},
	NO_EXECUTION {
		
		@Override
		public void execute(ConditionExecutable conditionExecutable) {}
	};
	
	public abstract void execute(ConditionExecutable conditionExecutable);
}
