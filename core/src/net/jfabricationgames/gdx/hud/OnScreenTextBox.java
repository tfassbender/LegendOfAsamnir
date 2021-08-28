package net.jfabricationgames.gdx.hud;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.Disposable;

import net.jfabricationgames.gdx.DwarfScrollerGame;
import net.jfabricationgames.gdx.condition.choice.PlayerChoice;
import net.jfabricationgames.gdx.event.EventConfig;
import net.jfabricationgames.gdx.event.EventHandler;
import net.jfabricationgames.gdx.event.EventType;
import net.jfabricationgames.gdx.input.InputActionListener;
import net.jfabricationgames.gdx.input.InputContext;
import net.jfabricationgames.gdx.text.ScreenTextWriter;
import net.jfabricationgames.gdx.util.GameUtil;

public class OnScreenTextBox implements Disposable, InputActionListener {
	
	protected static final float TEXT_SCALE = 1f;
	protected static final int DISPLAYABLE_LINES = 5;
	
	private static final Color[] TEXTURE_CONFIG = new Color[] { //
			new Color(0.95f, 0.95f, 0.95f, 1f), // bottom-left
			new Color(0.9f, 0.9f, 0.9f, 1f), // bottom-right
			new Color(0.99f, 0.99f, 0.99f, 1f), // top-right
			new Color(0.9f, 0.9f, 0.9f, 1f)// top-left
	};
	
	private static OnScreenTextBox instance;
	
	public static synchronized OnScreenTextBox getInstance() {
		if (instance == null) {
			throw new IllegalStateException("The instance of OnScreenTextBox has not yet been created. "
					+ "Use the createInstance(HeadsUpDisplay) method to create an instance.");
		}
		return instance;
	}
	
	protected static synchronized OnScreenTextBox createInstance(HeadsUpDisplay hud) {
		instance = new OnScreenTextBox(hud);
		return instance;
	}
	
	protected final float textBoxX;
	protected final float textBoxY;
	protected final float textBoxWidth;
	protected final float textBoxHeight;
	
	protected final float textBoxEdge;
	protected final float textOffsetX;
	protected final float textOffsetY;
	protected final float textWidth;
	protected final float headerOffsetY;
	
	private OnScreenTextRenderer textRenderer;
	private OnScreenPlayerChoiceRenderer playerChoiceRenderer;
	
	private OrthographicCamera camera;
	private SpriteBatch batch;
	private ShapeRenderer shapeRenderer;
	
	private ScreenTextWriter screenTextWriter;
	
	private String headerText;
	private Color headerColor = Color.RED;
	
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
		screenTextWriter.setFont(HeadsUpDisplay.DEFAULT_FONT_NAME);
		
		textRenderer = new OnScreenTextRenderer(this, batch, shapeRenderer, screenTextWriter);
		playerChoiceRenderer = new OnScreenPlayerChoiceRenderer(this, hud, batch, shapeRenderer, screenTextWriter);
		
		InputContext inputContext = DwarfScrollerGame.getInstance().getInputContext();
		inputContext.addListener(this);
	}
	
	public void render(float delta) {
		if (isDisplaying()) {
			batch.setProjectionMatrix(camera.combined);
			shapeRenderer.setProjectionMatrix(camera.combined);
			
			renderBackground();
			renderHeader();
			
			if (textRenderer.isDisplaying()) {
				textRenderer.render(delta);
			}
			else if (playerChoiceRenderer.isDisplaying()) {
				playerChoiceRenderer.render(delta);
			}
		}
	}
	
	private void renderHeader() {
		if (headerText != null) {
			screenTextWriter.setScale(1.25f * TEXT_SCALE);
			screenTextWriter.setColor(headerColor);
			screenTextWriter.drawText(headerText, textBoxX + textOffsetX, textBoxY + textBoxHeight - headerOffsetY);
		}
	}
	
	private void renderBackground() {
		shapeRenderer.begin(ShapeType.Filled);
		
		shapeRenderer.setColor(Color.BLACK);
		shapeRenderer.rect(textBoxX, textBoxY, textBoxWidth, textBoxHeight);
		shapeRenderer.rect(textBoxX + textBoxEdge, textBoxY + textBoxEdge, textBoxWidth - 2f * textBoxEdge, textBoxHeight - 2f * textBoxEdge,
				TEXTURE_CONFIG[0], TEXTURE_CONFIG[1], TEXTURE_CONFIG[2], TEXTURE_CONFIG[3]);
		
		shapeRenderer.end();
	}
	
	public void setText(String text) {
		setText(text, false);
	}
	
	public void setText(String text, boolean showNextPageIcon) {
		textRenderer.setText(text, showNextPageIcon);
	}
	
	public String getHeaderText() {
		return headerText;
	}
	
	public void setHeaderText(String headerText) {
		setHeaderText(headerText, Color.RED);
	}
	public void setHeaderText(String headerText, Color headerColor) {
		this.headerText = headerText;
		this.headerColor = headerColor;
	}
	
	public void showPlayerChoice(PlayerChoice playerChoice) {
		Color headerColor = GameUtil.getColorFromRGB(playerChoice.headerColor, Color.RED);
		setHeaderText(playerChoice.header, headerColor);
		
		playerChoiceRenderer.setPlayerChoice(playerChoice);
	}
	
	@Override
	public Priority getInputPriority() {
		return InputActionListener.Priority.ON_SCREEN_TEXT;
	}
	
	public boolean isDisplaying() {
		return textRenderer.isDisplaying() || playerChoiceRenderer.isDisplaying();
	}
	
	protected void close() {
		EventHandler.getInstance().fireEvent(new EventConfig().setEventType(EventType.ON_SCREEN_TEXT_ENDED));
		textRenderer.clear();
		playerChoiceRenderer.clear();
	}
	
	@Override
	public void dispose() {
		shapeRenderer.dispose();
		batch.dispose();
	}
	
	@Override
	public boolean onAction(String action, Type type, Parameters parameters) {
		if (textRenderer.onAction(action, type, parameters) || playerChoiceRenderer.onAction(action, type, parameters)) {
			return true;
		}
		return false;
	}
}
