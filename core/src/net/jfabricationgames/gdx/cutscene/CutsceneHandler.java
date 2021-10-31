package net.jfabricationgames.gdx.cutscene;

import java.util.Iterator;
import java.util.function.Supplier;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;

import net.jfabricationgames.gdx.cutscene.action.AbstractCutsceneAction;
import net.jfabricationgames.gdx.cutscene.action.CutsceneActionFactory;
import net.jfabricationgames.gdx.cutscene.action.CutsceneControlledActionConfig;
import net.jfabricationgames.gdx.cutscene.action.CutsceneMoveCameraAction;
import net.jfabricationgames.gdx.cutscene.action.CutsceneMoveableUnit;
import net.jfabricationgames.gdx.cutscene.action.CutsceneUnitProvider;
import net.jfabricationgames.gdx.cutscene.function.IsUnitMovingFunction;
import net.jfabricationgames.gdx.event.EventConfig;
import net.jfabricationgames.gdx.event.EventHandler;
import net.jfabricationgames.gdx.event.EventListener;
import net.jfabricationgames.gdx.event.EventType;

public class CutsceneHandler implements EventListener {
	
	public static final String CONFIG_PATH = "config/cutscene/cutscenes.json";
	
	private static CutsceneHandler instance;
	
	private ObjectMap<String, CutsceneConfig> cutscenes;
	private Array<AbstractCutsceneAction> executedActions;
	private String activeCutsceneId = null;
	
	private CutsceneUnitProvider unitProvider;
	private Supplier<Boolean> isTextDisplayed;
	
	public static final String CONTROLLED_UNIT_ID_PLAYER = "PLAYER";
	
	public static synchronized CutsceneHandler getInstance() {
		if (instance == null) {
			instance = new CutsceneHandler();
		}
		return instance;
	}
	
	private CutsceneHandler() {
		Gdx.app.log(getClass().getSimpleName(), "creating CutsceneHandler");
		loadCutscenes();
		executedActions = new Array<>();
		EventHandler.getInstance().registerEventListener(this);
	}
	
	@SuppressWarnings("unchecked")
	private void loadCutscenes() {
		Json json = new Json();
		Array<String> cutsceneFiles = json.fromJson(Array.class, String.class, Gdx.files.internal(CONFIG_PATH));
		cutscenes = new ObjectMap<>();
		
		for (String cutsceneConfigFile : cutsceneFiles) {
			Gdx.app.debug(getClass().getSimpleName(), "loading cutscene config file: " + cutsceneConfigFile);
			CutsceneConfig config = json.fromJson(CutsceneConfig.class, Gdx.files.internal(cutsceneConfigFile));
			cutscenes.put(config.id, config);
		}
	}
	
	public void setUnitProvider(CutsceneUnitProvider unitProvider) {
		this.unitProvider = unitProvider;
	}
	
	public void setTextDisplayedSupplier(Supplier<Boolean> isTextDisplayed) {
		this.isTextDisplayed = isTextDisplayed;
	}
	
	public boolean isCutsceneActive() {
		return activeCutsceneId != null || isTextDisplayed.get();
	}
	
	public boolean isCameraControlledByCutscene() {
		return isCutsceneActive() && isCameraMovementActionActive();
	}
	
	private boolean isCameraMovementActionActive() {
		for (AbstractCutsceneAction action : executedActions) {
			if (action instanceof CutsceneMoveCameraAction) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public void handleEvent(EventConfig event) {
		if (event.eventType == EventType.START_CUTSCENE) {
			String cutsceneId = event.stringValue;
			CutsceneConfig cutscene = cutscenes.get(cutsceneId);
			
			if (cutscene == null) {
				throw new IllegalStateException("The cutscene '" + cutsceneId
						+ "' is unknown. It has to be added into a file, that is referenced by the cutscene config file '" + CONFIG_PATH + "'");
			}
			playCutscene(cutscene);
		}
	}
	
	private void playCutscene(CutsceneConfig cutscene) {
		Gdx.app.debug(getClass().getSimpleName(), "playing cutscene: " + cutscene.id);
		if (isCutsceneActive()) {
			throw new IllegalStateException("A cutscene is already in action. Can't start a second cutscene.");
		}
		
		activeCutsceneId = cutscene.id;
		stopPlayerAction();
		stopCutsceneActorsActions(cutscene);
		
		Entry<String, CutsceneControlledActionConfig> initialAction = null;
		for (Entry<String, CutsceneControlledActionConfig> actionConfig : cutscene.controlledActions.entries()) {
			if (actionConfig.value.startsWithCutscene) {
				if (initialAction != null) {
					throw new IllegalStateException(
							"A CutsceneConfig has to configure exactly one action, that startsWithCutscene. This CutsceneConfig defines at least two: '"
									+ initialAction.key + "' and '" + actionConfig.key + "'.");
				}
				
				//create a new entry object because the for each iterator uses the same reference for all entry objects
				initialAction = new ObjectMap.Entry<>();
				initialAction.key = actionConfig.key;
				initialAction.value = actionConfig.value;
			}
		}
		
		addExecutedAction(initialAction.value, initialAction.key);
	}
	
	private void stopPlayerAction() {
		((CutsceneMoveableUnit) unitProvider.getUnitById(CONTROLLED_UNIT_ID_PLAYER)).changeToIdleState();
	}
	
	private void stopCutsceneActorsActions(CutsceneConfig cutscene) {
		for (CutsceneControlledActionConfig actionConfig : cutscene.controlledActions.values()) {
			if (actionConfig.controlledUnitId != null) {
				Object controlledUnit = unitProvider.getUnitById(actionConfig.controlledUnitId);
				if (controlledUnit instanceof CutsceneControlledCharacter) {
					((CutsceneControlledCharacter) controlledUnit).changeToIdleState();
				}
			}
		}
	}
	
	private void addExecutedAction(CutsceneControlledActionConfig actionConfig, String actionId) {
		Gdx.app.debug(getClass().getSimpleName(), "adding action config for cutscene '" + activeCutsceneId + "'; actionId: '" + actionId + "'");
		AbstractCutsceneAction action = CutsceneActionFactory.createAction(unitProvider, actionConfig, new IsUnitMovingFunction(executedActions));
		executedActions.add(action);
	}
	
	public void act(float delta) {
		if (isCutsceneActive() && executedActions.isEmpty()) {
			activeCutsceneId = null;
			return;
		}
		
		for (Iterator<AbstractCutsceneAction> iter = executedActions.iterator(); iter.hasNext();) {
			AbstractCutsceneAction action = iter.next();
			action.increaseExecutionTime(delta);
			
			if (action.isExecutionDelayPassed()) {
				action.execute(delta);
				
				if (action.isExecutionFinished()) {
					createFollowingActions(action);
					action.dispose();
					iter.remove();
				}
			}
		}
	}
	
	private void createFollowingActions(AbstractCutsceneAction action) {
		if (action.getFollowingActions() != null) {
			for (String actionId : action.getFollowingActions()) {
				CutsceneControlledActionConfig followingActionConfig = getActiveCutsceneConfig().controlledActions.get(actionId);
				if (followingActionConfig == null) {
					throw new IllegalStateException(
							"The following action '" + actionId + "' of the current state (defined in 'executes' parameter) is not known.");
				}
				
				addExecutedAction(followingActionConfig, actionId);
			}
		}
	}
	
	private CutsceneConfig getActiveCutsceneConfig() {
		return cutscenes.get(activeCutsceneId);
	}
}
