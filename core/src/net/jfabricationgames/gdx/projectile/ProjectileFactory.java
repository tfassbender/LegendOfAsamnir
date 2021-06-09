package net.jfabricationgames.gdx.projectile;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ObjectMap;

import net.jfabricationgames.gdx.animation.AnimationDirector;
import net.jfabricationgames.gdx.animation.AnimationManager;
import net.jfabricationgames.gdx.assets.AssetGroupManager;
import net.jfabricationgames.gdx.factory.AbstractFactory;
import net.jfabricationgames.gdx.map.GameMap;
import net.jfabricationgames.gdx.physics.PhysicsCollisionType;

public class ProjectileFactory extends AbstractFactory {
	
	private static final String PROJECTILE_TYPE_ARROW = "arrow";
	private static final String PROJECTILE_TYPE_BOMB = "bomb";
	private static final String PROJECTILE_TYPE_EXPLOSION = "explosion";
	private static final String PROJECTILE_TYPE_WEB = "web";
	private static final String PROJECTILE_TYPE_IMP_FIRE = "imp_fire";
	private static final String PROJECTILE_TYPE_ROCK = "rock";
	private static final String PROJECTILE_TYPE_BOOMERANG = "boomerang";
	private static final String configFile = "config/factory/projectile_factory.json";
	private static final String animationConfigFile = "config/animation/projectiles.json";
	private static Config config;
	
	private static ProjectileFactory instance;
	
	public static synchronized ProjectileFactory getInstance() {
		if (instance == null) {
			throw new IllegalStateException("The instance of ProjectileFactory has not yet been created. "
					+ "Use the createInstance(GameMap) method to create the instance.");
		}
		return instance;
	}
	
	public static synchronized ProjectileFactory createInstance() {
		if (instance != null) {
			Gdx.app.error(ProjectileFactory.class.getSimpleName(), "A ProjectileFactory for this game map has already been created.");
		}
		
		AnimationManager.getInstance().loadAnimations(animationConfigFile);
		instance = new ProjectileFactory();
		return instance;
	}
	
	private ObjectMap<String, ProjectileTypeConfig> typeConfigs;
	
	private ProjectileFactory() {
		if (config == null) {
			config = loadConfig(Config.class, configFile);
		}
		
		loadTypeConfigs();
		
		AssetGroupManager assetManager = AssetGroupManager.getInstance();
		atlas = assetManager.get(config.atlas);
	}
	
	@SuppressWarnings("unchecked")
	private void loadTypeConfigs() {
		typeConfigs = json.fromJson(ObjectMap.class, ProjectileTypeConfig.class, Gdx.files.internal(config.typesConfig));
	}
	
	public Projectile createProjectileAndAddToMap(String type, Vector2 position, Vector2 direction, PhysicsCollisionType collisionType) {
		if (type == null) {
			throw new IllegalStateException(
					"The 'type' parameter mussn't be null. Maybe the projectileType was not configured in the attack config file?");
		}
		ProjectileTypeConfig typeConfig = typeConfigs.get(type);
		if (typeConfig == null) {
			throw new IllegalStateException("No type config known for type: " + type
					+ ". Either the type name is wrong or you have to add it to the projectileTypesConfig (see \"" + configFile + "\")");
		}
		
		Sprite sprite = null;
		if (typeConfig.texture != null) {
			sprite = createSprite(position.x, position.y, typeConfig.texture);
		}
		AnimationDirector<TextureRegion> animation = null;
		if (typeConfig.animation != null) {
			animation = AnimationManager.getInstance().getAnimationDirectorCopy(typeConfig.animation);
		}
		
		Projectile projectile;
		switch (type) {
			case PROJECTILE_TYPE_ARROW:
				projectile = new Arrow(typeConfig, sprite);
				break;
			case PROJECTILE_TYPE_BOMB:
				projectile = new Bomb(typeConfig, sprite);
				break;
			case PROJECTILE_TYPE_EXPLOSION:
				projectile = new Explosion(typeConfig, animation);
				collisionType = PhysicsCollisionType.EXPLOSION;
				break;
			case PROJECTILE_TYPE_WEB:
				projectile = new Web(typeConfig, animation);
				break;
			case PROJECTILE_TYPE_IMP_FIRE:
				projectile = new ImpFireball(typeConfig, animation);
				break;
			case PROJECTILE_TYPE_ROCK:
				projectile = new Rock(typeConfig, sprite);
				break;
			case PROJECTILE_TYPE_BOOMERANG:
				projectile = new Boomerang(typeConfig, sprite);
				break;
			default:
				throw new IllegalStateException("Unknown object type: " + type);
		}
		projectile.createPhysicsBody(position, collisionType);
		projectile.startProjectile(direction);
		
		GameMap.getInstance().addProjectile(projectile);
		
		return projectile;
	}
	
	public static class Config {
		
		public String atlas;
		public String typesConfig;
	}
}
