package net.jfabricationgames.gdx.character;

import net.jfabricationgames.gdx.DwarfScrollerGame;
import net.jfabricationgames.gdx.character.container.CharacterItemContainer;
import net.jfabricationgames.gdx.input.InputActionListener;
import net.jfabricationgames.gdx.input.InputContext;
import net.jfabricationgames.gdx.interaction.InteractionManager;

public class CharacterInputProcessor implements InputActionListener {
	
	private static final float SQRT_0_5 = (float) Math.sqrt(0.5f);
	
	private static final String INPUT_MOVE_UP = "up";
	private static final String INPUT_MOVE_DOWN = "down";
	private static final String INPUT_MOVE_LEFT = "left";
	private static final String INPUT_MOVE_RIGHT = "right";
	private static final String INPUT_SPECIAL = "special";
	private static final String INPUT_ATTACK = "attack";
	private static final String INPUT_SPRINT = "sprint";
	private static final String INPUT_BLOCK = "block";
	
	private static final String ACTION_INTERACT = "interact";
	
	private PlayableCharacter inputCharacter;
	private CharacterItemContainer itemContainer;
	
	private boolean moveUp = false;
	private boolean moveDown = false;
	private boolean moveLeft = false;
	private boolean moveRight = false;
	private boolean special = false;
	private boolean attack = false;
	private boolean sprint = false;
	private boolean block = false;
	private boolean changeSprint = false;
	private boolean spinAttack = false;
	
	private boolean attackReleased = false;
	
	private float idleTime;
	private float timeTillIdleAnimation;
	private float attackHeld;
	private float timeTillSpinAttack;
	
	private MovingDirection jumpDirection;
	private MovingDirection lastMoveDirection;
	
	private InputContext inputContext;
	
	private boolean spinAttackCharged;
	
	public CharacterInputProcessor(PlayableCharacter inputCharacter, CharacterItemContainer itemContainer) {
		this.inputCharacter = inputCharacter;
		this.itemContainer = itemContainer;
		timeTillIdleAnimation = inputCharacter.getTimeTillIdleAnimation();
		timeTillSpinAttack = inputCharacter.getHoldTimeTillSpinAttack();
		jumpDirection = MovingDirection.NONE;
		lastMoveDirection = MovingDirection.NONE;
		inputContext = DwarfScrollerGame.getInstance().getInputContext();
		inputContext.addListener(this);
	}
	
	public void handleInputs(float delta) {
		readInputs(delta);
		
		boolean move = moveUp || moveDown || moveLeft || moveRight;
		boolean characterActionSet = false;
		
		if (inputCharacter.isAlive()) {
			if (!characterActionSet && spinAttack) {
				//hit and shield hit can only be interrupted by spin attacks (to free from near enemies)
				if (getAction().isInterruptable() || getAction() == CharacterAction.HIT || getAction() == CharacterAction.SHIELD_HIT) {
					inputCharacter.changeAction(CharacterAction.ATTACK_SPIN);
				}
			}
			if (!characterActionSet && attack) {
				if (getAction().isInterruptable()) {
					if (move && sprint) {
						lastMoveDirection = getDirectionFromInputs();
						jumpDirection = getDirectionFromInputs();
						characterActionSet = inputCharacter.changeAction(CharacterAction.ATTACK_JUMP);
					}
					else {
						characterActionSet = inputCharacter.changeAction(CharacterAction.ATTACK);
					}
				}
			}
			if (!characterActionSet && block) {
				if (getAction().isInterruptable()) {
					characterActionSet = inputCharacter.changeAction(CharacterAction.BLOCK);
				}
			}
			if (!characterActionSet && special) {
				if (getAction().isInterruptable()) {
					jumpDirection = getDirectionFromInputs();
					characterActionSet = inputCharacter.executeSpecialAction();
				}
			}
			if (!characterActionSet && move) {
				lastMoveDirection = getDirectionFromInputs();
				if (getAction().isInterruptable() && getAction() != CharacterAction.RUN) {
					characterActionSet = inputCharacter.changeAction(CharacterAction.RUN);
				}
			}
			else {
				if (getAction() == CharacterAction.RUN) {
					characterActionSet = inputCharacter.changeAction(CharacterAction.NONE);
				}
			}
			
			if (inputCharacter.getCurrentAction() == CharacterAction.NONE) {
				sprint = false;
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
	}
	
	private void readInputs(float delta) {
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
		if (inputContext.isStateActive(INPUT_SPECIAL)) {
			special = true;
		}
		if (inputContext.isStateActive(INPUT_ATTACK)) {
			if (attackReleased) {
				attack = true;
				attackReleased = false;
			}
			
			attackHeld += delta;
			if (attackHeld >= timeTillSpinAttack && !spinAttackCharged) {
				spinAttackCharged = true;
				inputCharacter.playSpinAttackChargedSound();
			}
		}
		else {
			if (spinAttackCharged) {
				spinAttack = true;
				spinAttackCharged = false;
			}
			attackHeld = 0;
			attackReleased = true;
		}
		if (inputContext.isStateActive(INPUT_BLOCK)) {
			block = true;
		}
		if (inputContext.isStateActive(INPUT_SPRINT)) {
			if (!changeSprint) {
				sprint = !sprint;
			}
			changeSprint = true;
		}
		else {
			changeSprint = false;
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
		attack = false;
		special = false;
		block = false;
		spinAttack = false;
		//attack is not reset, because the attackReleased flag does this
		//sprint is not reset here, but in the handleInputs method (when idle)
	}
	
	/**
	 * Get the current input direction (where horizontal directions have a higher priority than vertical directions)
	 * 
	 * @return The current direction from the inputs.
	 */
	private MovingDirection getDirectionFromInputs() {
		if (moveUp && moveRight) {
			return MovingDirection.UP_RIGHT;
		}
		if (moveUp && moveLeft) {
			return MovingDirection.UP_LEFT;
		}
		if (moveDown && moveRight) {
			return MovingDirection.DOWN_RIGHT;
		}
		if (moveDown && moveLeft) {
			return MovingDirection.DOWN_LEFT;
		}
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
			//reduce the endurance for sprinting before requesting the movement speed
			if (sprint) {
				inputCharacter.reduceEnduranceForSprinting(delta);
				if (inputCharacter.isExhausted()) {
					sprint = false;
				}
			}
			
			float moveSpeedPerDirection = inputCharacter.getMovingSpeed(sprint);
			if ((moveUp || moveDown) && (moveLeft || moveRight)) {
				moveSpeedPerDirection = inputCharacter.getMovingSpeed(sprint) * SQRT_0_5;
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
			float movingSpeed = inputCharacter.getMovingSpeed(sprint);
			float speedX = 0;
			float speedY = 0;
			
			if (jumpDirection.isCombinedDirection()) {
				movingSpeed *= SQRT_0_5;
			}
			
			if (jumpDirection.containsDirection(MovingDirection.UP)) {
				speedY = movingSpeed;
			}
			if (jumpDirection.containsDirection(MovingDirection.DOWN)) {
				speedY = -movingSpeed;
			}
			if (jumpDirection.containsDirection(MovingDirection.LEFT)) {
				speedX = -movingSpeed;
			}
			if (jumpDirection.containsDirection(MovingDirection.RIGHT)) {
				speedX = movingSpeed;
			}
			move(speedX, speedY, delta);
		}
		else if (getAction() == CharacterAction.BLOCK) {
			if (moveLeft) {
				lastMoveDirection = MovingDirection.LEFT;
			}
			if (moveRight) {
				lastMoveDirection = MovingDirection.RIGHT;
			}
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
	
	public MovingDirection getMovingDirection() {
		return lastMoveDirection;
	}
	
	@Override
	public boolean onAction(String action, Type type, Parameters parameters) {
		if (action.equals(ACTION_INTERACT) && (type == Type.KEY_DOWN || type == Type.CONTROLLER_BUTTON_PRESSED)) {
			InteractionManager.getInstance().interact(inputCharacter, itemContainer);
		}
		return false;
	}
}
