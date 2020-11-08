package net.jfabricationgames.gdx.object.interactive;

import com.badlogic.gdx.maps.MapProperties;

import net.jfabricationgames.gdx.event.EventConfig;
import net.jfabricationgames.gdx.event.EventHandler;
import net.jfabricationgames.gdx.event.EventType;
import net.jfabricationgames.gdx.hud.OnScreenTextBox;

public enum InteractiveAction {
	
	SHOW_ON_SCREEN_TEXT {
		
		private static final String MAP_PROPERTY_KEY_DISPLAY_TEXT = "displayText";
		private static final String MAP_PROPERTY_KEY_DISPLAY_TEXT_HEADER = "displayTextHeader";
		
		@Override
		public void execute(InteractiveObject object) {
			MapProperties mapProperties = object.getMapProperties();
			OnScreenTextBox onScreenTextBox = OnScreenTextBox.getInstance();
			onScreenTextBox.setHeaderText(mapProperties.get(MAP_PROPERTY_KEY_DISPLAY_TEXT_HEADER, String.class));
			onScreenTextBox.setText(mapProperties.get(MAP_PROPERTY_KEY_DISPLAY_TEXT, String.class));
		}
	},
	OPEN_SHOP_MENU {
		
		@Override
		public void execute(InteractiveObject object) {
			//fire an event that is handled by the GameScreen
			EventHandler.getInstance().fireEvent(new EventConfig().setEventType(EventType.SHOW_IN_GAME_SHOP_MENU));
		}
	};
	
	public abstract void execute(InteractiveObject object);
}
