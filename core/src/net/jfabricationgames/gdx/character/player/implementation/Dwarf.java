package net.jfabricationgames.gdx.character.player.implementation;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.utils.Disposable;

import net.jfabricationgames.gdx.Game;
import net.jfabricationgames.gdx.attack.AttackCreator;
import net.jfabricationgames.gdx.attack.AttackType;
import net.jfabricationgames.gdx.attack.Hittable;
import net.jfabricationgames.gdx.character.player.PlayableCharacter;
import net.jfabricationgames.gdx.data.GameDataService;
import net.jfabricationgames.gdx.data.handler.CharacterItemDataHandler;
import net.jfabricationgames.gdx.data.handler.CharacterPropertiesDataHandler;
import net.jfabricationgames.gdx.data.handler.FastTravelDataHandler;
import net.jfabricationgames.gdx.data.handler.GlobalValuesDataHandler;
import net.jfabricationgames.gdx.data.properties.FastTravelPointProperties;
import net.jfabricationgames.gdx.data.state.BeforePersistState;
import net.jfabricationgames.gdx.event.EventConfig;
import net.jfabricationgames.gdx.event.EventHandler;
import net.jfabricationgames.gdx.event.EventListener;
import net.jfabricationgames.gdx.event.EventType;
import net.jfabricationgames.gdx.item.Item;
import net.jfabricationgames.gdx.item.ItemAmmoType;
import net.jfabricationgames.gdx.item.rune.RuneType;
import net.jfabricationgames.gdx.map.GameMap;
import net.jfabricationgames.gdx.object.event.EventObject;
import net.jfabricationgames.gdx.physics.BeforeWorldStep;
import net.jfabricationgames.gdx.physics.PhysicsCollisionType;
import net.jfabricationgames.gdx.physics.PhysicsWorld;
import net.jfabricationgames.gdx.util.GameUtil;

public class Dwarf implements PlayableCharacter, Disposable, ContactListener, Hittable, EventListener {
	
	private static final float MOVING_SPEED_CUTSCENE = 3.5f;
	
	private static final float TIME_TILL_GAME_OVER_MENU = 3f;
	
	private static final String ATTACK_CONFIG_FILE_NAME = "config/dwarf/attacks.json";
	
	private static final String SOUND_AMMO_EMPTY = "ammo_empty";
	private static final String ATTACK_NAME_WAIT = "wait";
	private static final String RUNE_HAGALAZ_ANIMATION_NAME = "rune_hagalaz";
	
	protected AttackCreator attackCreator;
	
	protected CharacterAction action;
	protected SpecialAction activeSpecialAction;
	
	protected CharacterInputProcessor movementHandler;
	
	protected CharacterPropertiesDataHandler propertiesDataHandler;
	protected CharacterItemDataHandler itemDataHandler;
	protected FastTravelDataHandler fastTravelDataHandler;
	
	protected CharacterBodyHandler bodyHandler;
	protected CharacterRenderer renderer;
	protected CharacterSoundHandler soundHandler;
	
	public Dwarf() {
		propertiesDataHandler = CharacterPropertiesDataHandler.getInstance();
		itemDataHandler = CharacterItemDataHandler.getInstance();
		fastTravelDataHandler = FastTravelDataHandler.getInstance();
		
		action = CharacterAction.NONE;
		activeSpecialAction = SpecialAction.JUMP;
		renderer = new CharacterRenderer(this);
		bodyHandler = new CharacterBodyHandler(this);
		soundHandler = new CharacterSoundHandler();
		
		PhysicsWorld.getInstance().registerContactListener(this);
		
		attackCreator = new AttackCreator(ATTACK_CONFIG_FILE_NAME, bodyHandler.body, PhysicsCollisionType.PLAYER_ATTACK);
		movementHandler = new CharacterInputProcessor(this);
		
		EventHandler.getInstance().registerEventListener(this);
	}
	
	@Override
	public void reAddToWorld() {
		bodyHandler.createPhysicsBody();
		attackCreator = new AttackCreator(ATTACK_CONFIG_FILE_NAME, bodyHandler.body, PhysicsCollisionType.PLAYER_ATTACK);
	}
	
	protected boolean changeAction(CharacterAction action) {
		if (isAlive() || action == CharacterAction.DIE) {
			if (!propertiesDataHandler.hasEnoughEndurance(action)) {
				return false;
			}
			if ((action == CharacterAction.BLOCK || action == CharacterAction.SHIELD_HIT) && !propertiesDataHandler.hasBlock()) {
				return false;
			}
			
			this.action = action;
			renderer.changeAnimation();
			
			propertiesDataHandler.reduceEnduranceForAction(action);
			
			soundHandler.playSound(action);
			
			if (action.isAttack()) {
				attackCreator.startAttack(action.getAttack(), movementHandler.getMovingDirection().getNormalizedDirectionVector());
			}
			
			return true;
		}
		return false;
	}
	
	protected boolean executeSpecialAction() {
		if (activeSpecialAction != null) {
			switch (activeSpecialAction) {
				case BOW:
				case BOMB:
					ItemAmmoType ammoType = ItemAmmoType.fromSpecialAction(activeSpecialAction);
					if (attackCreator.allAttacksExecuted()) {
						if (itemDataHandler.hasAmmo(ammoType)) {
							itemDataHandler.decreaseAmmo(ammoType);
							attackCreator.startAttack(ammoType.name().toLowerCase(),
									movementHandler.getMovingDirection().getNormalizedDirectionVector());
							
							if (!itemDataHandler.hasAmmo(ammoType)) {
								fireOutOfAmmoEvent(ammoType);
							}
						}
						else {
							delayAttacks();
							soundHandler.playSound(SOUND_AMMO_EMPTY);
							return false;
						}
						
						return true;
					}
					break;
				case BOOMERANG:
				case WAND:
					if (propertiesDataHandler.hasEnoughMana(activeSpecialAction.manaCost) && attackCreator.allAttacksExecuted()) {
						propertiesDataHandler.reduceMana(activeSpecialAction.manaCost);
						attackCreator.startAttack(activeSpecialAction.name().toLowerCase(),
								movementHandler.getMovingDirection().getNormalizedDirectionVector());
					}
					break;
				case LANTERN:
					if (propertiesDataHandler.hasEnoughMana(activeSpecialAction.manaCost) && attackCreator.allAttacksExecuted()) {
						propertiesDataHandler.reduceMana(activeSpecialAction.manaCost);
						renderer.startDarknessFade();
						
						//handle the lantern as attack to have a duration (so it's not executed in every game step)
						delayAttacks();
					}
					break;
				case JUMP:
					return changeAction(CharacterAction.JUMP);
				case FEATHER:
					//do nothing here - the action will be executed in InteractiveAction.SHOW_OR_CHANGE_TEXT
					break;
				default:
					throw new IllegalStateException("Unexpected SpecialAction: " + activeSpecialAction);
			}
		}
		
		return false;
	}
	
	private void fireOutOfAmmoEvent(ItemAmmoType ammoType) {
		EventConfig eventConfig = new EventConfig().setEventType(EventType.OUT_OF_AMMO).setStringValue(ammoType.name());
		EventHandler.getInstance().fireEvent(eventConfig);
	}
	
	private void delayAttacks() {
		attackCreator.startAttack(ATTACK_NAME_WAIT, movementHandler.getMovingDirection().getNormalizedDirectionVector());
	}
	
	protected boolean isAnimationFinished() {
		return renderer.animation.isAnimationFinished();
	}
	
	protected boolean isBlocking() {
		return action == CharacterAction.BLOCK || action == CharacterAction.SHIELD_HIT;
	}
	
	@BeforeWorldStep
	public void resetGroundProperties() {
		bodyHandler.groundProperties = GameMap.DEFAULT_GROUND_PROPERTIES;
	}
	
	@BeforePersistState
	public void updatePositionToDataContainer() {
		propertiesDataHandler.setPlayerPosition(getPosition());
	}
	
	@Override
	public Vector2 getPosition() {
		return bodyHandler.body.getPosition().cpy();
	}
	
	@Override
	public void setPosition(float x, float y) {
		bodyHandler.body.setTransform(x, y, 0);
		propertiesDataHandler.setRespawnPoint(new Vector2(x, y));
	}
	
	@Override
	public int getAmmo(ItemAmmoType ammoType) {
		return itemDataHandler.getAmmo(ammoType);
	}
	
	@Override
	public void process(float delta) {
		updateAction(delta);
		propertiesDataHandler.updateStats(delta, action);
		attackCreator.handleAttacks(delta);
		
		movementHandler.handleInputs(delta);
		movementHandler.move(delta);
		
		renderer.processDarknessFadingAnimation(delta);
	}
	
	private void updateAction(float delta) {
		renderer.animation.increaseStateTime(delta);
		if (renderer.animation.isAnimationFinished()) {
			changeAction(CharacterAction.NONE);
		}
	}
	
	@Override
	public void render(float delta, SpriteBatch batch) {
		renderer.drawDwarf(batch);
		renderer.drawAimMarker(batch);
		attackCreator.renderAttacks(delta, batch);
	}
	
	@Override
	public void renderDarkness(SpriteBatch batch, ShapeRenderer shapeRenderer) {
		renderer.renderDarkness(batch, shapeRenderer);
	}
	
	@Override
	public void moveTo(Vector2 position, float speedFactor) {
		Vector2 direction = position.cpy().sub(getPosition());
		direction.nor().scl(MOVING_SPEED_CUTSCENE * speedFactor);
		
		move(direction.x, direction.y);
	}
	
	protected void move(float deltaX, float deltaY) {
		bodyHandler.move(deltaX, deltaY);
	}
	
	@Override
	public void changeToMovingState() {
		if (action != CharacterAction.RUN) {
			changeAction(CharacterAction.RUN);
		}
	}
	
	@Override
	public void changeToIdleState() {
		if (action != CharacterAction.IDLE) {
			changeAction(CharacterAction.IDLE);
		}
	}
	
	@Override
	public float getHealth() {
		return propertiesDataHandler.getHealthPercentual();
	}
	
	@Override
	public boolean isAlive() {
		return propertiesDataHandler.isAlive();
	}
	
	@Override
	public float getMana() {
		return propertiesDataHandler.getManaPercentual();
	}
	
	@Override
	public float getEndurance() {
		return propertiesDataHandler.getEndurancePercentual();
	}
	
	@Override
	public float getArmor() {
		return propertiesDataHandler.getArmorPercentual();
	}
	
	@Override
	public int getCoins() {
		return propertiesDataHandler.getCoins();
	}
	
	@Override
	public int getCoinsForHud() {
		return propertiesDataHandler.getCoinsForHud();
	}
	
	@Override
	public int getNormalKeys() {
		return itemDataHandler.getNumNormalKeys();
	}
	
	@Override
	public SpecialAction getActiveSpecialAction() {
		return activeSpecialAction;
	}
	
	@Override
	public void setActiveSpecialAction(SpecialAction specialAction) {
		activeSpecialAction = specialAction;
	}
	
	@Override
	public void beginContact(Contact contact) {
		bodyHandler.beginContact(contact);
	}
	
	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		bodyHandler.preSolve(contact);
	}
	
	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {}
	
	@Override
	public void endContact(Contact contact) {}
	
	@Override
	public void takeDamage(float damage, AttackType attackType) {
		if (isAlive()) {
			if (isBlocking() && attackType.canBeBlocked()) {
				takeArmorDamage(damage * 0.33f);
				damage *= 0.1f;
			}
			propertiesDataHandler.takeDamage(damage);
			if (!propertiesDataHandler.isAlive()) {
				die();
			}
			else {
				if (isBlocking() && attackType.canBeBlocked()) {
					changeAction(CharacterAction.SHIELD_HIT);
				}
				else {
					changeAction(CharacterAction.HIT);
				}
			}
		}
	}
	
	private void takeArmorDamage(float damage) {
		propertiesDataHandler.takeArmorDamage(damage);
	}
	
	private void die() {
		if (runeCollectedAndForged()) {
			propertiesDataHandler.increaseHealthByHalf();
			GlobalValuesDataHandler.getInstance().put(RuneType.GLOBAL_VALUE_KEY_RUNE_HAGALAZ_FORGED, false);
			EventHandler.getInstance().fireEvent(new EventConfig().setEventType(EventType.RUNE_USED).setStringValue(RUNE_HAGALAZ_ANIMATION_NAME));
		}
		else {
			soundHandler.playSound(CharacterAction.HIT);
			changeAction(CharacterAction.DIE);
			GameUtil.runDelayed(() -> Game.getInstance().setGameOver(true), TIME_TILL_GAME_OVER_MENU);
		}
	}
	
	private boolean runeCollectedAndForged() {
		GlobalValuesDataHandler globalValues = GlobalValuesDataHandler.getInstance();
		return globalValues.getAsBoolean(RuneType.HAGALAZ.globalValueKeyCollected) //
				&& globalValues.getAsBoolean(RuneType.GLOBAL_VALUE_KEY_RUNE_HAGALAZ_FORGED);
	}
	
	@Override
	public void removeFromMap() {
		PhysicsWorld.getInstance().removeBodyWhenPossible(bodyHandler.body);
	}
	
	@Override
	public void pushByHit(Vector2 hitCenter, float force, boolean blockAffected) {
		bodyHandler.pushByHit(hitCenter, force, blockAffected);
	}
	
	@Override
	public void handleEvent(EventConfig event) {
		if (event.eventType == EventType.EVENT_OBJECT_TOUCHED && event.stringValue.equals(EventObject.EVENT_KEY_RESPAWN_CHECKPOINT)) {
			if (event.parameterObject != null && event.parameterObject instanceof EventObject) {
				EventObject respawnObject = (EventObject) event.parameterObject;
				propertiesDataHandler.setRespawnPoint(respawnObject.getEventObjectCenterPosition());
			}
			GameDataService.fireQuickSaveEvent();
		}
		if (event.eventType == EventType.TAKE_PLAYERS_COINS) {
			propertiesDataHandler.reduceCoins(event.intValue);
		}
		if (event.eventType == EventType.PLAYER_BUY_ITEM) {
			itemDataHandler.collectItem((Item) event.parameterObject, this);
		}
		if (event.eventType == EventType.FAST_TRAVEL_TO_MAP_POSITION) {
			FastTravelPointProperties fastTravelTargetPoint = fastTravelDataHandler.getFastTravelPropertiesById(event.stringValue);
			if (fastTravelTargetPoint.enabled) {
				setPosition(fastTravelTargetPoint.positionOnMapX, fastTravelTargetPoint.positionOnMapY);
			}
		}
		if (event.eventType == EventType.SET_ITEM) {
			String itemId = event.stringValue;
			itemDataHandler.addSpecialItem(itemId);
		}
	}
	
	@Override
	public void respawn() {
		Vector2 respawnPoint = propertiesDataHandler.getRespawnPoint();
		setPosition(respawnPoint.x, respawnPoint.y);
		propertiesDataHandler.changeStatsAfterRespawn();
		Game.getInstance().setGameOver(false);
		
		EventHandler.getInstance().fireEvent(new EventConfig().setEventType(EventType.PLAYER_RESPAWNED));
	}
	
	@Override
	public void dispose() {
		soundHandler.dispose();
		PhysicsWorld.getInstance().removeContactListener(this);
		EventHandler.getInstance().removeEventListener(this);
	}
}
