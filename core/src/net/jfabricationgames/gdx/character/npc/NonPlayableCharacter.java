package net.jfabricationgames.gdx.character.npc;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapProperties;

import net.jfabricationgames.gdx.character.AbstractCharacter;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyProperties;
import net.jfabricationgames.gdx.physics.PhysicsWorld;

public class NonPlayableCharacter extends AbstractCharacter {
	
	public NonPlayableCharacter(MapProperties properties) {
		super(properties);
	}
	
	@Override
	protected PhysicsBodyProperties definePhysicsBodyProperties() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	protected void addAdditionalPhysicsParts() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void act(float delta) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void draw(float delta, SpriteBatch batch) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void removeFromMap() {
		gameMap.removeNpc(this, body);
		PhysicsWorld.getInstance().removeContactListener(this);
		body = null;// set the body to null to avoid strange errors in native Box2D methods
	}
}
