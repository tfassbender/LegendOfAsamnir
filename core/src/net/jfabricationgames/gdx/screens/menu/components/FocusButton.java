package net.jfabricationgames.gdx.screens.menu.components;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

public class FocusButton {
	
	public static class FocusButtonBuilder {
		
		public String ninePatchConfig;
		public String ninePatchConfigFocused;
		public float width;
		public float height;
		public float x;
		public float y;
		
		public FocusButtonBuilder setNinePatchConfig(String ninePatchConfig) {
			this.ninePatchConfig = ninePatchConfig;
			return this;
		}
		
		public FocusButtonBuilder setNinePatchConfigFocused(String ninePatchConfigFocused) {
			this.ninePatchConfigFocused = ninePatchConfigFocused;
			return this;
		}
		
		public FocusButtonBuilder setSize(float width, float height) {
			this.width = width;
			this.height = height;
			return this;
		}
		
		public FocusButtonBuilder setPosition(float x, float y) {
			this.x = x;
			this.y = y;
			return this;
		}
		
		public FocusButton build() {
			return new FocusButton(ninePatchConfig, ninePatchConfigFocused, x, y, width, height);
		}
	}
	
	private Button button;
	
	private FocusButton(String ninePatchConfig, String ninePatchConfigFocused, float x, float y, float width, float height) {
		NinePatchDrawable buttonTexture = new NinePatchDrawable(NinePatchLoader.load(ninePatchConfig));
		NinePatchDrawable buttonTextureFocused = new NinePatchDrawable(NinePatchLoader.load(ninePatchConfigFocused));
		
		ButtonStyle style = new ButtonStyle();
		style.up = buttonTexture;
		style.checked = buttonTextureFocused;
		
		button = new Button(style);
		button.setSize(width, height);
		button.setPosition(x, y);
	}
	
	public boolean hasFocus() {
		return button.isChecked();
	}
	
	public void setFocused(boolean focused) {
		button.setChecked(focused);
	}
	
	public void draw(SpriteBatch batch) {
		button.draw(batch, 1f);
	}
	
	public void scaleBy(float scale) {
		button.setTransform(true);
		button.scaleBy(scale);
		button.setSize(button.getWidth() / scale, button.getHeight() / scale);
	}
}
