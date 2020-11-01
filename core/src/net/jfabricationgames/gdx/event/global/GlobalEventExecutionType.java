package net.jfabricationgames.gdx.event.global;

import com.badlogic.gdx.utils.ObjectMap;

import net.jfabricationgames.gdx.hud.OnScreenTextBox;

public enum GlobalEventExecutionType {
	
	SHOW_ON_SCREEN_TEXT {
		
		private static final String MAP_KEY_DISPLAY_TEXT = "displayText";
		private static final String MAP_KEY_DISPLAY_TEXT_HEADER = "displayTextHeader";
		
		@Override
		public void execute(ObjectMap<String, String> parameters) {
			String displayText = (String) parameters.get(MAP_KEY_DISPLAY_TEXT);
			String displayTextHeader = (String) parameters.get(MAP_KEY_DISPLAY_TEXT_HEADER);
			
			OnScreenTextBox onScreenTextBox = OnScreenTextBox.getInstance();
			onScreenTextBox.setHeaderText(displayTextHeader);
			onScreenTextBox.setText(displayText);
		}
	};
	
	public abstract void execute(ObjectMap<String, String> parameters);
}
