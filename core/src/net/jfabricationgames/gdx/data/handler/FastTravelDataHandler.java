package net.jfabricationgames.gdx.data.handler;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;

import net.jfabricationgames.gdx.data.container.FastTravelContainer;
import net.jfabricationgames.gdx.data.container.GameDataContainer;
import net.jfabricationgames.gdx.data.properties.FastTravelPointProperties;
import net.jfabricationgames.gdx.event.EventConfig;
import net.jfabricationgames.gdx.event.EventHandler;
import net.jfabricationgames.gdx.event.EventListener;
import net.jfabricationgames.gdx.event.EventType;
import net.jfabricationgames.gdx.event.dto.FastTravelPointEventDto;

public class FastTravelDataHandler implements DataHandler, EventListener {
	
	private static FastTravelDataHandler instance;
	
	public static synchronized FastTravelDataHandler getInstance() {
		if (instance == null) {
			instance = new FastTravelDataHandler();
		}
		return instance;
	}
	
	private FastTravelContainer properties;
	
	private FastTravelDataHandler() {
		EventHandler.getInstance().registerEventListener(this);
	}

	@Override
	public void updateData(GameDataContainer dataContainer) {
		properties = dataContainer.fastTravelDataContainer;
	}
	
	public Array<FastTravelPointProperties> getFastTravelPositions() {
		Array<FastTravelPointProperties> fastTravelPositions = new Array<>(properties.fastTravelProperties.size);
		for (FastTravelPointProperties property : properties.fastTravelProperties.values()) {
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
				
				properties.fastTravelProperties.put(fastTravelDto.fastTravelPointId, FastTravelPointProperties.fromDto(fastTravelDto));
			}
		}
	}
	
	public FastTravelPointProperties getFastTravelPropertiesById(String fastTravelPointId) {
		FastTravelPointProperties fastTravelProperty = properties.fastTravelProperties.get(fastTravelPointId);
		return fastTravelProperty;
	}
}
