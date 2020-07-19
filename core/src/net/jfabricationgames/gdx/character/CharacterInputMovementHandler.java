package net.jfabricationgames.gdx.character;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;

import net.jfabricationgames.gdx.character.animation.MovingDirection;

public class CharacterInputMovementHandler {
	
	private Dwarf inputCharacter;
	
	private boolean moveUp = false;
	private boolean moveDown = false;
	private boolean moveLeft = false;
	private boolean moveRight = false;
	private boolean jump = false;
	private boolean attack = false;
	
	private MovingDirection jumpDirection;
	private MovingDirection direction;
	
	public CharacterInputMovementHandler(Dwarf inputCharacter) {
		this.inputCharacter = inputCharacter;
		jumpDirection = MovingDirection.NONE;
		direction = MovingDirection.NONE;
	}
	
	public void handleInputs(float delta) {
		readInputs();
		
		boolean move = moveUp || moveDown || moveLeft || moveRight;
		
		if (attack) {
			if (getAction().isInterruptable()) {
				if (move) {
					direction = getDirectionFromInputs();
					jumpDirection = getDirectionFromInputs();
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
			if (getAction().isInterruptable() && getAction() != CharacterAction.RUN) {
				direction = getDirectionFromInputs();
				inputCharacter.changeAction(CharacterAction.RUN);
			}
		}
		else {
			if (getAction() == CharacterAction.RUN) {
				inputCharacter.changeAction(CharacterAction.NONE);
			}
		}
	}
	
	private void readInputs() {
		resetInputFlags();
		if (Gdx.input.isKeyPressed(Keys.W)) {
			moveUp = true;
		}
		if (Gdx.input.isKeyPressed(Keys.S)) {
			moveDown = true;
		}
		if (Gdx.input.isKeyPressed(Keys.A)) {
			moveLeft = true;
		}
		if (Gdx.input.isKeyPressed(Keys.D)) {
			moveRight = true;
		}
		if (Gdx.input.isKeyPressed(Keys.SPACE)) {
			jump = true;
		}
		if (Gdx.input.isButtonPressed(Buttons.LEFT)) {
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
	
	private MovingDirection getDirectionFromInputs() {
		if (moveUp) {
			return MovingDirection.UP;
		}
		if (moveDown) {
			return MovingDirection.DOWN;
		}
		if (moveLeft) {
			return MovingDirection.LEFT;
		}
		if (moveRight) {
			return MovingDirection.RIGHT;
		}
		return MovingDirection.NONE;
	}
	
	public void move(float delta) {
		if (!getAction().isMoveBlocking()) {
			float moveSpeedPerDirection = inputCharacter.getMovingSpeed();
			if ((moveUp || moveDown) && (moveLeft || moveRight)) {
				moveSpeedPerDirection = inputCharacter.getMovingSpeed() * (float) Math.sqrt(0.5f);
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
	
	public MovingDirection getDirection() {
		return direction;
	}
}
