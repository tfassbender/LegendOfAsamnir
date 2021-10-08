package net.jfabricationgames.gdx.cutscene;

import com.badlogic.gdx.utils.ObjectMap;

import net.jfabricationgames.gdx.cutscene.action.CutsceneControlledActionConfig;

public class CutsceneConfig {
	
	public String id;
	public ObjectMap<String, CutsceneControlledActionConfig> controlledActions;
}
