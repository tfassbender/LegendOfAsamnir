package net.jfabricationgames.gdx.cutscene.action;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

import net.jfabricationgames.gdx.constants.Constants;
import net.jfabricationgames.gdx.util.GameUtil;

public class CutsceneColorTransitionAction extends AbstractCutsceneAction {
	
	private Color transitionColor;
	private float executionTime = -1f;
	
	private OrthographicCamera hudCamera;
	private ShapeRenderer shapeRenderer = new ShapeRenderer();
	
	public CutsceneColorTransitionAction(CutsceneUnitProvider unitProvider, CutsceneControlledActionConfig actionConfig,
			OrthographicCamera hudCamera) {
		super(unitProvider, actionConfig);
		
		transitionColor = GameUtil.getColorFromRGB(actionConfig.rgbColor, Color.BLACK);
		this.hudCamera = hudCamera;
	}
	
	@Override
	public void execute(float delta) {
		if (executionTime < 0f) {
			executionTime = 0;
		}
		executionTime += delta;
		
		if (isBeforeTransition()) {
			if (!actionConfig.fadeIntoColor) {
				drawColorTransition(1f);
			}
		}
		else if (isWithinTransition()) {
			float alpha = (executionTime - actionConfig.colorTransitionDelayInSeconds) / actionConfig.colorTransitionDurationInSeconds;
			if (!actionConfig.fadeIntoColor) {
				alpha = 1f - alpha;
			}
			drawColorTransition(alpha);
		}
		else if (isAfterTransition()) {
			if (actionConfig.fadeIntoColor) {
				drawColorTransition(1f);
			}
		}
	}
	
	private boolean isBeforeTransition() {
		return executionTime <= actionConfig.colorTransitionDelayInSeconds;
	}
	
	private boolean isWithinTransition() {
		return executionTime > actionConfig.colorTransitionDelayInSeconds
				&& executionTime <= actionConfig.colorTransitionDelayInSeconds + actionConfig.colorTransitionDurationInSeconds;
	}
	
	private boolean isAfterTransition() {
		return executionTime > actionConfig.colorTransitionDelayInSeconds + actionConfig.colorTransitionDurationInSeconds;
	}
	
	private void drawColorTransition(float alpha) {
		transitionColor.a = alpha;
		
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		shapeRenderer.setProjectionMatrix(hudCamera.combined);
		shapeRenderer.begin(ShapeType.Filled);
		shapeRenderer.setColor(transitionColor);
		shapeRenderer.rect(0, 0, Constants.HUD_SCENE_WIDTH, Constants.HUD_SCENE_HEIGHT);
		shapeRenderer.end();
		Gdx.gl.glDisable(GL20.GL_BLEND);
	}
	
	@Override
	public boolean isExecutionFinished() {
		return executionTime > actionConfig.colorTransitionDelayInSeconds + actionConfig.colorTransitionDurationInSeconds
				+ actionConfig.delayAfterColorTransitionInSeconds;
	}
	
	@Override
	public void dispose() {
		super.dispose();
		shapeRenderer.dispose();
	}
}
