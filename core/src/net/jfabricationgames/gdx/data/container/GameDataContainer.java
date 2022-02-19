package net.jfabricationgames.gdx.data.container;

import net.jfabricationgames.gdx.constants.Constants;

public class GameDataContainer {
	
	public String version = Constants.GAME_VERSION;
	
	public CharacterDataContainer characterDataContainer;
	public CharacterItemContainer itemDataContainer;
	public FastTravelContainer fastTravelDataContainer;
	public GlobalValuesContainer globalValuesDataContainer;
	public MapDataContainer mapDataContainer;
	public MapObjectDataContainer mapObjectDataContainer;
}
