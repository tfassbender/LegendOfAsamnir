package net.jfabricationgames.gdx.character.container.data;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectSet;

public class CharacterItemProperties {
	
	public int ammoArrow = 0;
	public final int maxAmmoArrow = 30;
	public int ammoBomb = 0;
	public final int maxAmmoBomb = 15;
	
	public Array<KeyItem> keys = new Array<>();
	public ObjectSet<String> specialItems = new ObjectSet<>();
}
