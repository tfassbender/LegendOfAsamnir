package net.jfabricationgames.gdx.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetErrorListener;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AssetLoader;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonValue.JsonIterator;
import com.badlogic.gdx.utils.ObjectMap;

public class AssetGroupManager implements Disposable, AssetErrorListener {
	
	private static AssetGroupManager instance;
	
	public static AssetGroupManager getInstance() {
		if (instance == null) {
			throw new IllegalStateException(
					"The AssetGroupManager has not yet been initialized. Use the initialize(String) method to initialize it.");
		}
		return instance;
	}
	
	public static synchronized AssetGroupManager initialize(String assetFile) {
		if (instance != null) {
			throw new IllegalStateException("The AssetGroupManager has already been initialized.");
		}
		instance = new AssetGroupManager(assetFile);
		return instance;
	}
	
	public synchronized boolean isInitialized() {
		return instance != null;
	}
	
	private AssetManager manager;
	private ObjectMap<String, Array<Asset>> groups;
	
	private AssetGroupManager(String assetFile) {
		manager = new AssetManager();
		manager.setErrorListener(this);
		
		loadGroups(assetFile);
	}
	
	public AssetLoader<?, ?> getLoader(Class<?> type) {
		return manager.getLoader(type);
	}
	
	@SuppressWarnings("unchecked")
	public void loadGroup(String groupName) {
		Gdx.app.log(getClass().getSimpleName(), "loading group: " + groupName);
		
		Array<Asset> assets = groups.get(groupName, null);
		
		if (assets != null) {
			for (Asset asset : assets) {
				Gdx.app.log(getClass().getSimpleName(), "loading... " + asset.path);
				manager.load(asset.path, asset.type, asset.parameters);
			}
		}
		else {
			Gdx.app.log(getClass().getSimpleName(), "error loading group: " + groupName + ", not found");
		}
	}
	
	public void unloadGroup(String groupName) {
		Gdx.app.log(getClass().getSimpleName(), "unloading group: " + groupName);
		
		Array<Asset> assets = groups.get(groupName, null);
		
		if (assets != null) {
			for (Asset asset : assets) {
				if (manager.isLoaded(asset.path, asset.type)) {
					manager.unload(asset.path);
				}
			}
		}
		else {
			Gdx.app.log(getClass().getSimpleName(), "error unloading group: " + groupName + ", not found");
		}
	}
	
	public synchronized <T> T get(String fileName) {
		return manager.get(fileName);
	}
	
	public synchronized <T> T get(String fileName, Class<T> type) {
		return manager.get(fileName, type);
	}
	
	public <T> boolean isLoaded(String fileName, Class<T> type) {
		return manager.isLoaded(fileName, type);
	}
	
	public boolean update() {
		return manager.update();
	}
	
	public void finishLoading() {
		manager.finishLoading();
	}
	
	public float getProgress() {
		return manager.getProgress();
	}
	
	@Override
	public void dispose() {
		Gdx.app.log(getClass().getSimpleName(), "disposing...");
		manager.dispose();
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public void error(AssetDescriptor asset, Throwable throwable) {
		Gdx.app.log(getClass().getSimpleName(), "error loading: " + asset.fileName + " - message: " + throwable.getMessage());
	}
	
	private void loadGroups(String assetFile) {
		groups = new ObjectMap<String, Array<Asset>>();
		
		Gdx.app.log(getClass().getSimpleName(), "loading file: " + assetFile);
		
		try {
			Json json = new Json();
			JsonReader reader = new JsonReader();
			JsonValue root = reader.parse(Gdx.files.internal(assetFile));
			
			JsonIterator groupIt = root.iterator();
			
			while (groupIt.hasNext()) {
				JsonValue groupValue = groupIt.next();
				
				if (groups.containsKey(groupValue.name)) {
					Gdx.app.log(getClass().getSimpleName(), "group '" + groupValue.name + "' already exists, skipping");
					continue;
				}
				
				Gdx.app.log(getClass().getSimpleName(), "registering group: " + groupValue.name);
				Array<Asset> assets = new Array<Asset>();
				JsonIterator assetIt = groupValue.iterator();
				
				while (assetIt.hasNext()) {
					JsonValue assetValue = assetIt.next();
					
					Asset asset = json.fromJson(Asset.class, assetValue.toString());
					assets.add(asset);
				}
				
				groups.put(groupValue.name, assets);
			}
		}
		catch (Exception e) {
			Gdx.app.log(getClass().getSimpleName(), "error loading file: " + assetFile + " " + e.getMessage());
		}
	}
}