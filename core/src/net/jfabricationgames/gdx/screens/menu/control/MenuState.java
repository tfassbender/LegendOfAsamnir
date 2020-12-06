package net.jfabricationgames.gdx.screens.menu.control;

public class MenuState {
	
	public String up;
	public String down;
	public String left;
	public String right;
	public String select;
	public boolean initial;
	
	/**
	 * Used for states with multiple config files to configure additional ways to reach states (e.g. the mini-map states can be reached from the back
	 * button in the menu, which is to be defined in the mini-map config files)
	 * 
	 * Adds a direction state (up, down, left, right) to a already known state. If the state is unknown or the direction state would overwrite a state
	 * that is not null, an exception will be thrown when reading the config files.
	 */
	public AdditionalStateTransition[] reachableFrom;
}
