package net.jfabricationgames.gdx.input;

import com.badlogic.gdx.Gdx;

import net.jfabricationgames.gdx.TestApplicationListener;

public class InputProfileTest implements InputActionListener, Runnable {
	
	private InputContext context;
	
	public InputProfileTest() {
		InputProfile inputProfile = new InputProfile(Gdx.files.internal("config/input/demo_profile.xml"),
				TestApplicationListener.getInstance().inputMultiplexer);
		inputProfile.setContext("controller");
		context = inputProfile.getContext();
		context.addListener(this);
		new Thread(this).start();
	}
	
	@Override
	public void run() {
		try {
			while (true) {
				//print some states of the controllers (state polling)
				boolean up = context.isStateActive("up");
				boolean down = context.isStateActive("down");
				boolean left = context.isStateActive("left");
				boolean right = context.isStateActive("right");
				float verticalMove = context.getControllerAxisValue("controller_1_vertical_move_axis");
				Gdx.app.log(getClass().getSimpleName(),
						"Up: " + up + " Down: " + down + " Left: " + left + " Right: " + right + " verticalMove: " + verticalMove);
				
				Thread.sleep(2000);
			}
		}
		catch (InterruptedException e) {
			Gdx.app.log(getClass().getSimpleName(), "Error: sleep interrupted", e);
		}
	}
	
	@Override
	public boolean onAction(String action, Type type, Parameters parameters) {
		//listen to actions (event listening)
		Gdx.app.log(getClass().getSimpleName(), "Action: " + action + " Type: " + type + " Parameters: " + parameters);
		return false;
	}
}
