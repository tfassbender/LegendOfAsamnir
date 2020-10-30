package net.jfabricationgames.gdx.attack;

public enum AttackType {
	
	ATTACK(null), //
	MELEE(ATTACK), //
	HIT(MELEE), //
	SPIN_ATTACK(HIT), //
	NOVA(MELEE), //
	PROJECTILE(ATTACK), //
	ARROW(PROJECTILE), //
	BOMB(PROJECTILE), //
	WEB(PROJECTILE), //
	FIREBALL(PROJECTILE); //
	
	private final AttackType superType;
	
	private AttackType(AttackType superType) {
		this.superType = superType;
	}
	
	public AttackType getSuperType() {
		return superType;
	}
	
	public boolean isSubTypeOf(AttackType type) {
		if (type == this) {
			return true;
		}
		
		AttackType currentType = superType;
		while (currentType != null) {
			if (currentType == type) {
				return true;
			}
			currentType = currentType.superType;
		}
		
		return false;
	}
}