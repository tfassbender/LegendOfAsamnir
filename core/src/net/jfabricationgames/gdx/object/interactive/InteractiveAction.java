package net.jfabricationgames.gdx.object.interactive;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.maps.MapProperties;

import net.jfabricationgames.gdx.data.handler.GlobalValuesDataHandler;
import net.jfabricationgames.gdx.event.EventConfig;
import net.jfabricationgames.gdx.event.EventHandler;
import net.jfabricationgames.gdx.event.EventType;
import net.jfabricationgames.gdx.event.dto.FastTravelPointEventDto;
import net.jfabricationgames.gdx.object.GameObjectTextBox;
import net.jfabricationgames.gdx.util.GameUtil;

public enum InteractiveAction {
	
	SHOW_ON_SCREEN_TEXT {
		
		@Override
		public void execute(InteractiveObject object) {
			MapProperties mapProperties = object.getMapProperties();
			String headerText = mapProperties.get(MAP_PROPERTY_KEY_DISPLAY_TEXT_HEADER, String.class);
			String text = mapProperties.get(MAP_PROPERTY_KEY_DISPLAY_TEXT, String.class);
			String headerColor = mapProperties.get(MAP_PROPERTY_KEY_COLOR_HEADER, String.class);
			
			textBox.setHeaderText(headerText, GameUtil.getColorFromRGB(headerColor, Color.RED));
			textBox.setText(text);
		}
	},
	SHOW_OR_CHANGE_TEXT {
		
		private static final String MAP_PROPERTIES_KEY_DISPLAY_TEXT_CHANGED = "displayTextChanged";
		private static final String MAP_PROPERTIES_KEY_GLOBAL_CONDITION_VALUE = "globalConditionValue";
		private static final String MAP_PROPERTIES_KEY_GLOBAL_CONDITION_KEY = "globalConditionKey";
		private static final String MAP_PROPERTIES_KEY_EVENT_PARAMETER = "eventParameter";
		
		@Override
		public void execute(InteractiveObject object) {
			String globalConditionKey = object.getMapProperties().get(MAP_PROPERTIES_KEY_GLOBAL_CONDITION_KEY, String.class);
			String globalConditionValue = object.getMapProperties().get(MAP_PROPERTIES_KEY_GLOBAL_CONDITION_VALUE, String.class);
			String headerColor = object.getMapProperties().get(MAP_PROPERTY_KEY_COLOR_HEADER, String.class);
			String headerText = object.getMapProperties().get(MAP_PROPERTY_KEY_DISPLAY_TEXT_HEADER, String.class);
			String text = object.getMapProperties().get(MAP_PROPERTY_KEY_DISPLAY_TEXT, String.class);
			String changedText = object.getMapProperties().get(MAP_PROPERTIES_KEY_DISPLAY_TEXT_CHANGED, String.class);
			String eventParameter = object.getMapProperties().get(MAP_PROPERTIES_KEY_EVENT_PARAMETER, String.class);
			
			if (player.isSpecialActionFeatherSelected() && !isValueChanged(globalConditionKey, globalConditionValue)) {
				GlobalValuesDataHandler.getInstance().put(globalConditionKey, globalConditionValue);
				
				if (eventParameter != null) {
					EventHandler.getInstance()
							.fireEvent(new EventConfig().setEventType(EventType.CHANGE_SIGNBOARD_TEXT).setStringValue(eventParameter));
				}
				else {
					//only show the onscreen-text if no event is fired, because the event might lead to a cutscene
					showOnScreenText("The text was changed.", "Text changed", headerColor);
				}
			}
			else {
				if (isValueChanged(globalConditionKey, globalConditionValue)) {
					showOnScreenText(changedText, headerText, headerColor);
				}
				else {
					showOnScreenText(text, headerText, headerColor);
				}
			}
		}
		
		private void showOnScreenText(String text, String header, String headerColor) {
			textBox.setHeaderText(header, GameUtil.getColorFromRGB(headerColor, Color.RED));
			textBox.setText(text);
		}
		
		private boolean isValueChanged(String globalConditionKey, String globalConditionValue) {
			return GlobalValuesDataHandler.getInstance().isValueEqual(globalConditionKey, globalConditionValue);
		}
	},
	OPEN_SHOP_MENU {
		
		@Override
		public void execute(InteractiveObject object) {
			//fire an event that is handled by the GameScreen
			EventHandler.getInstance().fireEvent(new EventConfig().setEventType(EventType.SHOW_IN_GAME_SHOP_MENU));
		}
	},
	ENABLE_FAST_TRAVEL_POINT {
		
		@Override
		public void execute(InteractiveObject object) {
			FastTravelPointEventDto eventDto = object.createFastTravelPointEventDto();
			EventHandler.getInstance().fireEvent(new EventConfig().setEventType(EventType.FAST_TRAVEL_POINT_ENABLED).setParameterObject(eventDto));
		}
	},
	START_CUTSCENE {
		
		private static final String MAP_PROPERTY_KEY_CUTSCENE_ID = "cutsceneId";
		
		@Override
		public void execute(InteractiveObject object) {
			String cutsceneId = object.getMapProperties().get(MAP_PROPERTY_KEY_CUTSCENE_ID, String.class);
			EventHandler.getInstance().fireEvent(new EventConfig().setEventType(EventType.START_CUTSCENE).setStringValue(cutsceneId));
		}
	};
	
	private static final String MAP_PROPERTY_KEY_COLOR_HEADER = "colorHeader";
	private static final String MAP_PROPERTY_KEY_DISPLAY_TEXT_HEADER = "displayTextHeader";
	private static final String MAP_PROPERTY_KEY_DISPLAY_TEXT = "displayText";
	
	private static GameObjectTextBox textBox;
	private static InteractivePlayer player;
	
	public static void setTextBox(GameObjectTextBox textBox) {
		InteractiveAction.textBox = textBox;
	}
	
	public static void setPlayer(InteractivePlayer player) {
		InteractiveAction.player = player;
	}
	
	public abstract void execute(InteractiveObject object);
}
