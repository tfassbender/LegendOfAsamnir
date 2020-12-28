package net.jfabricationgames.gdx.map;

public interface GameMapObject {
	
	/**
	 * Remove the game object from the map by removing the body from the Box2D world and setting the body to null to avoid reference errors (that
	 * would lead to segmentation faults in the native methods)
	 */
	public void removeFromMap();
}
