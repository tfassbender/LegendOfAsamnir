package net.jfabricationgames.gdx.object;

import com.badlogic.gdx.graphics.Color;

public interface GameObjectTextBox {
	
	public void setHeaderText(String header, Color colorFromRGB);
	public void setHeaderText(String displayTextHeader);
	public void setText(String displayText);
}
