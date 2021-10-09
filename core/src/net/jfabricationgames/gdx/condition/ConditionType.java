package net.jfabricationgames.gdx.condition;

import java.util.function.Function;

import net.jfabricationgames.gdx.data.handler.CharacterItemDataHandler;
import net.jfabricationgames.gdx.data.handler.CharacterPropertiesDataHandler;
import net.jfabricationgames.gdx.data.handler.GlobalValuesDataHandler;
import net.jfabricationgames.gdx.rune.RuneType;

public enum ConditionType {
	
	AND {
		
		@Override
		public boolean check(Condition condition) {
			Condition condition1 = condition.conditionalParameters.get(CONDITIONAL_PARAMETERS_KEY_CONDITION_1);
			Condition condition2 = condition.conditionalParameters.get(CONDITIONAL_PARAMETERS_KEY_CONDITION_2);
			return condition1.check() && condition2.check();
		}
	},
	OR {
		
		@Override
		public boolean check(Condition condition) {
			Condition condition1 = condition.conditionalParameters.get(CONDITIONAL_PARAMETERS_KEY_CONDITION_1);
			Condition condition2 = condition.conditionalParameters.get(CONDITIONAL_PARAMETERS_KEY_CONDITION_2);
			return condition1.check() || condition2.check();
		}
	},
	NOT {
		
		@Override
		public boolean check(Condition condition) {
			Condition negated = condition.conditionalParameters.get(CONDITIONAL_PARAMETERS_KEY_CONDITION_1);
			return !negated.check();
		}
	},
	HAS_ITEM {
		
		private static final String PARAMETERS_ITEM_ID = "itemId";
		
		@Override
		public boolean check(Condition condition) {
			String itemId = condition.parameters.get(PARAMETERS_ITEM_ID);
			return CharacterItemDataHandler.getInstance().containsSpecialItem(itemId);
		}
	},
	HAS_COINS {
		
		private static final String PARAMETERS_COINS = "atLeast";
		
		@Override
		public boolean check(Condition condition) {
			int coinsNeeded = Integer.parseInt(condition.parameters.get(PARAMETERS_COINS));
			return CharacterPropertiesDataHandler.getInstance().getCoins() >= coinsNeeded;
		}
	},
	STATE_SWITCH_ACTIVE {
		
		private static final String PARAMETER_STATE_SWITCH_ID = "stateSwitchId";
		
		@Override
		public boolean check(Condition condition) {
			String stateSwitchId = condition.parameters.get(PARAMETER_STATE_SWITCH_ID);
			return isStateSwitchActive.apply(stateSwitchId);
		}
	},
	GLOBAL_VALUE_SET {
		
		private static final String PARAMETER_VALUE_KEY = "key";
		private static final String PARAMETER_EXPECTED_VALUE = "expectedValue";
		
		@Override
		public boolean check(Condition condition) {
			String key = condition.parameters.get(PARAMETER_VALUE_KEY);
			String expectedValue = condition.parameters.get(PARAMETER_EXPECTED_VALUE);
			
			return GlobalValuesDataHandler.getInstance().isValueEqual(key, expectedValue);
		}
	},
	RUNE_COLLECTED {
		
		private static final String PARAMETER_RUNE_NAME = "rune";
		
		@Override
		public boolean check(Condition condition) {
			String runeName = condition.parameters.get(PARAMETER_RUNE_NAME);
			RuneType rune = RuneType.getByContainingName(runeName);
			return rune.isCollected();
		}
	};
	
	private static final String CONDITIONAL_PARAMETERS_KEY_CONDITION_1 = "condition";
	private static final String CONDITIONAL_PARAMETERS_KEY_CONDITION_2 = "condition2";
	
	private static Function<String, Boolean> isStateSwitchActive;
	
	public static void setIsStateSwitchActive(Function<String, Boolean> isStateSwitchActive) {
		ConditionType.isStateSwitchActive = isStateSwitchActive;
	}
	
	public abstract boolean check(Condition condition);
}
