package net.jfabricationgames.gdx.input;

public interface InputActionListener {
	
	public boolean onAction(String action);
	
	public boolean onButtonAction(String action, float screenX, float screenY, int pointer);
}
