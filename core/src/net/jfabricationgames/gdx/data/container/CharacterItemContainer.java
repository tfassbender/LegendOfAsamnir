package net.jfabricationgames.gdx.data.container;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectSet;

import net.jfabricationgames.gdx.data.properties.KeyItemProperties;

public class CharacterItemContainer {
	
	public int ammoArrow = 0;
	public final int maxAmmoArrow = 30;
	public int ammoBomb = 0;
	public final int maxAmmoBomb = 15;
	
	public int numNormalKeys = 0;
	
	public Array<KeyItemProperties> keys = new Array<>();
	public ObjectSet<String> specialItems = new ObjectSet<>();
}
