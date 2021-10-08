package net.jfabricationgames.gdx.cutscene.action;

public interface CutsceneControlledStatefullUnit extends CutsceneControlledUnit {
	
	public CutsceneControlledState getState(String controlledUnitState);
	public void setState(CutsceneControlledState state);
}
