package net.jfabricationgames.gdx.object;

import net.jfabricationgames.gdx.event.EventConfig;
import net.jfabricationgames.gdx.event.EventHandler;
import net.jfabricationgames.gdx.event.EventType;
import net.jfabricationgames.gdx.event.dto.FastTravelPointEventDto;
import net.jfabricationgames.gdx.object.interactive.InteractiveObject;

public enum GameObjectAction {
	
	REGISTER_FAST_TRAVEL_POINT {
		
		@Override
		public void execute(GameObject gameObject) {
			FastTravelPointEventDto eventDto = FastTravelPointEventDto.createFromGameObject((InteractiveObject) gameObject, gameObject.mapProperties);
			EventHandler.getInstance().fireEvent(new EventConfig().setEventType(EventType.FAST_TRAVEL_POINT_REGISTERED).setParameterObject(eventDto));
		}
	};
	
	public abstract void execute(GameObject object);
}
