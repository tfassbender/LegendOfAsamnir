package net.jfabricationgames.gdx.projectile;

public interface ProjectileReflector {
	
	/**
	 * May reflect a projectile.
	 * 
	 * @param projectile The projectile that is to be reflected.
	 * @return Return true if the projectile was reflected (so the original should be removed).
	 */
	public boolean reflectProjectile(Projectile projectile);
}
