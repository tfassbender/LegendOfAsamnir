package net.jfabricationgames.gdx.event.global;

import com.badlogic.gdx.utils.ObjectMap;

import net.jfabricationgames.gdx.event.EventConfig;
import net.jfabricationgames.gdx.event.EventHandler;
import net.jfabricationgames.gdx.event.EventType;
import net.jfabricationgames.gdx.hud.OnScreenTextBox;

public enum GlobalEventExecutionType {
	
	SHOW_ON_SCREEN_TEXT {
		
		private static final String MAP_KEY_DISPLAY_TEXT = "displayText";
		private static final String MAP_KEY_DISPLAY_TEXT_HEADER = "displayTextHeader";
		
		@Override
		public void execute(ObjectMap<String, String> parameters) {
			String displayText = parameters.get(MAP_KEY_DISPLAY_TEXT);
			String displayTextHeader = parameters.get(MAP_KEY_DISPLAY_TEXT_HEADER);
			
			OnScreenTextBox onScreenTextBox = OnScreenTextBox.getInstance();
			onScreenTextBox.setHeaderText(displayTextHeader);
			onScreenTextBox.setText(displayText);
		}
	},
	START_CUTSCENE {
		
		private static final String MAP_KEY_CUTSCENE_ID = "cutsceneId";
		
		@Override
		public void execute(ObjectMap<String, String> parameters) {
			String cutsceneId = parameters.get(MAP_KEY_CUTSCENE_ID);
			EventHandler.getInstance().fireEvent(new EventConfig().setEventType(EventType.START_CUTSCENE).setStringValue(cutsceneId));
		}
	},
	CHANGE_MAP {

		private static final String MAP_KEY_TARGET_MAP = "map";
		
		@Override
		public void execute(ObjectMap<String, String> parameters) {
			String targetMap = parameters.get(MAP_KEY_TARGET_MAP);
			EventHandler.getInstance().fireEvent(new EventConfig().setEventType(EventType.CHANGE_MAP).setStringValue(targetMap));
		}
	};
	
	public abstract void execute(ObjectMap<String, String> parameters);
}
