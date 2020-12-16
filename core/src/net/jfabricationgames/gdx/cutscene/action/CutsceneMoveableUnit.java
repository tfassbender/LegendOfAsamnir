package net.jfabricationgames.gdx.cutscene.action;

import com.badlogic.gdx.math.Vector2;

public interface CutsceneMoveableUnit extends CutscenePositioningUnit {
	
	public void moveTo(Vector2 position, float speedFactor);
	public void changeToMovingState();
}
