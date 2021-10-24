package net.jfabricationgames.gdx.camera;

import java.util.function.Supplier;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;

import net.jfabricationgames.gdx.constants.Constants;

public class CameraMovementHandler {
	
	public static final float CAMERA_MOVEMENT_SPEED = 150.0f * Constants.WORLD_TO_SCREEN;
	
	private static CameraMovementHandler instance;
	
	private static final float MOVEMENT_EDGE_OFFSET = 75f;
	private static final float MOVEMENT_RANGE_X = Constants.SCENE_WIDTH * 0.5f - MOVEMENT_EDGE_OFFSET * Constants.WORLD_TO_SCREEN;
	private static final float MOVEMENT_RANGE_Y = Constants.SCENE_HEIGHT * 0.5f - MOVEMENT_EDGE_OFFSET * Constants.WORLD_TO_SCREEN;
	
	public static synchronized CameraMovementHandler createInstanceIfAbsent(OrthographicCamera camera, Supplier<Vector2> playerPosition,
			Supplier<Boolean> isCameraControlledByCutscene) {
		if (instance == null) {
			instance = new CameraMovementHandler(camera, playerPosition, isCameraControlledByCutscene);
		}
		
		return getInstance();
	}
	
	public static CameraMovementHandler getInstance() {
		if (instance == null) {
			throw new IllegalStateException("The instance of CameraMovementHandler has not yet been created. "
					+ "Use the createInstance(OrtographicCamera, PlayableCharacter) method to create an instance.");
		}
		return instance;
	}
	
	private OrthographicCamera camera;
	private Supplier<Boolean> isCameraControlledByCutscene;
	private Supplier<Vector2> playerPosition;
	
	private CameraMovementHandler(OrthographicCamera camera, Supplier<Vector2> playerPosition, Supplier<Boolean> isCutscene) {
		this.camera = camera;
		this.playerPosition = playerPosition;
		this.isCameraControlledByCutscene = isCutscene;
	}
	
	public void moveCamera(float delta) {
		if (!isCameraControlledByCutscene.get()) {
			moveCameraToPlayer();
		}
		
		camera.update();
	}
	
	public void moveCamera(float deltaX, float deltaY) {
		camera.position.x += deltaX;
		camera.position.y += deltaY;
	}
	
	public void centerCameraOnPlayer() {
		Vector2 position = playerPosition.get();
		camera.position.x = position.x;
		camera.position.y = position.y;
	}
	
	public Vector2 getCameraPosition() {
		return new Vector2(camera.position.x, camera.position.y);
	}
	
	private void moveCameraToPlayer() {
		Vector2 position = playerPosition.get();
		
		//movement in positive X and Y direction
		float deltaX = camera.position.x - position.x;
		float deltaY = camera.position.y - position.y;
		float movementXPos = deltaX - MOVEMENT_RANGE_X;
		float movementYPos = deltaY - MOVEMENT_RANGE_Y;
		
		//movement in negative X and Y direction
		deltaX = position.x - camera.position.x;
		deltaY = position.y - camera.position.y;
		float movementXNeg = deltaX - MOVEMENT_RANGE_X;
		float movementYNeg = deltaY - MOVEMENT_RANGE_Y;
		
		camera.position.x -= Math.max(movementXPos, 0);
		camera.position.y -= Math.max(movementYPos, 0);
		
		camera.position.x += Math.max(movementXNeg, 0);
		camera.position.y += Math.max(movementYNeg, 0);
	}
}
