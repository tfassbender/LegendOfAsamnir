package net.jfabricationgames.gdx.character.enemy.ai;

import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;

import net.jfabricationgames.gdx.character.ai.AbstractArtificialIntelligence;
import net.jfabricationgames.gdx.character.ai.ArtificialIntelligence;
import net.jfabricationgames.gdx.character.ai.move.AIAttackingMove;
import net.jfabricationgames.gdx.character.ai.move.MoveType;
import net.jfabricationgames.gdx.character.enemy.implementation.GoblinKing;
import net.jfabricationgames.gdx.character.state.CharacterState;
import net.jfabricationgames.gdx.event.EventConfig;
import net.jfabricationgames.gdx.event.EventHandler;
import net.jfabricationgames.gdx.event.EventType;

public class GoblinKingEnduranceAI extends AbstractArtificialIntelligence {
	
	private static final String SPAWN_EVENT_MINI_GOLEM = "goblin_king_command_mini_golem";
	private static final String SPAWN_EVENT_GLADIATOR = "goblin_king_command_gladiator";
	private static final String SPAWN_EVENT_IMP = "goblin_king_command_imp";
	private static final String SPAWN_EVENT_SPIDER = "goblin_king_command_spider";
	
	private final AIAttackingMove blockingMove = new AIAttackingMove(this);
	
	private CharacterState commandState;
	private BooleanSupplier isExhausted;
	private BooleanSupplier isEnduranceFullyCharged;
	private Runnable onRechargeEndurance;
	private DoubleSupplier percentualHealthSupplier;
	
	private boolean enduranceLoading;
	
	public GoblinKingEnduranceAI(ArtificialIntelligence subAI, CharacterState commandState, BooleanSupplier isExhausted,
			BooleanSupplier isEnduranceFullyCharged, DoubleSupplier percentualHealthSupplier, Runnable onRechargeEndurance) {
		super(subAI);
		this.commandState = commandState;
		this.isExhausted = isExhausted;
		this.isEnduranceFullyCharged = isEnduranceFullyCharged;
		this.percentualHealthSupplier = percentualHealthSupplier;
		this.onRechargeEndurance = onRechargeEndurance;
	}
	
	@Override
	public void calculateMove(float delta) {
		subAI.calculateMove(delta);
		
		if (isExhausted.getAsBoolean()) {
			enduranceLoading = true;
			if (!stateMachine.isInState(GoblinKing.STATE_NAME_EAT)) {
				onRechargeEndurance.run();
			}
		}
		
		if (enduranceLoading) {
			if (isEnduranceFullyCharged.getAsBoolean()) {
				enduranceLoading = false;
				AIAttackingMove commandMove = new AIAttackingMove(this);
				commandMove.attack = commandState;
				setMove(MoveType.ATTACK, commandMove);
			}
			else {
				setMove(MoveType.ATTACK, blockingMove); // block the moves of other AIs
			}
		}
	}
	
	@Override
	public void executeMove(float delta) {
		AIAttackingMove move = getMove(MoveType.ATTACK, AIAttackingMove.class);
		if (move == blockingMove) {
			move.executed();
		}
		else if (isExecutedByMe(move)) {
			stateMachine.setState(commandState);
			
			String eventStringValue = chooseSpawnEvent();
			EventHandler.getInstance().fireEvent(new EventConfig().setEventType(EventType.GOBLIN_KING_COMMAND).setStringValue(eventStringValue));
		}
		
		subAI.executeMove(delta);
	}
	
	private String chooseSpawnEvent() {
		float health = (float) percentualHealthSupplier.getAsDouble();
		
		if (health < 0.25f) {
			return SPAWN_EVENT_SPIDER;
		}
		else if (health < 0.5f) {
			return SPAWN_EVENT_IMP;
		}
		else if (health < 0.75f) {
			return SPAWN_EVENT_GLADIATOR;
		}
		else {
			return SPAWN_EVENT_MINI_GOLEM;
		}
	}
}
