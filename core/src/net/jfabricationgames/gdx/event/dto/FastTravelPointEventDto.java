package net.jfabricationgames.gdx.event.dto;

public class FastTravelPointEventDto {
	
	/**
	 * The fastTravelPointId must be equal in the map config of the object and in the map state config file (in config/menu/maps/...)
	 */
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
	
	@Override
	public String toString() {
		return "FastTravelPointEventDto [fastTravelPointId=" + fastTravelPointId + ", fastTravelPointName=" + fastTravelPointName + ", enabled="
				+ enabled + ", positionOnMapX=" + positionOnMapX + ", positionOnMapY=" + positionOnMapY + "]";
	}
}
