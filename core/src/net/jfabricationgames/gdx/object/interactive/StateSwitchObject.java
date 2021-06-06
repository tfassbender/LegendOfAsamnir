package net.jfabricationgames.gdx.object.interactive;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.utils.ObjectMap;

import net.jfabricationgames.gdx.data.handler.MapObjectDataHandler;
import net.jfabricationgames.gdx.data.state.MapObjectState;
import net.jfabricationgames.gdx.event.EventConfig;
import net.jfabricationgames.gdx.event.EventHandler;
import net.jfabricationgames.gdx.event.EventType;
import net.jfabricationgames.gdx.object.GameObjectTypeConfig;

public class StateSwitchObject extends InteractiveObject {
	
	private static final String MAP_PROPERTIES_KEY_STATE_SWITCH_ID = "stateSwitchId";
	private static final String MAP_PROPERTIES_KEY_STATE_CHANGED_EVENT_PARAMETER = "eventParameter";
	
	private static ObjectMap<String, Boolean> switchObjectStates = new ObjectMap<>();
	
	public static boolean isStateSwitchActive(String stateSwitchId) {
		Boolean active = switchObjectStates.get(stateSwitchId);
		if (active == null) {
			Gdx.app.error(StateSwitchObject.class.getSimpleName(), "The required stateSwitchId '" + stateSwitchId + "' does not exist.");
			return false;
		}
		
		return active;
	}
	
	private static void changeSwitchObjectState(String stateSwitchId, boolean active) {
		switchObjectStates.put(stateSwitchId, active);
	}
	
	@MapObjectState
	private boolean active;
	private String stateSwitchId;
	
	public StateSwitchObject(GameObjectTypeConfig typeConfig, Sprite sprite, MapProperties properties) {
		super(typeConfig, sprite, properties);
		
		changeSwitchObjectState(stateSwitchId, false);
	}
	
	@Override
	protected void processMapProperties() {
		super.processMapProperties();
		
		stateSwitchId = mapProperties.get(MAP_PROPERTIES_KEY_STATE_SWITCH_ID, String.class);
		if (stateSwitchId == null) {
			Gdx.app.error(getClass().getSimpleName(),
					"A StateSwitchObject should have stateSwitchId (configured in the map properties). Map-ID: " + getMapObjectId());
		}
	}
	
	private void updateSprite() {
		if (active) {
			sprite = createSprite(typeConfig.textureAfterAction);
		}
		else {
			sprite = createSprite(typeConfig.texture);
		}
	}
	
	@Override
	protected void executeInteraction() {
		performAction();
		active = !active;
		
		changeSwitchObjectState(stateSwitchId, active);
		updateSprite();
		executeEvent();
		
		MapObjectDataHandler.getInstance().addStatefulMapObject(this);
	}
	
	private void executeEvent() {
		if (mapProperties.containsKey(MAP_PROPERTIES_KEY_STATE_CHANGED_EVENT_PARAMETER)) {
			String eventParameter = mapProperties.get(MAP_PROPERTIES_KEY_STATE_CHANGED_EVENT_PARAMETER, String.class);
			EventHandler.getInstance().fireEvent(new EventConfig().setEventType(EventType.STATE_SWITCH_ACTION).setStringValue(eventParameter));
		}
	}
}
