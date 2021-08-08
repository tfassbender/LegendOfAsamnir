package net.jfabricationgames.gdx.item;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapProperties;

import net.jfabricationgames.gdx.animation.AnimationDirector;
import net.jfabricationgames.gdx.event.EventConfig;
import net.jfabricationgames.gdx.event.EventHandler;
import net.jfabricationgames.gdx.event.EventType;

public class EventItem extends Item {
	
	public static final String EVENT_PARAMETER_MAP_PROPERTY_KEY = "eventParameter";
	
	private String eventParameter;
	
	public EventItem(String itemName, ItemTypeConfig typeConfig, Sprite sprite, AnimationDirector<TextureRegion> animation, MapProperties properties) {
		super(itemName, typeConfig, sprite, animation, properties);
		readEventParameters();
	}
	
	private void readEventParameters() {
		eventParameter = properties.get(EVENT_PARAMETER_MAP_PROPERTY_KEY, String.class);
	}
	
	@Override
	public void pickUp() {
		super.pickUp();
		EventHandler.getInstance()
				.fireEvent(new EventConfig().setEventType(EventType.EVENT_ITEM_PICKED_UP).setStringValue(eventParameter).setParameterObject(this));
	}
}
