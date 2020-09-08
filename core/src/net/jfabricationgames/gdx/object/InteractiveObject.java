package net.jfabricationgames.gdx.object;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.MapProperties;

import net.jfabricationgames.gdx.hud.OnScreenTextBox;

public class InteractiveObject extends GameObject {
	
	private enum Properties {
		
		DISPLAY_TEXT("displayText"), // text that is displayed in the OnScreenTextBox
		DISPLAY_TEXT_HEADER("displayTextHeader"); // header that is displayed in the OnScreenTextBox (only if DISPLAY_TEXT is defined)
		
		public final String key;
		
		private Properties(String key) {
			this.key = key;
		}
	}
	
	public InteractiveObject(ObjectTypeConfig typeConfig, Sprite sprite, MapProperties properties) {
		super(typeConfig, sprite, properties);
	}
	
	@Override
	public void takeDamage(float damage) {
		super.takeDamage(damage);
		interact();
	}
	
	public void interact() {
		if (typeConfig.animationAction != null) {
			animation = getActionAnimation();			
		}
		performAction();
	}
	
	private void performAction() {
		if (properties.containsKey(Properties.DISPLAY_TEXT.key)) {
			OnScreenTextBox onScreenTextBox = OnScreenTextBox.getInstance();
			onScreenTextBox.setHeaderText(properties.get(Properties.DISPLAY_TEXT_HEADER.key, String.class));
			onScreenTextBox.setText(properties.get(Properties.DISPLAY_TEXT.key, String.class));
		}
	}
}
