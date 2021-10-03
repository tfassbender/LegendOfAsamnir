package net.jfabricationgames.gdx.screens.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;

import net.jfabricationgames.gdx.Game;
import net.jfabricationgames.gdx.animation.AnimationManager;
import net.jfabricationgames.gdx.character.player.PlayableCharacter;
import net.jfabricationgames.gdx.character.player.Player;
import net.jfabricationgames.gdx.input.InputManager;
import net.jfabricationgames.gdx.screens.game.GameScreen;
import net.jfabricationgames.gdx.screens.menu.control.ControlledMenu;

public abstract class InGameMenuScreen<T extends ControlledMenu<T>> extends MenuScreen<T> {
	
	protected GameScreen gameScreen;
	protected FrameBuffer gameSnapshotFrameBuffer;
	protected Sprite gameSnapshotSprite;
	
	protected PlayableCharacter player;
	
	public InGameMenuScreen(GameScreen gameScreen, String... stateConfigFiles) {
		super(stateConfigFiles);
		this.gameScreen = gameScreen;
		
		gameSnapshotFrameBuffer = new FrameBuffer(Format.RGB888, VIRTUAL_WIDTH, VIRTUAL_HEIGHT, false);
		
		AnimationManager.getInstance().loadAnimations(ANIMATION_CONFIG_FILE);
		
		player = Player.getInstance();
	}
	
	protected void takeGameSnapshot() {
		gameSnapshotFrameBuffer.bind();
		gameScreen.render(0f);
		FrameBuffer.unbind();
		
		Texture gameSnapshotTexture = gameSnapshotFrameBuffer.getColorBufferTexture();
		gameSnapshotSprite = new Sprite(gameSnapshotTexture);
		gameSnapshotSprite.flip(false, true);
		gameSnapshotSprite.setColor(Color.GRAY);
	}
	
	@Override
	public void dispose() {
		super.dispose();
		gameSnapshotFrameBuffer.dispose();
	}
	
	//****************************************************************
	//*** State machine methods (called via reflection)
	//****************************************************************
	
	public void backToGame() {
		removeInputListener();
		Game game = Game.getInstance();
		InputManager.getInstance().changeInputContext(GameScreen.INPUT_CONTEXT_NAME);
		game.setScreen(gameScreen);
	}
	
	public void respawnInLastCheckpoint() {
		Gdx.app.debug(getClass().getSimpleName(), "'Respawn' selected");
		player.respawn();
		backToGame();
	}
	
	public void restartGame() {
		Gdx.app.debug(getClass().getSimpleName(), "'Restart Game' selected");
		gameScreen.restartGame();
		backToGame();
	}
}
