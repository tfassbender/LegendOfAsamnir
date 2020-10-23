package net.jfabricationgames.gdx.object;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapProperties;

import net.jfabricationgames.gdx.animation.AnimationDirector;
import net.jfabricationgames.gdx.animation.AnimationSpriteConfig;
import net.jfabricationgames.gdx.attack.AttackType;

public class DestroyableObject extends GameObject {
	
	protected float health;
	protected boolean destroyed;
	
	protected String destroySound;
	
	public DestroyableObject(ObjectTypeConfig type, Sprite sprite, MapProperties properties) {
		super(type, sprite, properties);
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
	
	public void draw(float delta, SpriteBatch batch) {
		super.draw(delta, batch);
		
		//remove this object, if it is destroyed and the destroy animation has finished
		if (destroyed && (animation == null || animation.isAnimationFinished())) {
			remove();
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
		
		AnimationDirector<TextureRegion> animation = animationManager.getAnimationDirector(typeConfig.animationBreak);
		animation.setSpriteConfig(AnimationSpriteConfig.fromSprite(sprite));
		return animation;
	}
	
	private void playDestroySound() {
		if (destroySound != null) {
			soundSet.playSound(destroySound);
		}
	}
}
