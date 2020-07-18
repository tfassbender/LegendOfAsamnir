package net.jfabricationgames.gdx.screens.animation;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import net.jfabricationgames.gdx.screens.GameScreen;

public class RunningDwarf {
	
	private Animation<TextureRegion> runAnimation;
	
	private float stateTime;
	
	public RunningDwarf(GameScreen gameScreen) {
		TextureAtlas atlas = gameScreen.getAssetManager().get("packed/dwarf/dwarf_run_right.atlas", TextureAtlas.class);
		runAnimation = new Animation<TextureRegion>(0.1f, atlas.findRegions("dwarf_run_right"), PlayMode.LOOP);
	}
	
	public void render(float delta, SpriteBatch batch) {
		stateTime += delta;
		TextureRegion currentFrame = runAnimation.getKeyFrame(stateTime);
		batch.draw(currentFrame, 500, 200, currentFrame.getRegionWidth() * 5, currentFrame.getRegionHeight() * 5);
	}
}
