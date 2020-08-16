package net.jfabricationgames.gdx.physics;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

/**
 * A singleton that keeps track of the current world instance.
 */
public class PhysicsWorld implements ContactListener {
	
	private static PhysicsWorld instance;
	
	public static synchronized PhysicsWorld getInstance() {
		if (instance == null) {
			instance = new PhysicsWorld();
		}
		return instance;
	}
	
	private World world;
	private Array<ContactListener> contactListeners;
	private Array<Body> bodiesToRemove;
	
	private PhysicsWorld() {
		bodiesToRemove = new Array<>();
	}
	
	public World createWorld(Vector2 gravity, boolean doSleep) {
		disposeWorld();
		world = new World(gravity, doSleep);
		world.setContactListener(this);
		contactListeners = new Array<>();
		return world;
	}
	
	public World getWorld() {
		return world;
	}
	
	public void disposeWorld() {
		if (world != null) {
			world.dispose();
		}
	}
	
	public void registerContactListener(ContactListener contactListener) {
		this.contactListeners.add(contactListener);
	}
	public void removeContactListener(ContactListener contactListener) {
		this.contactListeners.removeValue(contactListener, true);
	}
	
	/**
	 * Mark a body to be deleted after the world step is over.
	 */
	public void destroyBodyAfterWorldStep(Body body) {
		bodiesToRemove.add(body);
	}
	/**
	 * Destroy all bodies that are marked to be deleted after the world step is over.
	 */
	public void removeDeadBodies() {
		for (Body body : bodiesToRemove) {
			world.destroyBody(body);
		}
		bodiesToRemove.clear();
	}
	
	@Override
	public void beginContact(Contact contact) {
		for (ContactListener listener : contactListeners) {
			listener.beginContact(contact);
		}
	}
	
	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		for (ContactListener listener : contactListeners) {
			listener.preSolve(contact, oldManifold);
		}
	}
	
	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		for (ContactListener listener : contactListeners) {
			listener.postSolve(contact, impulse);
		}
	}
	
	@Override
	public void endContact(Contact contact) {
		for (ContactListener listener : contactListeners) {
			listener.endContact(contact);
		}
	}
}
