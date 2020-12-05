package net.jfabricationgames.gdx.event.dto;

import com.badlogic.gdx.maps.MapProperties;

import net.jfabricationgames.gdx.object.interactive.InteractiveObject;

public class FastTravelPointEventDto {
	
	private static final String MAP_PROPERTY_KEY_FAST_TRAVEL_POINT_ID = "fastTravelPointId";
	private static final String MAP_PROPERTY_KEY_FAST_TRAVEL_POINT_NAME = "fastTravelPointName";
	
	public static FastTravelPointEventDto createFromGameObject(InteractiveObject object, MapProperties mapProperties) {
		String fastTravelPointId = mapProperties.get(MAP_PROPERTY_KEY_FAST_TRAVEL_POINT_ID, String.class);
		String fastTravelPointName = mapProperties.get(MAP_PROPERTY_KEY_FAST_TRAVEL_POINT_NAME, String.class);
		boolean enabled = object.isActivateOnStartup() || object.isActionExecuted();
		float posX = object.getPosition().x;
		float posY = object.getPosition().y;
		
		return new FastTravelPointEventDto() //
				.setFastTravelPointId(fastTravelPointId) //
				.setFastTravelPointName(fastTravelPointName) //
				.setEnabled(enabled) //
				.setPositionOnMapX(posX) //
				.setPositionOnMapY(posY);
	}
	
	public String fastTravelPointId;
	public String fastTravelPointName;
	public boolean enabled;
	public float positionOnMapX;
	public float positionOnMapY;
	
	public FastTravelPointEventDto setFastTravelPointId(String fastTravelPointId) {
		this.fastTravelPointId = fastTravelPointId;
		return this;
	}
	
	public FastTravelPointEventDto setFastTravelPointName(String fastTravelPointName) {
		this.fastTravelPointName = fastTravelPointName;
		return this;
	}
	
	public FastTravelPointEventDto setEnabled(boolean enabled) {
		this.enabled = enabled;
		return this;
	}
	
	public FastTravelPointEventDto setPositionOnMapX(float positionOnMapX) {
		this.positionOnMapX = positionOnMapX;
		return this;
	}
	
	public FastTravelPointEventDto setPositionOnMapY(float positionOnMapY) {
		this.positionOnMapY = positionOnMapY;
		return this;
	}
}
