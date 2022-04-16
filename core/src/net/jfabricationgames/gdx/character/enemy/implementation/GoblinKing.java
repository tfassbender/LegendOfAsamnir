package net.jfabricationgames.gdx.character.enemy.implementation;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.utils.ArrayMap;

import net.jfabricationgames.gdx.attack.hit.AttackType;
import net.jfabricationgames.gdx.character.ai.ArtificialIntelligence;
import net.jfabricationgames.gdx.character.ai.BaseAI;
import net.jfabricationgames.gdx.character.ai.implementation.BackToStartingPointMovementAI;
import net.jfabricationgames.gdx.character.ai.util.timer.RandomIntervalAttackTimer;
import net.jfabricationgames.gdx.character.enemy.Enemy;
import net.jfabricationgames.gdx.character.enemy.EnemyTypeConfig;
import net.jfabricationgames.gdx.character.enemy.ai.GoblinKingAttackAI;
import net.jfabricationgames.gdx.character.enemy.ai.GoblinKingEnduranceAI;
import net.jfabricationgames.gdx.character.enemy.statsbar.EnemyEnduranceBarRenderer;
import net.jfabricationgames.gdx.character.state.CharacterState;
import net.jfabricationgames.gdx.state.GameStateManager;

public class GoblinKing extends Enemy {
	
	public static final String STATE_NAME_IDLE = "idle";
	public static final String STATE_NAME_MOVE = "move";
	public static final String STATE_NAME_JUMP = "jump";
	public static final String STATE_NAME_COIN_BAG = "coin_bag";
	public static final String STATE_NAME_ATTACK_THROW = "throw";
	public static final String STATE_NAME_COMMAND = "command";
	public static final String STATE_NAME_EAT = "eat";
	public static final String STATE_NAME_PANIC = "panic";
	
	private static final float ENDURANCE_BAR_OFFSET_Y = -0.04f;
	private static final float ENCURANCE_CHANGE_PER_SECOND = 20f;
	private static final float MAX_ENDURANCE = 100f;
	
	private static final float HEALTH_CHANGE_PER_SECOND = 20f;
	private static final float HEALING_FACTOR_AFTER_EATING = 0.2f;
	
	private EnemyEnduranceBarRenderer enduranceBarRenderer = new EnemyEnduranceBarRenderer();
	private float endurance = MAX_ENDURANCE;
	private float enduranceDecrease = 0f;
	private float enduranceIncrease = 0f;
	private float healthIncrease = 0f;
	
	private boolean eatingCompleted;
	private boolean startedEating;
	
	public GoblinKing(EnemyTypeConfig typeConfig, MapProperties properties) {
		super(typeConfig, properties);
	}
	
	@Override
	protected void createAI() {
		ai = new BaseAI();
		ai = createMovementAI(ai);
		ai = createFightAI(ai);
		ai = createEnduranceAI(ai);
	}
	
	private ArtificialIntelligence createMovementAI(ArtificialIntelligence ai) {
		CharacterState idleState = stateMachine.getState(STATE_NAME_IDLE);
		CharacterState moveState = stateMachine.getState(STATE_NAME_MOVE);
		
		return new BackToStartingPointMovementAI(ai, moveState, idleState, 0f);
	}
	
	private ArtificialIntelligence createFightAI(ArtificialIntelligence ai) {
		CharacterState attackSlamState = stateMachine.getState(GoblinKing.STATE_NAME_JUMP); // use jump, because the following state is slam
		CharacterState attackCoinBagState = stateMachine.getState(GoblinKing.STATE_NAME_COIN_BAG);
		CharacterState attackThrowState = stateMachine.getState(GoblinKing.STATE_NAME_ATTACK_THROW);
		
		ArrayMap<String, CharacterState> attackStates = new ArrayMap<>();
		attackStates.put(GoblinKing.STATE_NAME_JUMP, attackSlamState);
		attackStates.put(GoblinKing.STATE_NAME_COIN_BAG, attackCoinBagState);
		attackStates.put(GoblinKing.STATE_NAME_ATTACK_THROW, attackThrowState);
		
		ArrayMap<CharacterState, Float> attackDistances = new ArrayMap<>();
		attackDistances.put(attackSlamState, 4f);
		attackDistances.put(attackCoinBagState, 15f);
		
		float minTimeBetweenAttacks = 0.5f;
		float maxTimeBetweenAttacks = 2.5f;
		
		GoblinKingAttackAI attackAI = new GoblinKingAttackAI(ai, attackStates, attackDistances,
				new RandomIntervalAttackTimer(minTimeBetweenAttacks, maxTimeBetweenAttacks));
		attackAI.setEnduranceConsumer(change -> enduranceDecrease = change);
		attackAI.setAttackHandler(attackHandler);
		attackAI.setMoveToPlayerWhenAttacking(false);
		
		return attackAI;
	}
	
	private ArtificialIntelligence createEnduranceAI(ArtificialIntelligence ai) {
		CharacterState commandState = stateMachine.getState(GoblinKing.STATE_NAME_COMMAND);
		
		return new GoblinKingEnduranceAI(ai, commandState, () -> endurance <= 0f, () -> endurance >= MAX_ENDURANCE, () -> health / typeConfig.health,
				this::startEnduranceRecharge);
	}
	
	private void startEnduranceRecharge() {
		enduranceIncrease = MAX_ENDURANCE;
		startedEating = false;
		endurance = 0.1f; // add some endurance to not get back into the eating state directly after leaving it
		if (eatingCompleted) {
			healthIncrease = typeConfig.health * HEALING_FACTOR_AFTER_EATING;
		}
	}
	
	@Override
	public void act(float delta) {
		super.act(delta);
		
		if (enduranceDecrease > 0f) {
			float decreaseStep = Math.min(delta * ENCURANCE_CHANGE_PER_SECOND, enduranceDecrease);
			enduranceDecrease -= decreaseStep;
			endurance = Math.max(endurance - decreaseStep, 0);
			if (endurance <= 0 && enduranceDecrease > 0) {
				enduranceDecrease = 0;
			}
		}
		
		if (enduranceIncrease > 0f) {
			float increaseStep = Math.min(delta * ENCURANCE_CHANGE_PER_SECOND, enduranceIncrease);
			enduranceIncrease -= increaseStep;
			endurance = Math.min(endurance + increaseStep, MAX_ENDURANCE);
			if (endurance >= MAX_ENDURANCE) {
				endurance = MAX_ENDURANCE;
				if (enduranceIncrease > 0) {
					enduranceIncrease = 0;
				}
			}
		}
		
		if (healthIncrease > 0f) {
			float increaseStep = Math.min(delta * HEALTH_CHANGE_PER_SECOND, healthIncrease);
			healthIncrease -= increaseStep;
			health = Math.min(health + increaseStep, typeConfig.health);
			if (health >= typeConfig.health) {
				health = typeConfig.health;
				if (healthIncrease > 0) {
					healthIncrease = 0;
				}
			}
		}
		
		if (endurance <= 0 && !startedEating) {
			stateMachine.setState(STATE_NAME_EAT);
			eatingCompleted = true; // will be set to false if eating is interrupted
			startedEating = true;
		}
	}
	
	@Override
	public void takeDamage(float damage, AttackType attackType) {
		if (attackType == AttackType.ARROW) {
			if (stateMachine.isInState(STATE_NAME_EAT)) {
				stateMachine.setState(STATE_NAME_PANIC);
				eatingCompleted = false;
			}
			else {
				// only take damage from arrows while eating
				return;
			}
		}
		if (attackType == AttackType.BOMB) {
			//don't take damage from bombs
			return;
		}
		
		super.takeDamage(damage, attackType);
	}
	
	@Override
	protected void drawStatsBar(ShapeRenderer shapeRenderer, float x, float y, float width) {
		super.drawStatsBar(shapeRenderer, x, y, width);
		enduranceBarRenderer.drawStatsBar(shapeRenderer, endurance / MAX_ENDURANCE, x, y + ENDURANCE_BAR_OFFSET_Y, width);
	}
	
	@Override
	protected boolean drawStatsBar() {
		return true;
	}
	
	@Override
	protected void die() {
		super.die();
		GameStateManager.fireQuickSaveEvent();
	}
}
