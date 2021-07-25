package net.jfabricationgames.gdx.screens.menu.components;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

import net.jfabricationgames.gdx.data.handler.GlobalValuesDataHandler;
import net.jfabricationgames.gdx.item.RuneItem.RuneType;

public class RuneSubMenu extends ItemSubMenu {
	
	private static final String RUNE_NOT_FOUND_YET = "You have not revealed this rune yet";
	
	private RuneType hoveredRune;
	private boolean[] runeFound;
	private boolean runeHagalazForged;
	
	public RuneSubMenu(int numItemsPerLine, int numItemRows, Array<String> items) {
		super(numItemsPerLine, numItemRows, items);
		updateRuneStates();
	}
	
	public void updateRuneStates() {
		runeFound = initRunesFound();
		runeHagalazForged = GlobalValuesDataHandler.getInstance().isValueEqual(RuneType.GLOBAL_VALUE_KEY_RUNE_HAGALAZ_FORGED, "true");
	}
	
	private boolean[] initRunesFound() {
		boolean[] runesFound = new boolean[9];
		
		GlobalValuesDataHandler globalValues = GlobalValuesDataHandler.getInstance();
		for (RuneType rune : RuneType.values()) {
			runesFound[rune.order] = globalValues.isValueEqual(rune.globalValueKey, "true");
		}
		
		return runesFound;
	}
	
	@Override
	protected void drawItem(SpriteBatch batch, float posX, float posY, float scaledWidth, float scaledHeight, float borderFactor, float sizeFactor,
			TextureRegion itemTexture, int index) {
		if (runeFound[index]) {
			if (index == RuneType.HAGALAZ.order) {
				//the rune hagalaz needs to be forged after use
				if (runeHagalazForged) {
					super.drawItem(batch, posX, posY, scaledWidth, scaledHeight, borderFactor, sizeFactor, itemTexture, index);
				}
				else {
					batch.setColor(Color.LIGHT_GRAY);
					super.drawItem(batch, posX, posY, scaledWidth, scaledHeight, borderFactor, sizeFactor, itemTexture, index);
					batch.setColor(Color.WHITE);//reset to default color
				}
			}
			else {
				super.drawItem(batch, posX, posY, scaledWidth, scaledHeight, borderFactor, sizeFactor, itemTexture, index);
			}
		}
	}
	
	@Override
	public void setHoveredIndex(int hoveredIndex) {
		super.setHoveredIndex(hoveredIndex);
		
		if (hoveredIndex == -1) {
			hoveredRune = null;
		}
		else {
			hoveredRune = RuneType.getByOrder(hoveredIndex);
		}
	}
	
	public String getHoveredRuneDescription() {
		if (hoveredRune == null) {
			return "";
		}
		if (!runeFound[hoveredRune.order]) {
			return RUNE_NOT_FOUND_YET;
		}
		
		if (hoveredRune == RuneType.HAGALAZ) {
			if (runeHagalazForged) {
				return RuneType.HAGALAZ.description + RuneType.RUNE_HAGALAZ_DESCRIPTION_POSTFIX_FORGED;
			}
			else {
				return RuneType.HAGALAZ.description + RuneType.RUNE_HAGALAZ_DESCRIPTION_POSTFIX_UNFORGED;
			}
		}
		
		return hoveredRune.description;
	}
}
