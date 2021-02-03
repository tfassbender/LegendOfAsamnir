package net.jfabricationgames.gdx.hud;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

import net.jfabricationgames.gdx.condition.choice.PlayerChoice;
import net.jfabricationgames.gdx.event.EventConfig;
import net.jfabricationgames.gdx.event.EventHandler;
import net.jfabricationgames.gdx.event.EventType;
import net.jfabricationgames.gdx.input.InputActionListener.Parameters;
import net.jfabricationgames.gdx.input.InputActionListener.Type;
import net.jfabricationgames.gdx.text.ScreenTextWriter;

public class OnScreenPlayerChoiceRenderer {
	
	private final float OPTION_OFFSET_X;
	private final float OPTION_LINE_OFFSET_Y;
	
	private OnScreenTextBox onScreenTextBox;
	
	private SpriteBatch batch;
	private ShapeRenderer shapeRenderer;
	private ScreenTextWriter screenTextWriter;
	
	private PlayerChoice playerChoice;
	
	private int selectedOption = 0;
	
	public OnScreenPlayerChoiceRenderer(OnScreenTextBox onScreenTextBox, HeadsUpDisplay hud, SpriteBatch batch, ShapeRenderer shapeRenderer,
			ScreenTextWriter screenTextWriter) {
		this.onScreenTextBox = onScreenTextBox;
		this.batch = batch;
		this.shapeRenderer = shapeRenderer;
		this.screenTextWriter = screenTextWriter;
		
		OPTION_OFFSET_X = hud.getHudSceneWidth() * 0.03f;
		OPTION_LINE_OFFSET_Y = -(hud.getHudSceneHeight() * 0.065f);
	}
	
	protected void render(float delta) {
		batch.begin();
		
		screenTextWriter.setScale(OnScreenTextBox.TEXT_SCALE);
		screenTextWriter.setColor(Color.BLACK);
		screenTextWriter.drawText(playerChoice.description, onScreenTextBox.textBoxX + onScreenTextBox.textOffsetX,
				onScreenTextBox.textBoxY + onScreenTextBox.textBoxHeight - onScreenTextBox.textOffsetY);
		
		for (int i = 0; i < playerChoice.options.size; i++) {
			String optionText = playerChoice.options.get(i);
			screenTextWriter.drawText(optionText, onScreenTextBox.textBoxX + onScreenTextBox.textOffsetX + OPTION_OFFSET_X,
					onScreenTextBox.textBoxY + onScreenTextBox.textBoxHeight - onScreenTextBox.textOffsetY + (i + 1) * OPTION_LINE_OFFSET_Y);
		}
		
		drawOptionSelectionIcon();
		
		batch.end();
	}
	
	private void drawOptionSelectionIcon() {
		shapeRenderer.begin(ShapeType.Filled);
		
		shapeRenderer.setColor(Color.BLACK);
		//draw an arrow in front of the selected option
		float lineOffsetY = selectedOption * OPTION_LINE_OFFSET_Y;
		shapeRenderer.triangle(60f, 188f + lineOffsetY, 91f, 172f + lineOffsetY, 60f, 156f + lineOffsetY);
		
		shapeRenderer.end();
	}
	
	protected boolean isDisplaying() {
		return playerChoice != null;
	}
	
	public void setPlayerChoice(PlayerChoice playerChoice) {
		this.playerChoice = playerChoice;
		selectedOption = 0;
	}
	
	public void clear() {
		playerChoice = null;
	}
	
	protected boolean onAction(String action, Type type, Parameters parameters) {
		if (isDisplaying()) {
			if (action.equals("interact") && (type == Type.KEY_DOWN || type == Type.CONTROLLER_BUTTON_PRESSED)) {
				choseSelectedOption();
				return true;
			}
			else if (action.equals("choice_up") && (type == Type.KEY_DOWN || type == Type.CONTROLLER_BUTTON_PRESSED)) {
				selectedOption = (selectedOption + playerChoice.options.size - 1) % playerChoice.options.size;
				return true;
			}
			else if (action.equals("choice_down") && (type == Type.KEY_DOWN || type == Type.CONTROLLER_BUTTON_PRESSED)) {
				selectedOption = (selectedOption + 1) % playerChoice.options.size;
				return true;
			}
		}
		return false;
	}
	
	private void choseSelectedOption() {
		EventConfig event = new EventConfig().setEventType(EventType.CUTSCENE_PLAYER_CHOICE).setIntValue(selectedOption);
		EventHandler.getInstance().fireEvent(event);
		onScreenTextBox.close();
	}
}
