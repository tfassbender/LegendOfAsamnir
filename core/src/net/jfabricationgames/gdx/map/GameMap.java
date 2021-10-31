package net.jfabricationgames.gdx.map;

import java.util.function.Function;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

import net.jfabricationgames.gdx.character.animal.AnimalCharacterMap;
import net.jfabricationgames.gdx.character.enemy.EnemyCharacterMap;
import net.jfabricationgames.gdx.character.npc.NpcCharacterMap;
import net.jfabricationgames.gdx.cutscene.action.CutsceneUnitProvider;
import net.jfabricationgames.gdx.event.EventListener;
import net.jfabricationgames.gdx.item.ItemMap;
import net.jfabricationgames.gdx.map.ground.MapObjectType;
import net.jfabricationgames.gdx.object.GameObject;
import net.jfabricationgames.gdx.object.GameObjectMap;
import net.jfabricationgames.gdx.projectile.ProjectileMap;

public interface GameMap extends EventListener, CutsceneUnitProvider, EnemyCharacterMap, NpcCharacterMap, AnimalCharacterMap, GameObjectMap, ItemMap,
		ProjectileMap, Disposable {
	
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
	
	public void beforeLoadMap(String mapIdentifier);
	public void afterLoadMap(String mapIdentifier, int playerStartingPointId);
	
	public void executeBeforeWorldStep();
	public void executeAfterWorldStep();
	
	public void processPlayer(float delta);
	public void processAndRender(float delta);
	
	public MapProperties getGlobalMapProperties();
	
	public void updateAfterLoadingGameState();
	
	public boolean isDungeonMap();
	
	public float getMapWidth();
	public float getMapHeight();
	
	public void addPostAddObjectProcessing(Function<Array<GameObject>, Array<GameObject>> postAddObjectProcessingFunction);
	
	public void removePhysicsObjectsWithType(MapObjectType invisiblePathBlocker);
}
