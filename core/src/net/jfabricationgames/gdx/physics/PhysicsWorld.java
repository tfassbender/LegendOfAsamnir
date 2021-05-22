package net.jfabricationgames.gdx.physics;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;

import net.jfabricationgames.gdx.util.GameUtils;

/**
 * A singleton that keeps track of the current world instance.
 */
public class PhysicsWorld implements ContactListener {
	
	public static final int VELOCITY_ITERATIONS = 6;
	public static final int POSITION_ITERATIONS = 2;
	
	private static PhysicsWorld instance;
	
	private static final Vector2 WORLD_GRAVITY = new Vector2(0f, 0f);
	private static final boolean WORLD_SLEEP = true;
	
	public static synchronized PhysicsWorld getInstance() {
		if (instance == null) {
			instance = new PhysicsWorld();
		}
		return instance;
	}
	
	private World world;
	private Box2DDebugRenderer debugRenderer;
	
	private Array<ContactListener> contactListeners;
	private Array<Body> bodiesToRemove;
	private ArrayMap<Body, Array<Fixture>> fixturesToRemove;
	private Array<Runnable> runAfterWorldStep;
	
	private PhysicsWorld() {
		bodiesToRemove = new Array<>();
		fixturesToRemove = new ArrayMap<>();
	}
	
	public void createWorld() {
		disposeWorld();
		world = new World(WORLD_GRAVITY, WORLD_SLEEP);
		world.setContactListener(this);
		debugRenderer = new Box2DDebugRenderer(true, /* bodies */
				false, /* joints */
				false, /* aabbs */
				true, /* inactive bodies */
				true, /* velocities */
				false /* contacts */);
		
		contactListeners = new Array<>();
		runAfterWorldStep = new Array<>();
	}
	
	public void removeBodiesFromWorld() {
		Array<Body> bodies = new Array<Body>();
		world.getBodies(bodies);
		for (Body body : bodies) {
			world.destroyBody(body);
		}
	}
	
	public void disposeWorld() {
		if (world != null) {
			world.dispose();
		}
	}
	
	protected World getWorld() {
		return world;
	}
	
	public void registerContactListener(ContactListener contactListener) {
		this.contactListeners.add(contactListener);
	}
	public void removeContactListener(ContactListener contactListener) {
		this.contactListeners.removeValue(contactListener, true);
	}
	
	public void step(float timeStep) {
		world.step(timeStep, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
		afterWorldStep();
	}
	
	private void afterWorldStep() {
		removeBodiesAndFixtures();
		executeRunnables();
	}
	
	public void renderDebugGraphics(Matrix4 combinedCameraMatrix) {
		debugRenderer.render(world, combinedCameraMatrix);
	}
	
	public boolean isInWorldStepExecution() {
		return world.isLocked();
	}
	
	/**
	 * Mark a fixture to be deleted after the world step is over.
	 */
	public void removeFixture(Fixture fixture, Body body) {
		Array<Fixture> fixtures = fixturesToRemove.get(body);
		if (fixtures == null) {
			fixtures = new Array<Fixture>();
			fixturesToRemove.put(body, fixtures);
		}
		fixtures.add(fixture);
	}
	
	/**
	 * Destroy all bodies that are marked to be deleted after the world step is over.
	 */
	private void removeBodiesAndFixtures() {
		for (Body body : fixturesToRemove.keys()) {
			Array<Fixture> bodiesFixtures = body.getFixtureList();
			//iterate over all fixtures of the body and check if they are to be deleted because it just wont work any other way
			for (Fixture fixture : bodiesFixtures) {
				if (fixturesToRemove.get(body).contains(fixture, true)) {
					body.destroyFixture(fixture);
				}
			}
		}
		for (Body body : bodiesToRemove) {
			body.setUserData(null);
			world.destroyBody(body);
		}
		
		bodiesToRemove.clear();
		fixturesToRemove.clear();
	}
	
	public void runAfterWorldStep(Runnable runnable) {
		runAfterWorldStep.add(runnable);
	}
	
	public void runDelayedAfterWorldStep(Runnable runnable, float delayTime) {
		GameUtils.runDelayed(() -> runAfterWorldStep(runnable), delayTime);
	}
	
	private void executeRunnables() {
		for (Runnable runnable : runAfterWorldStep) {
			runnable.run();
		}
		runAfterWorldStep.clear();
	}
	
	public void rayCast(RayCastCallback rayCastCallback, Vector2 startPoint, Vector2 endPoint) {
		world.rayCast(rayCastCallback, startPoint, endPoint);
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
	
	/**
	 * If the world is within a world step execution, mark the body to be removed after the world step. Otherwise the body is removed directly.
	 */
	public void removeBodyWhenPossible(Body body) {
		if (body != null) {
			if (isInWorldStepExecution()) {
				bodiesToRemove.add(body);
			}
			else {
				body.setUserData(null);
				world.destroyBody(body);
			}
		}
	}
}
