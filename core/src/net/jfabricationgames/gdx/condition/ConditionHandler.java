package net.jfabricationgames.gdx.condition;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;

import net.jfabricationgames.gdx.condition.execution.ConditionalExecution;
import net.jfabricationgames.gdx.event.global.GlobalEventConditionalExecutor;

public class ConditionHandler implements GlobalEventConditionalExecutor {
	
	private static final String CONDITIONS_CONFIG_FILE_PATH = "config/condition/condition/conditions.json";
	private static final String CONDITIONAL_EXECUTIONS_CONFIG_FILE_PATH = "config/condition/execution/conditionalExecutions.json";
	
	private static ConditionHandler instance;
	
	public static synchronized ConditionHandler getInstance() {
		if (instance == null) {
			instance = new ConditionHandler();
		}
		return instance;
	}
	
	private ObjectMap<String, Condition> conditions;
	private ObjectMap<String, ConditionalExecution> conditionalExecutions;
	
	private ConditionHandler() {
		loadConditions();
		loadConditionalExecutions();
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
	
	@SuppressWarnings("unchecked")
	private void loadConditionalExecutions() {
		Json json = new Json();
		conditionalExecutions = new ObjectMap<String, ConditionalExecution>();
		
		Array<String> conditionFiles = json.fromJson(Array.class, String.class, Gdx.files.internal(CONDITIONAL_EXECUTIONS_CONFIG_FILE_PATH));
		for (String conditionFile : conditionFiles) {
			ObjectMap<String, ConditionalExecution> conditionalExecution = json.fromJson(ObjectMap.class, ConditionalExecution.class,
					Gdx.files.internal(conditionFile));
			conditionalExecutions.putAll(conditionalExecution);
		}
	}
	
	public boolean isConditionMet(String conditionId) {
		Condition condition = conditions.get(conditionId);
		if (condition == null) {
			throw new IllegalStateException("A condition with the id '" + conditionId
					+ "' was not found. Conditions need to be added to the config file '" + CONDITIONS_CONFIG_FILE_PATH + "'");
		}
		
		return condition.check();
	}
	
	@Override
	public void executeConditional(String conditionalExecutionId) {
		ConditionalExecution conditionalExecution = conditionalExecutions.get(conditionalExecutionId);
		Gdx.app.debug(getClass().getSimpleName(), "Executing conditional: " + conditionalExecution);
		if (conditionalExecution == null) {
			throw new IllegalStateException("A conditional execution with the id '" + conditionalExecutionId
					+ "' was not found. Conditional executions need to be added to the config file '" + CONDITIONAL_EXECUTIONS_CONFIG_FILE_PATH
					+ "'");
		}
		
		conditionalExecution.execute();
	}
}
