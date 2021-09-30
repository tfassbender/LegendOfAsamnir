package net.jfabricationgames.gdx.screens.menu.control;

public interface StatefullMenu {
	
	public void setFocusTo(String stateName, String leavingState);
	
	public void invokeMethod(String methodName);
}