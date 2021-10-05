package net.jfabricationgames.gdx.map;

import java.util.function.Function;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

import net.jfabricationgames.gdx.character.animal.Animal;
import net.jfabricationgames.gdx.character.enemy.Enemy;
import net.jfabricationgames.gdx.character.npc.NonPlayableCharacter;
import net.jfabricationgames.gdx.event.EventListener;
import net.jfabricationgames.gdx.item.Item;
import net.jfabricationgames.gdx.object.GameObject;
import net.jfabricationgames.gdx.projectile.Projectile;

public interface GameMap extends EventListener, Disposable {
	
	enum GlobalMapPropertyKeys {
		
		MINI_MAP_CONFIG_PATH("mini_map_config_path"), //
		MAP_WIDTH_IN_TILE_DIMENSIONS("width"), //
		MAP_HEIGHT_IN_TILE_DIMENSIONS("height"), //
		MAP_TILE_WIDTH_IN_PIXELS("tilewidth"), // 
		MAP_TILE_HEIGHT_IN_PIXELS("tileheight"), //
		MAP_CONTINUOUSE_DAMAGE("continuous_damage"), //
		MAP_CONTINUOUSE_DAMAGE_INTERVAL("continuous_damage_interval");
		
		private final String key;
		
		private GlobalMapPropertyKeys(String key) {
			this.key = key;
		}
		
		public String getKey() {
			return key;
		}
	}
	
	public String GLOBAL_VALUE_KEY_LANTERN_USED = "game_map__lantern_used";
	public String OBJECT_NAME_ANIMAL = "animal";
	public String OBJECT_NAME_NPC = "npc";
	public String OBJECT_NAME_ENEMY = "enemy";
	public String OBJECT_NAME_OBJECT = "object";
	public String OBJECT_NAME_ITEM = "item";
	public String OBJECT_NAME_PLAYER = "player";
	
	public void beforeLoadMap(String mapIdentifier);
	public void afterLoadMap(String mapIdentifier);
	
	public void executeBeforeWorldStep();
	public void executeAfterWorldStep();
	
	public void processPlayer(float delta);
	public void processAndRender(float delta);
	
	public MapProperties getGlobalMapProperties();
	
	public GameMapGroundType getGroundTypeByName(String groundTypeWeb);
	
	public Object getUnitById(String unitId);
	
	public void updateAfterLoadingGameState();
	
	public boolean isDungeonMap();
	
	public float getMapWidth();
	public float getMapHeight();
	
	public void addObject(GameObject gameObject);
	public void addItem(Item item);
	public void addItemAboveGameObjects(Item item);
	public void addProjectile(Projectile projectile);
	public void addEnemy(Enemy gameObject);
	
	public void addPostAddObjectProcessing(Function<Array<GameObject>, Array<GameObject>> postAddObjectProcessingFunction);
	
	public void removeObject(GameObject gameObject, Body body);
	public void removeItem(Item item, Body body);
	public void removeProjectile(Projectile projectile, Body body);
	public void removeEnemy(Enemy enemy, Body body);
	public void removeNpc(NonPlayableCharacter nonPlayableCharacter, Body body);
	public void removeAnimal(Animal animal, Body body);
	public void removePhysicsObjectsWithType(MapObjectType invisiblePathBlocker);
}
