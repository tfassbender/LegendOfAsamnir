package net.jfabricationgames.gdx.screens.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import net.jfabricationgames.gdx.DwarfScrollerGame;
import net.jfabricationgames.gdx.assets.AssetGroupManager;
import net.jfabricationgames.gdx.input.InputActionListener;
import net.jfabricationgames.gdx.input.InputContext;
import net.jfabricationgames.gdx.screens.game.GameScreen;

public class MainMenuScreen extends ScreenAdapter implements InputActionListener {
	
	public static final String ASSET_GROUP_NAME = "main_menu";
	public static final int VIRTUAL_WIDTH = 1280;
	public static final int VIRTUAL_HEIGHT = 720;
	
	private static final String CONTROLLER_ACTION_START_GAME = "start_game";
	private static final String INPUT_CONTEXT_NAME = "mainMenu";
	
	private AssetGroupManager assetManager;
	private InputContext inputContext;
	
	private Viewport viewport;
	
	private Table table;
	private Stage stage;
	
	public MainMenuScreen() {
		assetManager = AssetGroupManager.getInstance();
		assetManager.loadGroup(ASSET_GROUP_NAME);
		assetManager.finishLoading();
		
		DwarfScrollerGame.getInstance().changeInputContext(INPUT_CONTEXT_NAME);
		inputContext = DwarfScrollerGame.getInstance().getInputContext();
		inputContext.addListener(this);
		
		viewport = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
		stage = new Stage(viewport);
		DwarfScrollerGame.getInstance().addInputProcessor(stage);
		
		buildStage();
	}
	
	private void buildStage() {
		TextureAtlas menu = assetManager.get("packed/menu/menu.atlas");
		TextureRegion headlineTexture = menu.findRegion("dwarf_scroller_headline");
		TextureRegion playButtonTexture = menu.findRegion("play_button_text");
		TextureRegion exitButtonTexture = menu.findRegion("exit_button_text");
		
		Image headline = new Image(headlineTexture);
		
		ImageButtonStyle playButtonStyle = new ImageButtonStyle();
		playButtonStyle.up = new TextureRegionDrawable(menu.findRegion("button_wide_round_edges"));
		playButtonStyle.imageUp = new TextureRegionDrawable(playButtonTexture);
		ImageButton buttonPlay = new ImageButton(playButtonStyle);
		
		ImageButtonStyle exitButtonStyle = new ImageButtonStyle();
		exitButtonStyle.up = new TextureRegionDrawable(menu.findRegion("button_wide_round_edges"));
		exitButtonStyle.imageUp = new TextureRegionDrawable(exitButtonTexture);
		ImageButton buttonExit = new ImageButton(exitButtonStyle);
		
		// build the table
		table = new Table();
		
		table.row();
		table.add(headline).align(Align.center).padBottom(50f).expand();
		table.row();
		table.add(buttonPlay).padBottom(20).uniform();
		table.row();
		table.add(buttonExit).padBottom(20).uniform();
		
		table.setFillParent(true);
		table.pack();
		
		table.setPosition(0, VIRTUAL_HEIGHT);
		table.addAction(Actions.sequence(Actions.moveTo(0, -VIRTUAL_HEIGHT * 0.05f, 0.5f), Actions.moveTo(0, 0, 0.2f)));
		
		buttonPlay.addListener(new ClickListener() {
			
			@Override
			public void clicked(InputEvent event, float x, float y) {
				startGame();
			};
		});
		buttonExit.addListener(new ClickListener() {
			
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Gdx.app.exit();
			};
		});
		
		stage.addActor(table);
	}
	
	private void startGame() {
		DwarfScrollerGame.getInstance().setScreen(new GameScreen());
		dispose();
	}
	
	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
	}
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		stage.act(Math.min(delta, 1f / 60f));
		stage.draw();
	}
	
	@Override
	public void dispose() {
		DwarfScrollerGame.getInstance().removeInputProcessor(stage);
		assetManager.unloadGroup(ASSET_GROUP_NAME);
		inputContext.removeListener(this);
		
		stage.dispose();
	}
	
	@Override
	public boolean onAction(String action, Type type, Parameters parameters) {
		if (action.equals(CONTROLLER_ACTION_START_GAME)) {
			startGame();
		}
		return false;
	}
}
