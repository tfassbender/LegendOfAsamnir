package net.jfabricationgames.gdx.object.destroyable;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapProperties;

import net.jfabricationgames.gdx.animation.AnimationDirector;
import net.jfabricationgames.gdx.animation.AnimationSpriteConfig;
import net.jfabricationgames.gdx.attack.hit.AttackType;
import net.jfabricationgames.gdx.object.GameObject;
import net.jfabricationgames.gdx.object.GameObjectMap;
import net.jfabricationgames.gdx.object.GameObjectTypeConfig;

public class DestroyableObject extends GameObject {
	
	protected float health;
	protected boolean destroyed;
	
	protected String destroySound;
	
	public DestroyableObject(GameObjectTypeConfig type, Sprite sprite, MapProperties properties, GameObjectMap gameMap) {
		super(type, sprite, properties, gameMap);
		destroyed = false;
	}
	
	@Override
	protected void readTypeConfig() {
		super.readTypeConfig();
		
		destroySound = typeConfig.destroySound;
		health = typeConfig.health;
	}
	
	@Override
	public void takeDamage(float damage, AttackType attackType) {
		if (attackType.isSubTypeOf(typeConfig.requiredAttackType)) {
			health -= damage;
			
			if (health <= 0) {
				destroy();
				return;
			}
		}
		
		animation = getHitAnimation();
		playHitSound();
	}
	
	@Override
	public void draw(float delta, SpriteBatch batch) {
		super.draw(delta, batch);
		
		//remove this object, if it is destroyed and the destroy animation has finished
		if (destroyed && (animation == null || animation.isAnimationFinished())) {
			removeFromMap();
		}
	}
	
	public void destroy() {
		destroyed = true;
		animation = getDestroyAnimation();
		playDestroySound();
		dropItems();
		removePhysicsBody();
	}
	
	protected AnimationDirector<TextureRegion> getDestroyAnimation() {
		if (typeConfig.animationBreak == null) {
			return null;
		}
		
		AnimationDirector<TextureRegion> animation = animationManager.getTextureAnimationDirector(typeConfig.animationBreak);
		animation.setSpriteConfig(AnimationSpriteConfig.fromSprite(sprite));
		return animation;
	}
	
	private void playDestroySound() {
		if (destroySound != null) {
			soundSet.playSound(destroySound);
		}
	}
}
