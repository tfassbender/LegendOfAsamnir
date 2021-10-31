package net.jfabricationgames.gdx.event.global;

import com.badlogic.gdx.graphics.Color;

import net.jfabricationgames.gdx.event.PlayerChoice;

public interface GlobalEventTextBox {
	
	public void setHeaderText(String displayTextHeader, Color colorFromRGB);
	public void setText(String displayText, boolean showNextPageIcon);
	public void showPlayerChoice(PlayerChoice parameterObject);
	public void setShowOnBlackScreen(boolean showOnBlackScreen);
}
