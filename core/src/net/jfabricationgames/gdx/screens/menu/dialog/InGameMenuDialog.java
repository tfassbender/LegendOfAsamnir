package net.jfabricationgames.gdx.screens.menu.dialog;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;

import net.jfabricationgames.gdx.screens.menu.InGameMenuScreen;
import net.jfabricationgames.gdx.screens.menu.components.FocusButton;
import net.jfabricationgames.gdx.screens.menu.components.MenuBox;
import net.jfabricationgames.gdx.text.ScreenTextWriter;

public abstract class InGameMenuDialog implements Disposable {
	
	protected MenuBox background;
	protected MenuBox banner;
	protected FocusButton buttonBackToMenu;
	protected boolean visible;
	protected SpriteBatch batch;
	protected ScreenTextWriter screenTextWriter;
	
	public InGameMenuDialog() {
		batch = new SpriteBatch();
		
		screenTextWriter = new ScreenTextWriter();
		screenTextWriter.setFont(InGameMenuScreen.FONT_NAME);
	}
	
	public boolean isVisible() {
		return visible;
	}
	
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	
	protected String getButtonTextColorEncoding(FocusButton button) {
		return button.hasFocus() ? InGameMenuScreen.TEXT_COLOR_ENCODING_FOCUS : InGameMenuScreen.TEXT_COLOR_ENCODING_NORMAL;
	}
	
	@Override
	public void dispose() {
		batch.dispose();
	}
}