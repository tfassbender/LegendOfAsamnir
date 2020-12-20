package net.jfabricationgames.gdx.camera;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import net.jfabricationgames.gdx.DwarfScrollerGame;
import net.jfabricationgames.gdx.character.PlayableCharacter;
import net.jfabricationgames.gdx.cutscene.CutsceneHandler;
import net.jfabricationgames.gdx.input.InputContext;
import net.jfabricationgames.gdx.screens.game.GameScreen;

public class CameraMovementHandler {
	
	public static final float CAMERA_MOVEMENT_SPEED = 150.0f * GameScreen.WORLD_TO_SCREEN;
	
	private static final float CAMERA_ZOOM_SPEED = 2.0f;
	private static final float CAMERA_ZOOM_MAX = 2.0f;
	private static final float CAMERA_ZOOM_MIN = 0.25f;
	
	private static final float MOVEMENT_EDGE_OFFSET = 75f;
	private static final float MOVEMENT_RANGE_X = GameScreen.SCENE_WIDTH * 0.5f - MOVEMENT_EDGE_OFFSET * GameScreen.WORLD_TO_SCREEN;
	private static final float MOVEMENT_RANGE_Y = GameScreen.SCENE_HEIGHT * 0.5f - MOVEMENT_EDGE_OFFSET * GameScreen.WORLD_TO_SCREEN;
	
	private static final String INPUT_AXIS_CAMERA_VERTICAL_MOVMENT = "camera_vertical_move_axis";
	private static final String INPUT_AXIS_CAMERA_HORIZONTAL_MOVMENT = "camera_horizontal_move_axis";
	private static final float INPUT_AXIS_CAMERA_MOVEMENT_THRESHOLD = 0.3f;
	
	private OrthographicCamera camera;
	private PlayableCharacter player;
	private CutsceneHandler cutsceneHandler;
	
	private InputContext inputContext;
	
	public CameraMovementHandler(OrthographicCamera camera, PlayableCharacter player) {
		this.camera = camera;
		this.player = player;
		cutsceneHandler = CutsceneHandler.getInstance();
		
		inputContext = DwarfScrollerGame.getInstance().getInputContext();
	}
	
	public void moveCamera(float delta) {
		if (!cutsceneHandler.isCutsceneActive()) {
			if (GameScreen.DEBUG) {
				moveDebugCamera(delta);
			}
			moveCameraToPlayer();
		}
		
		camera.update();
	}
	
	private void moveDebugCamera(float delta) {
		float cameraMovementAxisVertically = inputContext.getControllerAxisValue(INPUT_AXIS_CAMERA_VERTICAL_MOVMENT);
		float cameraMovementAxisHorizontally = inputContext.getControllerAxisValue(INPUT_AXIS_CAMERA_HORIZONTAL_MOVMENT);
		float cameraMovementSpeedX = 0;
		float cameraMovementSpeedY = 0;
		if (Math.abs(cameraMovementAxisHorizontally) > INPUT_AXIS_CAMERA_MOVEMENT_THRESHOLD) {
			cameraMovementSpeedX = CAMERA_MOVEMENT_SPEED * cameraMovementAxisHorizontally * delta;
		}
		if (Math.abs(cameraMovementAxisVertically) > INPUT_AXIS_CAMERA_MOVEMENT_THRESHOLD) {
			cameraMovementSpeedY = -CAMERA_MOVEMENT_SPEED * cameraMovementAxisVertically * delta;
		}
		
		if (Gdx.input.isKeyPressed(Keys.LEFT)) {
			cameraMovementSpeedX = -CAMERA_MOVEMENT_SPEED * delta;
		}
		else if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
			cameraMovementSpeedX = CAMERA_MOVEMENT_SPEED * delta;
		}
		
		if (Gdx.input.isKeyPressed(Keys.UP)) {
			cameraMovementSpeedY = CAMERA_MOVEMENT_SPEED * delta;
		}
		else if (Gdx.input.isKeyPressed(Keys.DOWN)) {
			cameraMovementSpeedY = -CAMERA_MOVEMENT_SPEED * delta;
		}
		
		moveCamera(cameraMovementSpeedX, cameraMovementSpeedY);
		
		if (Gdx.input.isKeyPressed(Keys.PAGE_UP)) {
			camera.zoom -= CAMERA_ZOOM_SPEED * delta;
		}
		else if (Gdx.input.isKeyPressed(Keys.PAGE_DOWN)) {
			camera.zoom += CAMERA_ZOOM_SPEED * delta;
		}
		
		camera.zoom = MathUtils.clamp(camera.zoom, CAMERA_ZOOM_MIN, CAMERA_ZOOM_MAX);
	}
	
	private void moveCamera(float deltaX, float deltaY) {
		camera.position.x += deltaX;
		camera.position.y += deltaY;
	}
	
	private void moveCameraToPlayer() {
		Vector2 playerPosition = player.getPosition();
		
		//movement in positive X and Y direction
		float deltaX = camera.position.x - playerPosition.x;
		float deltaY = camera.position.y - playerPosition.y;
		float movementXPos = deltaX - MOVEMENT_RANGE_X;
		float movementYPos = deltaY - MOVEMENT_RANGE_Y;
		
		//movement in negative X and Y direction
		deltaX = playerPosition.x - camera.position.x;
		deltaY = playerPosition.y - camera.position.y;
		float movementXNeg = deltaX - MOVEMENT_RANGE_X;
		float movementYNeg = deltaY - MOVEMENT_RANGE_Y;
		
		camera.position.x -= Math.max(movementXPos, 0);
		camera.position.y -= Math.max(movementYPos, 0);
		
		camera.position.x += Math.max(movementXNeg, 0);
		camera.position.y += Math.max(movementYNeg, 0);
	}
}
