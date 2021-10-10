package net.jfabricationgames.gdx.object;

import com.badlogic.gdx.physics.box2d.Body;

public interface GameObjectMap {
	
	public void addObject(GameObject gameObject);
	public void removeObject(GameObject gameObject, Body body);
}
