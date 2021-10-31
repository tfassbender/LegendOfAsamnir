package net.jfabricationgames.gdx.cutscene.action;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

import net.jfabricationgames.gdx.constants.Constants;

public class CutsceneWaitAction extends AbstractCutsceneAction {
	
	private float executionTime = -1f;
	
	private OrthographicCamera hudCamera;
	private ShapeRenderer shapeRenderer = new ShapeRenderer();
	
	public CutsceneWaitAction(CutsceneUnitProvider unitProvider, CutsceneControlledActionConfig actionConfig, OrthographicCamera hudCamera) {
		super(unitProvider, actionConfig);
		this.hudCamera = hudCamera;
	}
	
	@Override
	public void execute(float delta) {
		if (executionTime < 0f) {
			executionTime = 0;
		}
		executionTime += delta;
		
		if (actionConfig.showBlackScreen) {
			shapeRenderer.setProjectionMatrix(hudCamera.combined);
			shapeRenderer.begin(ShapeType.Filled);
			shapeRenderer.setColor(Color.BLACK);
			shapeRenderer.rect(0f, 0f, Constants.HUD_SCENE_WIDTH, Constants.HUD_SCENE_HEIGHT);
			shapeRenderer.end();
		}
	}
	
	@Override
	public boolean isExecutionFinished() {
		return executionTime > actionConfig.executionDurationInSeconds;
	}
}
