package net.jfabricationgames.gdx.character.player.container;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

import net.jfabricationgames.gdx.character.player.container.data.CharacterFastTravelProperties;
import net.jfabricationgames.gdx.event.EventConfig;
import net.jfabricationgames.gdx.event.EventHandler;
import net.jfabricationgames.gdx.event.EventListener;
import net.jfabricationgames.gdx.event.EventType;
import net.jfabricationgames.gdx.event.dto.FastTravelPointEventDto;

public class CharacterFastTravelContainer implements EventListener {
	
	private static CharacterFastTravelContainer instance;
	
	public static synchronized CharacterFastTravelContainer getInstance() {
		if (instance == null) {
			instance = new CharacterFastTravelContainer();
		}
		return instance;
	}
	
	private ObjectMap<String, CharacterFastTravelProperties> fastTravelProperties = new ObjectMap<>();
	
	private CharacterFastTravelContainer() {
		EventHandler.getInstance().registerEventListener(this);
	}
	
	public Array<CharacterFastTravelProperties> getFastTravelPositions() {
		Array<CharacterFastTravelProperties> fastTravelPositions = new Array<>(fastTravelProperties.size);
		for (CharacterFastTravelProperties property : fastTravelProperties.values()) {
			fastTravelPositions.add(property.clone());
		}
		return fastTravelPositions;
	}
	
	@Override
	public void handleEvent(EventConfig event) {
		if (event.eventType == EventType.FAST_TRAVEL_POINT_REGISTERED || event.eventType == EventType.FAST_TRAVEL_POINT_ENABLED) {
			if (event.parameterObject != null && event.parameterObject instanceof FastTravelPointEventDto) {
				Gdx.app.debug(getClass().getSimpleName(),
						"Fast travel point registered: event type: " + event.eventType + " object: " + event.parameterObject);
				
				FastTravelPointEventDto fastTravelDto = (FastTravelPointEventDto) event.parameterObject;
				if (fastTravelDto.fastTravelPointId == null) {
					throw new IllegalStateException("A FastTravelPointEventDto must have a fastTravelPointId (that is to be configured in the map).");
				}
				
				fastTravelProperties.put(fastTravelDto.fastTravelPointId, CharacterFastTravelProperties.fromDto(fastTravelDto));
			}
		}
	}
	
	public CharacterFastTravelProperties getFastTravelPropertiesById(String fastTravelPointId) {
		CharacterFastTravelProperties fastTravelProperty = fastTravelProperties.get(fastTravelPointId);
		return fastTravelProperty;
	}
}
