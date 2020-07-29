package net.jfabricationgames.gdx.text;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Array;

import net.jfabricationgames.gdx.text.ScreenTextWriter.TextDrawCall.CallType;

public class ScreenTextWriter {
	
	private BitmapFont font;
	private ShaderProgram fontShader;
	private FontManager fontManager;
	
	private float scale;
	private Color color;
	
	private Array<TextDrawCall> bufferedTexts;
	
	public ScreenTextWriter() {
		fontManager = FontManager.getInstance();
		fontShader = fontManager.getFontShader();
		bufferedTexts = new Array<>();
	}
	
	public void setFont(String fontName) {
		font = fontManager.getFont(fontName);
	}
	
	public Array<TextDrawCall> draw(SpriteBatch batch) {
		ShaderProgram tmpShader = batch.getShader();
		batch.setShader(fontShader);
		
		Array<TextDrawCall> drawCalls = new Array<>();
		for (TextDrawCall drawCall : bufferedTexts) {
			drawCall.invoke(batch, font);
			drawCalls.add(drawCall);
		}
		
		batch.setShader(tmpShader);
		clearBuffer();
		
		return drawCalls;
	}
	
	public void setScale(float scale) {
		this.scale = scale;
	}
	
	public void setColor(Color color) {
		this.color = color;
	}
	
	public void addText(CharSequence str, float x, float y) {
		addText(CallType.DRAW_X_Y, str, x, y, -1, -1, -1, -1, false, null);
	}
	
	public void addText(CharSequence str, float x, float y, float targetWidth, int halign, boolean wrap) {
		addText(CallType.DRAW_TARGET_WIDTH_ALIGN_WRAP, str, x, y, -1, -1, targetWidth, halign, wrap, null);
	}
	
	public void addText(CharSequence str, float x, float y, int start, int end, float targetWidth, int halign, boolean wrap) {
		addText(CallType.DRAW_START_END, str, x, y, start, end, targetWidth, halign, wrap, null);
	}
	
	public void addText(CharSequence str, float x, float y, int start, int end, float targetWidth, int halign, boolean wrap, String truncate) {
		addText(CallType.DRAW_START_END_TARGET_WIDTH_ALIGN_WRAP_TRUNCATE, str, x, y, start, end, targetWidth, halign, wrap, truncate);
	}
	
	private void addText(CallType callType, CharSequence str, float x, float y, int start, int end, float targetWidth, int halign, boolean wrap, String truncate) {
		TextDrawCall drawCall = new TextDrawCall();
		drawCall.callType = callType;
		drawCall.str = str;
		drawCall.x = x;
		drawCall.y = y;
		drawCall.start = start;
		drawCall.end = end;
		drawCall.targetWidth = targetWidth;
		drawCall.halign = halign;
		drawCall.wrap = wrap;
		drawCall.truncate = truncate;
		drawCall.scale = scale;
		drawCall.color = new Color(color);
		bufferedTexts.add(drawCall);
	}
	
	public void addText(GlyphLayout layout, float x, float y) {
		TextDrawCall drawCall = new TextDrawCall();
		drawCall.callType = CallType.DRAW_LAYOUT;
		drawCall.glyphLayout = layout;
		drawCall.x = x;
		drawCall.y = y;
		drawCall.scale = scale;
		drawCall.color = new Color(color);
		bufferedTexts.add(drawCall);
	}
	
	public void clearBuffer() {
		bufferedTexts.clear();
	}
	
	public void setFontColor(Color color) {
		font.setColor(color);
	}
	
	public static class TextDrawCall {
		
		public enum CallType {
			DRAW_X_Y, DRAW_TARGET_WIDTH_ALIGN_WRAP, DRAW_START_END, DRAW_START_END_TARGET_WIDTH_ALIGN_WRAP_TRUNCATE, DRAW_LAYOUT;
		}
		
		public CallType callType;
		public CharSequence str;
		public float x;
		public float y;
		public int start;
		public int end;
		public float targetWidth;
		public int halign;
		public boolean wrap;
		public String truncate;
		
		public GlyphLayout glyphLayout;
		
		public float scale;
		public Color color;
		
		public void invoke(SpriteBatch batch, BitmapFont font) {
			font.setColor(color);
			font.getData().setScale(scale);
			switch (callType) {
				case DRAW_LAYOUT:
					font.draw(batch, glyphLayout, x, y);
					break;
				case DRAW_START_END:
					glyphLayout = font.draw(batch, str, x, y, start, end, targetWidth, halign, wrap);
					break;
				case DRAW_START_END_TARGET_WIDTH_ALIGN_WRAP_TRUNCATE:
					glyphLayout = font.draw(batch, str, x, y, start, end, targetWidth, halign, wrap, truncate);
					break;
				case DRAW_TARGET_WIDTH_ALIGN_WRAP:
					glyphLayout = font.draw(batch, str, x, y, targetWidth, halign, wrap);
					break;
				case DRAW_X_Y:
					glyphLayout = font.draw(batch, str, x, y);
					break;
				default:
					throw new IllegalStateException("The callType is unknown: " + callType);
			}
		}
	}
}
