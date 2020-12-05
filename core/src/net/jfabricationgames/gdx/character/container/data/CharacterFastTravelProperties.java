package net.jfabricationgames.gdx.character.container.data;

import com.badlogic.gdx.Gdx;

import net.jfabricationgames.gdx.event.dto.FastTravelPointEventDto;

public class CharacterFastTravelProperties implements Cloneable {
	
	public static CharacterFastTravelProperties fromDto(FastTravelPointEventDto fastTravelDto) {
		CharacterFastTravelProperties properties = new CharacterFastTravelProperties();
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
	public CharacterFastTravelProperties clone() {
		try {
			return (CharacterFastTravelProperties) super.clone();
		}
		catch (CloneNotSupportedException e) {
			Gdx.app.error(getClass().getSimpleName(), "CloneNotSupportedException in CharacterFastTravelProperties: ", e);
			return null;
		}
	}
}
