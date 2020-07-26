package net.jfabricationgames.gdx.input;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlReader.Element;

public class InputProfile implements InputProcessor, ControllerListener {
	
	/** Can be changed (before the XML-profile is loaded) if higher or lower dead zones for the analog sticks are needed */
	public static float CONTROLLER_AXIS_DEAD_ZONE = 0.1f;
	
	private ArrayMap<String, InputContext> contexts;
	private InputContext context;
	
	public InputProfile(FileHandle handle, InputMultiplexer inputMultiplexer) {
		inputMultiplexer.addProcessor(this);
		Controllers.addListener(this);
		
		contexts = new ArrayMap<String, InputContext>();
		context = null;
		
		try {
			Gdx.app.debug("InputProfile", "Reading file " + handle.path());
			InputStream inputStream = handle.read();
			InputStreamReader streamReader = new InputStreamReader(inputStream, "UTF-8");
			XmlReader reader = new XmlReader();
			
			Element root = reader.parse(streamReader);
			int numContexts = root.getChildCount();
			
			for (int i = 0; i < numContexts; ++i) {
				Element contextElement = root.getChild(i);
				InputContext context = new InputContext();
				context.load(contextElement);
				contexts.put(context.getName(), context);
			}
		}
		catch (UnsupportedEncodingException e) {
			Gdx.app.error("InputProfile", "error loading file " + handle.path(), e);
		}
	}
	
	public void setContext(String contextName) {
		context = contexts.get(contextName);
	}
	
	public InputContext getContext() {
		return context;
	}
	
	public InputContext getContextByName(String name) {
		return contexts.get(name);
	}
	
	@Override
	public boolean keyDown(int keycode) {
		if (context != null) {
			return context.keyDown(keycode);
		}
		return false;
	}
	
	@Override
	public boolean keyUp(int keycode) {
		if (context != null) {
			return context.keyUp(keycode);
		}
		return false;
	}
	
	@Override
	public boolean keyTyped(char character) {
		if (context != null) {
			return context.keyTyped(character);
		}
		return false;
	}
	
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if (context != null) {
			return context.touchDown(screenX, screenY, pointer, button);
		}
		return false;
	}
	
	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if (context != null) {
			return context.touchUp(screenX, screenY, pointer, button);
		}
		return false;
	}
	
	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		if (context != null) {
			return context.touchDragged(screenX, screenY, pointer);
		}
		return false;
	}
	
	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		if (context != null) {
			return context.mouseMoved(screenX, screenY);
		}
		return false;
	}
	
	@Override
	public boolean scrolled(int amount) {
		if (context != null) {
			return context.scrolled(amount);
		}
		return false;
	}
	
	@Override
	public boolean buttonDown(Controller controller, int buttonCode) {
		if (context != null) {
			return context.controllerButtonDown(controller, buttonCode);
		}
		return false;
	}
	
	@Override
	public boolean buttonUp(Controller controller, int buttonCode) {
		if (context != null) {
			return context.controllerButtonUp(controller, buttonCode);
		}
		return false;
	}
	
	@Override
	public boolean axisMoved(Controller controller, int axisCode, float value) {
		if (context != null && Math.abs(value) > CONTROLLER_AXIS_DEAD_ZONE) {
			return context.controllerAxisMoved(controller, axisCode, value);
		}
		return false;
	}
	
	@Override
	public boolean povMoved(Controller controller, int povCode, PovDirection value) {
		if (context != null) {
			return context.controllerPovMoved(controller, povCode, value);
		}
		return false;
	}
	
	@Override
	public boolean xSliderMoved(Controller controller, int sliderCode, boolean value) {
		//not supported here
		return false;
	}
	
	@Override
	public boolean ySliderMoved(Controller controller, int sliderCode, boolean value) {
		//not supported here
		return false;
	}
	
	@Override
	public boolean accelerometerMoved(Controller controller, int accelerometerCode, Vector3 value) {
		//not supported here
		return false;
	}
	
	@Override
	public void connected(Controller controller) {
		//not supported here
	}
	
	@Override
	public void disconnected(Controller controller) {
		//not supported here
	}
}
