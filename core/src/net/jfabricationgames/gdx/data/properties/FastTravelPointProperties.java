package net.jfabricationgames.gdx.data.properties;

import com.badlogic.gdx.Gdx;

import net.jfabricationgames.gdx.event.dto.FastTravelPointEventDto;

public class FastTravelPointProperties implements Cloneable {
	
	public static FastTravelPointProperties fromDto(FastTravelPointEventDto fastTravelDto) {
		FastTravelPointProperties properties = new FastTravelPointProperties();
		properties.fastTravelPointId = fastTravelDto.fastTravelPointId;
		properties.fastTravelPointName = fastTravelDto.fastTravelPointName;
		properties.enabled = fastTravelDto.enabled;
		properties.positionOnMapX = fastTravelDto.positionOnMapX;
		properties.positionOnMapY = fastTravelDto.positionOnMapY;
		
		return properties;
	}
	
	public String fastTravelPointId;
	public String fastTravelPointName;
	public boolean enabled;
	public float positionOnMapX;
	public float positionOnMapY;
	
	@Override
	public FastTravelPointProperties clone() {
		try {
			return (FastTravelPointProperties) super.clone();
		}
		catch (CloneNotSupportedException e) {
			Gdx.app.error(getClass().getSimpleName(), "CloneNotSupportedException in CharacterFastTravelProperties: ", e);
			return null;
		}
	}
}
