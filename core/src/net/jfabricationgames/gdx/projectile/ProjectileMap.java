package net.jfabricationgames.gdx.projectile;

import com.badlogic.gdx.physics.box2d.Body;

import net.jfabricationgames.gdx.map.GameMapGroundType;

public interface ProjectileMap {
	
	public void addProjectile(Projectile projectile);
	public void removeProjectile(Projectile projectile, Body body);
	public GameMapGroundType getGroundTypeByName(String groundType);
}
