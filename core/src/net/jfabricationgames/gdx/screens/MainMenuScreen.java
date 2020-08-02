package net.jfabricationgames.gdx.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
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
import net.jfabricationgames.gdx.text.ScreenTextWriter;

public class MainMenuScreen extends ScreenAdapter {
	
	public static final String ASSET_GROUP_NAME = "main_menu";
	public static final String FONT_NAME = "vikingMedium";
	public static final int VIRTUAL_WIDTH = 1280;
	public static final int VIRTUAL_HEIGHT = 720;
	
	private AssetGroupManager assetManager;
	
	private Viewport viewport;
	
	private ScreenTextWriter screenTextWriter;
	
	private Table table;
	private Stage stage;
	
	public MainMenuScreen() {
		assetManager = AssetGroupManager.getInstance();
		assetManager.loadGroup(ASSET_GROUP_NAME);
		assetManager.finishLoading();
		
		viewport = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
		stage = new Stage(viewport);
		DwarfScrollerGame.getInstance().addInputProcessor(stage);
		
		screenTextWriter = new ScreenTextWriter();
		screenTextWriter.setFont(FONT_NAME);
		
		buildStage();
	}
	
	private void buildStage() {
		TextureAtlas menuButtons = assetManager.get("packed/menu/menu_buttons.atlas");
		Texture headlineTexture = assetManager.get("menu/dwarf_scroller_headline.png");
		TextureRegion playButtonTexture = menuButtons.findRegion("play_button_text");
		TextureRegion exitButtonTexture = menuButtons.findRegion("exit_button_text");
		
		Image headline = new Image(headlineTexture);
		
		ImageButtonStyle playButtonStyle = new ImageButtonStyle();
		playButtonStyle.up = new TextureRegionDrawable(menuButtons.findRegion("button_wide_round_edges"));
		playButtonStyle.imageUp = new TextureRegionDrawable(playButtonTexture);
		ImageButton buttonPlay = new ImageButton(playButtonStyle);
		
		ImageButtonStyle exitButtonStyle = new ImageButtonStyle();
		exitButtonStyle.up = new TextureRegionDrawable(menuButtons.findRegion("button_wide_round_edges"));
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
				DwarfScrollerGame.getInstance().setScreen(new GameScreen());
				dispose();
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
		
		stage.dispose();
	}
}
