package net.jfabricationgames.gdx.item;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapProperties;

import net.jfabricationgames.gdx.sound.SoundSet;

public class ItemPickUpSound {
	
	private SoundSet soundSet;
	private String soundName;
	
	public ItemPickUpSound(String itemName, MapProperties properties, SoundSet soundSet) {
		this.soundSet = soundSet;
		soundName = selectSoundName(itemName, properties);
	}
	
	private String selectSoundName(String itemName, MapProperties properties) {
		switch (itemName) {
			case "health":
				int health = properties.get("health", Integer.class);
				if (health > 25) {
					return "health_big";
				}
				else {
					return "health_small";
				}
			case "axe":
			case "coin":
				//TODO add sound for this icons
				break;
			default:
				Gdx.app.error(getClass().getSimpleName(), "Item name is unknown: " + itemName);
		}
		return null;
	}
	
	public void play() {
		if (soundName != null) {
			soundSet.playSound(soundName);
		}
	}
}
