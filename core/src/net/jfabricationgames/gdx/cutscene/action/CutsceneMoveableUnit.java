package net.jfabricationgames.gdx.cutscene.action;

import com.badlogic.gdx.math.Vector2;

public interface CutsceneMoveableUnit {
	
	public void moveTo(Vector2 position);
	public Vector2 getPosition();
	public void changeToMovingState();
}
