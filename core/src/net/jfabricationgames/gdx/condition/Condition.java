package net.jfabricationgames.gdx.condition;

import com.badlogic.gdx.utils.ObjectMap;

public class Condition {
	
	public ConditionType conditionType;
	public ObjectMap<String, String> parameters;
	public ObjectMap<String, Condition> conditionalParameters;
	
	public boolean check() {
		return conditionType.check(this);
	}
}
