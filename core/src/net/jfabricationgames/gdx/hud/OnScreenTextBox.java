package net.jfabricationgames.gdx.hud;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;

import net.jfabricationgames.gdx.DwarfScrollerGame;
import net.jfabricationgames.gdx.input.InputActionListener;
import net.jfabricationgames.gdx.input.InputContext;
import net.jfabricationgames.gdx.text.ScreenTextWriter;

public class OnScreenTextBox implements Disposable, InputActionListener {
	
	public static final String DEFAULT_FONT_NAME = "vikingMedium";
	
	private static final Color[] textBoxColors = new Color[] { //
			new Color(0.95f, 0.95f, 0.95f, 1f), // bottom-left
			new Color(0.9f, 0.9f, 0.9f, 1f), // bottom-right
			new Color(0.99f, 0.99f, 0.99f, 1f), // top-right
			new Color(0.9f, 0.9f, 0.9f, 1f)// top-left
	};
	
	private static final float textScale = 1f;
	private static final int displayableLines = 5;
	
	private static OnScreenTextBox instance;
	
	public static synchronized OnScreenTextBox getInstance() {
		if (instance == null) {
			throw new IllegalStateException("The instance of OnScreenTextBox has not yet been created. "
					+ "Use the createInstance(HeadsUpDisplay) method to create an instance.");
		}
		return instance;
	}
	
	protected static synchronized OnScreenTextBox createInstance(HeadsUpDisplay hud) {
		if (instance != null) {
			throw new IllegalStateException("An instance of OnScreenTextBox has already been created.");
		}
		instance = new OnScreenTextBox(hud);
		return instance;
	}
	
	private final float textBoxX;
	private final float textBoxY;
	private final float textBoxWidth;
	private final float textBoxHeight;
	
	private final float textBoxEdge;
	private final float textOffsetX;
	private final float textOffsetY;
	private final float textWidth;
	private final float headerOffsetY;
	
	private OrthographicCamera camera;
	private SpriteBatch batch;
	private ShapeRenderer shapeRenderer;
	
	private ScreenTextWriter screenTextWriter;
	
	private String text;
	private GlyphLayout textLayout;
	private String headerText;
	
	private int firstDisplayedLine = 0;
	private int[] displayedTextIndices;
	
	private int displayedCharacters = 0;
	private float timeSincePageStarted = 0;
	private float displayCharactersPerSecond = 25f;
	
	private float nextPageIndicatorTimer = 0;
	private final float nextPageIndicatorBlinkingTime = 0.75f;
	private boolean displayNextPageIndicator = false;
	
	private OnScreenTextBox(HeadsUpDisplay hud) {
		this.camera = hud.getCamera();
		batch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();
		
		textBoxX = hud.getHudSceneWidth() * 0.025f;
		textBoxY = hud.getHudSceneHeight() * 0.025f;
		textBoxWidth = hud.getHudSceneWidth() * 0.95f;
		textBoxHeight = hud.getHudSceneHeight() * 0.37f;
		textBoxEdge = textBoxHeight * 0.015f;
		textOffsetX = textBoxEdge * 6f;
		textOffsetY = 90f;
		textWidth = textBoxWidth - textOffsetX * 5f;
		headerOffsetY = textOffsetX * 1.2f;
		
		screenTextWriter = new ScreenTextWriter();
		screenTextWriter.setFont(DEFAULT_FONT_NAME);
		
		InputContext inputContext = DwarfScrollerGame.getInstance().getInputContext();
		inputContext.addListener(this);
	}
	
	public void render(float delta) {
		if (text != null) {
			batch.setProjectionMatrix(camera.combined);
			shapeRenderer.setProjectionMatrix(camera.combined);
			
			renderBackground();
			renderText();
			
			nextPageIndicatorTimer += delta;
			if (nextPageIndicatorTimer > nextPageIndicatorBlinkingTime) {
				nextPageIndicatorTimer -= nextPageIndicatorBlinkingTime;
				displayNextPageIndicator = !displayNextPageIndicator;
			}
			
			if (!allCharactersDisplayed()) {
				timeSincePageStarted += delta;
				displayedCharacters = Math.min((int) (displayCharactersPerSecond * timeSincePageStarted),
						displayedTextIndices[1] - displayedTextIndices[0]);
			}
		}
	}
	
	private void renderBackground() {
		shapeRenderer.begin(ShapeType.Filled);
		
		shapeRenderer.setColor(Color.BLACK);
		shapeRenderer.rect(textBoxX, textBoxY, textBoxWidth, textBoxHeight);
		shapeRenderer.rect(textBoxX + textBoxEdge, textBoxY + textBoxEdge, textBoxWidth - 2f * textBoxEdge, textBoxHeight - 2f * textBoxEdge,
				textBoxColors[0], textBoxColors[1], textBoxColors[2], textBoxColors[3]);
		
		shapeRenderer.end();
	}
	
	private void renderText() {
		batch.begin();
		
		if (headerText != null) {
			screenTextWriter.setScale(1.25f * textScale);
			screenTextWriter.setColor(Color.RED);
			screenTextWriter.drawText(headerText, textBoxX + textOffsetX, textBoxY + textBoxHeight - headerOffsetY);
		}
		
		screenTextWriter.setScale(textScale);
		screenTextWriter.setColor(Color.BLACK);
		screenTextWriter.drawText(text, textBoxX + textOffsetX, textBoxY + textBoxHeight - textOffsetY, displayedTextIndices[0],
				displayedTextIndices[0] + displayedCharacters, textWidth, Align.left, true);
		
		if (hasNextPage() && displayNextPageIndicator) {
			drawNextPageIndicator();
		}
		
		batch.end();
	}
	
	private void drawNextPageIndicator() {
		shapeRenderer.begin(ShapeType.Filled);
		
		shapeRenderer.setColor(Color.BLACK);
		shapeRenderer.triangle(1200f, 55f, 1230f, 55f, 1215f, 35f);
		
		shapeRenderer.end();
	}
	
	public String getText() {
		return text;
	}
	
	public void setText(String text) {
		this.text = text;
		calculateTextLayout();
		calculateDisplayedText();
	}
	
	private void calculateTextLayout() {
		screenTextWriter.setScale(textScale);
		textLayout = screenTextWriter.createGlyphLayout(text, textWidth, Align.left, true);
	}
	
	private void calculateDisplayedText() {
		firstDisplayedLine = 0;
		displayedTextIndices = getDisplayedTextIndices();
		resetDisplayedCharacters();
	}

	private void resetDisplayedCharacters() {
		displayedCharacters = 0;
		timeSincePageStarted = 0;
	}
	
	public void nextPage() {
		if (hasNextPage()) {
			firstDisplayedLine += displayableLines;
			displayedTextIndices = getDisplayedTextIndices();
			timeSincePageStarted = 0;
			displayedCharacters = 0;
		}
	}
	
	private boolean hasNextPage() {
		return textLayout.runs.size > firstDisplayedLine + displayableLines;
	}
	
	private int[] getDisplayedTextIndices() {
		int[] indices = new int[2];
		
		//find the start index
		for (int i = 0; i < firstDisplayedLine; i++) {
			//increase the start index by the number of characters in the previous (not displayed) lines
			indices[0] += textLayout.runs.get(i).glyphs.size + 1;//+1 for line breaks (I think...)
		}
		
		//add the start index to the end index, to only count the remaining displayed line's characters
		indices[1] = indices[0];
		
		//find the end index
		for (int i = 0; i < displayableLines && i + firstDisplayedLine < textLayout.runs.size; i++) {
			//increase the end index by the number of characters in the displayed lines
			indices[1] += textLayout.runs.get(i + firstDisplayedLine).glyphs.size + 1;//+1 for line breaks (I think...)
		}
		indices[1] -= 1;//remove the last line break index
		
		return indices;
	}
	
	public String getHeaderText() {
		return headerText;
	}
	
	public void setHeaderText(String headerText) {
		this.headerText = headerText;
	}
	
	@Override
	public Priority getInputPriority() {
		return InputActionListener.Priority.ON_SCREEN_TEXT;
	}
	
	@Override
	public boolean onAction(String action, Type type, Parameters parameters) {
		if (action.equals("interact") && (type == Type.KEY_DOWN || type == Type.CONTROLLER_BUTTON_PRESSED)) {
			if (showsText()) {
				if (!allCharactersDisplayed()) {
					showAllCharacters();
				}
				else if (hasNextPage()) {
					nextPage();
				}
				else {
					close();
				}
				return true;
			}
		}
		return false;
	}
	
	private boolean showsText() {
		return text != null;
	}
	
	private boolean allCharactersDisplayed() {
		return displayedCharacters == displayedTextIndices[1] - displayedTextIndices[0];
	}
	
	private void showAllCharacters() {
		displayedCharacters = displayedTextIndices[1] - displayedTextIndices[0];
	}
	
	private void close() {
		text = null;
	}
	
	@Override
	public void dispose() {
		shapeRenderer.dispose();
		batch.dispose();
	}
}