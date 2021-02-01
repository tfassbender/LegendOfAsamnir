package net.jfabricationgames.gdx.condition;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;

public class ConditionHandler {
	
	public static final String CONDITIONS_CONFIG_FILE_PATH = "config/condition/conditions.json";
	
	private static ConditionHandler instance;
	
	public static synchronized ConditionHandler getInstance() {
		if (instance == null) {
			instance = new ConditionHandler();
		}
		return instance;
	}
	
	private ObjectMap<String, Condition> conditions;
	
	private ConditionHandler() {
		loadConditions();
	}
	
	@SuppressWarnings("unchecked")
	private void loadConditions() {
		Json json = new Json();
		conditions = new ObjectMap<String, Condition>();
		
		Array<String> conditionFiles = json.fromJson(Array.class, String.class, Gdx.files.internal(CONDITIONS_CONFIG_FILE_PATH));
		for (String conditionFile : conditionFiles) {
			ObjectMap<String, Condition> conditionsPart = json.fromJson(ObjectMap.class, Condition.class, Gdx.files.internal(conditionFile));
			conditions.putAll(conditionsPart);
		}
	}
	
	public boolean checkCondition(String conditionId) {
		Condition condition = conditions.get(conditionId);
		if (condition == null) {
			throw new IllegalStateException("A condition with the id '" + conditionId
					+ "' was not found. Conditions need to be added to the config file '" + CONDITIONS_CONFIG_FILE_PATH + "'");
		}
		
		return condition.check();
	}
}
