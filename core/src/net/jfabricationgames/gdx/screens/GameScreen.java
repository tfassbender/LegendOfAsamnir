package net.jfabricationgames.gdx.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import net.jfabricationgames.gdx.screens.animation.RunningDwarf;

public class GameScreen extends ScreenAdapter {

	private SpriteBatch batch;
	private AssetManager assetManager;
	
	private TextureAtlas dwarfAttackLeftAtlas;
	private TextureRegion dwarfTexture;
	
	private RunningDwarf runningDwarf;
	
	public GameScreen() {
		assetManager = new AssetManager();
		assetManager.load("packed/dwarf/dwarf_left_attack.atlas", TextureAtlas.class);
		assetManager.load("packed/dwarf/dwarf_right_run.atlas", TextureAtlas.class);
		assetManager.finishLoading();

		batch = new SpriteBatch();
		
		dwarfAttackLeftAtlas = assetManager.get("packed/dwarf/dwarf_left_attack.atlas", TextureAtlas.class);
		dwarfTexture = dwarfAttackLeftAtlas.findRegion("dwarf_left_attack", 1);//indices starting at 1
		
		runningDwarf = new RunningDwarf(this);
	}
	
	@Override
	public void render(float delta) {
		//clear the screen (with a black screen)
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		batch.begin();
		batch.draw(dwarfTexture, 500, 500, dwarfTexture.getRegionWidth()*5, dwarfTexture.getRegionHeight()*5);
		runningDwarf.render(delta, batch);
		batch.end();
	}
	
	@Override
	public void hide() {
		dispose();
	}
	
	@Override
	public void dispose() {
		dwarfAttackLeftAtlas.dispose();
		batch.dispose();
	}

	
	public AssetManager getAssetManager() {
		return assetManager;
	}
}
