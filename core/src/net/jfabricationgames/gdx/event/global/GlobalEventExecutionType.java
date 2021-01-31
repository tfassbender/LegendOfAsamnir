package net.jfabricationgames.gdx.event.global;

import com.badlogic.gdx.graphics.Color;

import net.jfabricationgames.gdx.event.EventConfig;
import net.jfabricationgames.gdx.event.EventHandler;
import net.jfabricationgames.gdx.event.EventType;
import net.jfabricationgames.gdx.hud.OnScreenTextBox;
import net.jfabricationgames.gdx.util.GameUtils;

public enum GlobalEventExecutionType {
	
	SHOW_ON_SCREEN_TEXT {
		
		private static final String MAP_KEY_DISPLAY_TEXT = "displayText";
		private static final String MAP_KEY_DISPLAY_TEXT_HEADER = "displayTextHeader";
		private static final String MAP_KEY_COLOR_HEADER = "colorHeader";
		private static final String MAP_KEY_SHOW_NEXT_PAGE_ICON = "showNextPageIcon";
		
		@Override
		public void execute(GlobalEventConfig eventConfig) {
			String displayText = eventConfig.executionParameters.get(MAP_KEY_DISPLAY_TEXT);
			String displayTextHeader = eventConfig.executionParameters.get(MAP_KEY_DISPLAY_TEXT_HEADER);
			String colorHeader = eventConfig.executionParameters.get(MAP_KEY_COLOR_HEADER);
			boolean showNextPageIcon = Boolean.parseBoolean(eventConfig.executionParameters.get(MAP_KEY_SHOW_NEXT_PAGE_ICON));
			
			OnScreenTextBox onScreenTextBox = OnScreenTextBox.getInstance();
			onScreenTextBox.setHeaderText(displayTextHeader, GameUtils.getColorFromRGB(colorHeader, Color.RED));
			onScreenTextBox.setText(displayText, showNextPageIcon);
		}
	},
	START_CUTSCENE {
		
		private static final String MAP_KEY_CUTSCENE_ID = "cutsceneId";
		
		@Override
		public void execute(GlobalEventConfig eventConfig) {
			String cutsceneId = eventConfig.executionParameters.get(MAP_KEY_CUTSCENE_ID);
			EventHandler.getInstance().fireEvent(new EventConfig().setEventType(EventType.START_CUTSCENE).setStringValue(cutsceneId));
		}
	},
	CHANGE_MAP {
		
		private static final String MAP_KEY_TARGET_MAP = "map";
		
		@Override
		public void execute(GlobalEventConfig eventConfig) {
			String targetMap = eventConfig.executionParameters.get(MAP_KEY_TARGET_MAP);
			EventHandler.getInstance().fireEvent(new EventConfig().setEventType(EventType.CHANGE_MAP).setStringValue(targetMap));
		}
	},
	CONDITIONAL_EVENT {
		
		@Override
		public void execute(GlobalEventConfig eventConfig) {
			eventConfig.condition.execute();
		}
	},
	SET_ITEM {

		private static final String MAP_KEY_ITEM_NAME = "item";
		
		@Override
		public void execute(GlobalEventConfig eventConfig) {
			String item = eventConfig.executionParameters.get(MAP_KEY_ITEM_NAME);
			EventHandler.getInstance().fireEvent(new EventConfig().setEventType(EventType.SET_ITEM).setStringValue(item));
		}
	};
	
	public abstract void execute(GlobalEventConfig eventConfig);
}
