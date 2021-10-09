package net.jfabricationgames.gdx.event;

import com.badlogic.gdx.utils.Array;

public class PlayerChoice {
	
	public static final int MAX_OPTIONS = 3;
	
	public String header = "";
	public String headerColor = "#FF0000";
	public String description = "";
	public Array<String> options;
}
