package net.jfabricationgames.gdx.condition;

import net.jfabricationgames.gdx.data.handler.CharacterItemDataHandler;
import net.jfabricationgames.gdx.data.handler.CharacterPropertiesDataHandler;
import net.jfabricationgames.gdx.object.interactive.StateSwitchObject;

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
		
		private static final String PARAMETERS_KEY_ITEM_ID = "itemId";
		
		@Override
		public boolean check(Condition condition) {
			String itemId = condition.parameters.get(PARAMETERS_KEY_ITEM_ID);
			return CharacterItemDataHandler.getInstance().containsSpecialItem(itemId);
		}
	},
	HAS_COINS {
		
		private static final String PARAMETERS_KEY_COINS = "atLeast";
		
		@Override
		public boolean check(Condition condition) {
			int coinsNeeded = Integer.parseInt(condition.parameters.get(PARAMETERS_KEY_COINS));
			return CharacterPropertiesDataHandler.getInstance().getCoins() >= coinsNeeded;
		}
	},
	STATE_SWITCH_ACTIVE {
		
		private static final String STATE_SWITCH_ID = "stateSwitchId";
		
		@Override
		public boolean check(Condition condition) {
			String stateSwitchId = condition.parameters.get(STATE_SWITCH_ID);
			return StateSwitchObject.isStateSwitchActive(stateSwitchId);
		}
	};
	
	private static final String CONDITIONAL_PARAMETERS_KEY_CONDITION_1 = "condition";
	private static final String CONDITIONAL_PARAMETERS_KEY_CONDITION_2 = "condition2";
	
	public abstract boolean check(Condition condition);
}
