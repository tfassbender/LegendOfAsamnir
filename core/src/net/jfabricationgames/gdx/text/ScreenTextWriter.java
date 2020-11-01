package net.jfabricationgames.gdx.text;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

import net.jfabricationgames.gdx.text.ScreenTextWriter.TextDrawCall.CallType;

public class ScreenTextWriter {
	
	private BitmapFont font;
	private ShaderProgram fontShader;
	private FontManager fontManager;
	
	private SpriteBatch batch;
	
	private Color color;
	
	private GlyphLayout glyphLayout;
	
	public ScreenTextWriter() {
		fontManager = FontManager.getInstance();
		fontShader = fontManager.getFontShader();
		
		batch = new SpriteBatch();
		batch.setShader(fontShader);
		
		color = Color.BLACK;
		glyphLayout = new GlyphLayout();
	}
	
	public void setFont(String fontName) {
		font = fontManager.getFont(fontName);
	}
	
	public void setScale(float scale) {
		font.getData().setScale(scale);
	}
	
	public void setColor(Color color) {
		this.color = color;
	}
	
	public GlyphLayout createGlyphLayout(CharSequence str, int textStartIndex, int textEndIndex, float targetWidth, int halign, boolean wrap) {
		glyphLayout.setText(font, str, textStartIndex, textEndIndex, color, targetWidth, halign, wrap, null);
		return glyphLayout;
	}
	public GlyphLayout createGlyphLayout(CharSequence str, float targetWidth, int halign, boolean wrap) {
		glyphLayout.setText(font, str, color, targetWidth, halign, wrap);
		return glyphLayout;
	}
	
	public GlyphLayout drawText(CharSequence str, float x, float y) {
		return drawText(CallType.DRAW_X_Y, str, x, y, -1, -1, -1, -1, false, null);
	}
	
	public GlyphLayout drawText(CharSequence str, float x, float y, float targetWidth, int halign, boolean wrap) {
		return drawText(CallType.DRAW_TARGET_WIDTH_ALIGN_WRAP, str, x, y, -1, -1, targetWidth, halign, wrap, null);
	}
	
	public GlyphLayout drawText(CharSequence str, float x, float y, int start, int end, float targetWidth, int halign, boolean wrap) {
		return drawText(CallType.DRAW_START_END, str, x, y, start, end, targetWidth, halign, wrap, null);
	}
	
	public GlyphLayout drawText(CharSequence str, float x, float y, int start, int end, float targetWidth, int halign, boolean wrap,
			String truncate) {
		return drawText(CallType.DRAW_START_END_TARGET_WIDTH_ALIGN_WRAP_TRUNCATE, str, x, y, start, end, targetWidth, halign, wrap, truncate);
	}
	
	private GlyphLayout drawText(CallType callType, CharSequence str, float x, float y, int start, int end, float targetWidth, int halign,
			boolean wrap, String truncate) {
		TextDrawCall drawCall = createDrawCall(callType, str, x, y, start, end, targetWidth, halign, wrap, truncate);
		return drawCall.invoke(batch, font);
	}
	
	private TextDrawCall createDrawCall(CallType callType, CharSequence str, float x, float y, int start, int end, float targetWidth, int halign,
			boolean wrap, String truncate) {
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
		drawCall.color = new Color(color);
		return drawCall;
	}
	
	public void drawText(GlyphLayout layout, float x, float y) {
		TextDrawCall drawCall = createTextDrawCall(layout, x, y);
		drawCall.invoke(batch, font);
	}
	
	private TextDrawCall createTextDrawCall(GlyphLayout layout, float x, float y) {
		TextDrawCall drawCall = new TextDrawCall();
		drawCall.callType = CallType.DRAW_LAYOUT;
		drawCall.glyphLayout = layout;
		drawCall.x = x;
		drawCall.y = y;
		drawCall.color = new Color(color);
		return drawCall;
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
		
		public GlyphLayout invoke(SpriteBatch batch, BitmapFont font) {
			batch.begin();
			font.setColor(color);
			font.getData().markupEnabled = true;
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
			
			batch.end();
			return glyphLayout;
		}
	}
	
	public SpriteBatch getBatch() {
		return batch;
	}
}
