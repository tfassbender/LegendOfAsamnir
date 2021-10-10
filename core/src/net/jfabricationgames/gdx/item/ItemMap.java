package net.jfabricationgames.gdx.item;

import com.badlogic.gdx.physics.box2d.Body;

import net.jfabricationgames.gdx.rune.RuneType;

public interface ItemMap {
	
	public void addItem(Item item);
	public void addItemAboveGameObjects(Item item);
	public void removeItem(Item item, Body body);
	
	public void processRunePickUp(RuneType type);
}
