package net.jfabricationgames.gdx.character;

import net.jfabricationgames.gdx.DwarfScrollerGame;
import net.jfabricationgames.gdx.character.animation.MovingDirection;
import net.jfabricationgames.gdx.input.InputActionListener;
import net.jfabricationgames.gdx.input.InputContext;

public class CharacterInputMovementHandler implements InputActionListener {
	
	private static final float SQRT_0_5 = (float) Math.sqrt(0.5f);
	
	private static final String INPUT_MOVE_UP = "up";
	private static final String INPUT_MOVE_DOWN = "down";
	private static final String INPUT_MOVE_LEFT = "left";
	private static final String INPUT_MOVE_RIGHT = "right";
	private static final String INPUT_JUMP = "jump";
	private static final String INPUT_ATTACK = "attack";
	
	private Dwarf inputCharacter;
	
	private boolean moveUp = false;
	private boolean moveDown = false;
	private boolean moveLeft = false;
	private boolean moveRight = false;
	private boolean jump = false;
	private boolean attack = false;
	
	private float idleTime;
	private float timeTillIdleAnimation;
	
	private MovingDirection jumpDirection;
	private MovingDirection lastMoveDirection;
	
	private InputContext inputContext;
	
	public CharacterInputMovementHandler(Dwarf inputCharacter) {
		this.inputCharacter = inputCharacter;
		timeTillIdleAnimation = inputCharacter.getTimeTillIdleAnimation();
		jumpDirection = MovingDirection.NONE;
		lastMoveDirection = MovingDirection.NONE;
		inputContext = DwarfScrollerGame.getInstance().getInputContext();
		inputContext.addListener(this);
	}
	
	public void handleInputs(float delta) {
		readInputs();
		
		boolean move = moveUp || moveDown || moveLeft || moveRight;
		
		if (attack) {
			if (getAction().isInterruptable()) {
				if (move) {
					lastMoveDirection = getDirectionFromInputs();
					jumpDirection = getDrawingDirectionChangesFromInputs();
					inputCharacter.changeAction(CharacterAction.ATTACK_JUMP);
				}
				else {
					inputCharacter.changeAction(CharacterAction.ATTACK);
				}
			}
		}
		else if (jump) {
			if (getAction().isInterruptable()) {
				jumpDirection = getDirectionFromInputs();
				inputCharacter.changeAction(CharacterAction.JUMP);
			}
		}
		else if (move) {
			lastMoveDirection = getDrawingDirectionChangesFromInputs();
			if (getAction().isInterruptable() && getAction() != CharacterAction.RUN) {
				inputCharacter.changeAction(CharacterAction.RUN);
			}
		}
		else {
			if (getAction() == CharacterAction.RUN) {
				inputCharacter.changeAction(CharacterAction.NONE);
			}
		}
		
		if (inputCharacter.getCurrentAction() == CharacterAction.NONE) {
			idleTime += delta;
			
			if (idleTime > timeTillIdleAnimation) {
				if (inputCharacter.getCurrentAction() != CharacterAction.IDLE) {
					inputCharacter.changeAction(CharacterAction.IDLE);
				}
				else if (inputCharacter.isAnimationFinished()) {
					inputCharacter.changeAction(CharacterAction.NONE);
					idleTime = 0;
				}
			}
		}
		else {
			idleTime = 0;
		}
	}
	
	private void readInputs() {
		resetInputFlags();
		if (inputContext.isStateActive(INPUT_MOVE_UP)) {
			moveUp = true;
		}
		if (inputContext.isStateActive(INPUT_MOVE_DOWN)) {
			moveDown = true;
		}
		if (inputContext.isStateActive(INPUT_MOVE_LEFT)) {
			moveLeft = true;
		}
		if (inputContext.isStateActive(INPUT_MOVE_RIGHT)) {
			moveRight = true;
		}
		if (inputContext.isStateActive(INPUT_JUMP)) {
			jump = true;
		}
		if (inputContext.isStateActive(INPUT_ATTACK)) {
			attack = true;
		}
		
		if (moveUp && moveDown) {
			moveUp = false;
			moveDown = false;
		}
		if (moveLeft && moveRight) {
			moveLeft = false;
			moveRight = false;
		}
	}
	
	private void resetInputFlags() {
		moveUp = false;
		moveDown = false;
		moveLeft = false;
		moveRight = false;
		jump = false;
		attack = false;
	}
	
	/**
	 * Get the current drawing direction (left or right) or the current drawing direction if no movement to the left or the right is done.
	 * 
	 * @return The drawing direction.
	 */
	private MovingDirection getDrawingDirectionChangesFromInputs() {
		if (moveLeft) {
			return MovingDirection.LEFT;
		}
		if (moveRight) {
			return MovingDirection.RIGHT;
		}
		return lastMoveDirection;
	}
	
	/**
	 * Get the current input direction (where horizontal directions have a higher priority than vertical directions)
	 * 
	 * @return The current direction from the inputs.
	 */
	private MovingDirection getDirectionFromInputs() {
		if (moveLeft) {
			return MovingDirection.LEFT;
		}
		if (moveRight) {
			return MovingDirection.RIGHT;
		}
		if (moveUp) {
			return MovingDirection.UP;
		}
		if (moveDown) {
			return MovingDirection.DOWN;
		}
		return MovingDirection.NONE;
	}
	
	public void move(float delta) {
		if (!getAction().isMoveBlocking()) {
			float moveSpeedPerDirection = inputCharacter.getMovingSpeed();
			if ((moveUp || moveDown) && (moveLeft || moveRight)) {
				moveSpeedPerDirection = inputCharacter.getMovingSpeed() * SQRT_0_5;
			}
			
			float speedX = 0;
			float speedY = 0;
			
			if (moveUp) {
				speedY = moveSpeedPerDirection;
			}
			if (moveDown) {
				speedY = -moveSpeedPerDirection;
			}
			if (moveLeft) {
				speedX = -moveSpeedPerDirection;
			}
			if (moveRight) {
				speedX = moveSpeedPerDirection;
			}
			move(speedX, speedY, delta);
		}
		else if (getAction() == CharacterAction.JUMP || getAction() == CharacterAction.ATTACK_JUMP) {
			float jumpingSpeed = inputCharacter.getJumpingSpeed();
			float speedX = 0;
			float speedY = 0;
			
			if (jumpDirection == MovingDirection.UP) {
				speedY = jumpingSpeed;
			}
			else if (jumpDirection == MovingDirection.DOWN) {
				speedY = -jumpingSpeed;
			}
			else if (jumpDirection == MovingDirection.LEFT) {
				speedX = -jumpingSpeed;
			}
			else if (jumpDirection == MovingDirection.RIGHT) {
				speedX = jumpingSpeed;
			}
			move(speedX, speedY, delta);
		}
	}
	
	private void move(float speedX, float speedY, float delta) {
		inputCharacter.move(speedX * delta, speedY * delta);
	}
	
	private CharacterAction getAction() {
		return inputCharacter.getCurrentAction();
	}
	
	public boolean isDrawDirectionRight() {
		return lastMoveDirection.isDrawingDirectionRight();
	}
	
	@Override
	public boolean onAction(String action, Type type, Parameters parameters) {
		//only states are used in this implementation
		return false;
	}
}
