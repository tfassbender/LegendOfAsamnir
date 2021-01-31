package net.jfabricationgames.gdx.condition.choice;

import com.badlogic.gdx.utils.Array;

public class PlayerChoice {
	
	public static final int MAX_OPTIONS = 3;
	
	public String choiceId;
	
	public String header;
	public String description;
	public Array<String> options;
}
