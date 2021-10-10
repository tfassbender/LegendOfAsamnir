package net.jfabricationgames.gdx.character.player.implementation;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;

import net.jfabricationgames.gdx.animation.AnimationDirector;
import net.jfabricationgames.gdx.animation.AnimationManager;
import net.jfabricationgames.gdx.animation.DummyAnimationDirector;
import net.jfabricationgames.gdx.animation.GrowingAnimationDirector;
import net.jfabricationgames.gdx.constants.Constants;
import net.jfabricationgames.gdx.data.handler.GlobalValuesDataHandler;
import net.jfabricationgames.gdx.texture.TextureLoader;

class CharacterRenderer {
	
	private static final String ANIMATION_DWARF_CONFIG_FILE = "config/animation/dwarf.json";
	private static final String TEXTURE_CONFIG_FILE_NAME = "config/dwarf/textures.json";
	
	private static final Vector2 PHYSICS_BODY_POSITION_OFFSET = new Vector2(0f, -0.15f);
	private static final float DRAWING_DIRECTION_OFFSET = 0.1f;
	private static final Vector2 DARKNESS_GRADIENT_POSITION_OFFSET = new Vector2(0f, -0.6f);
	
	private Dwarf player;
	
	private AnimationManager animationManager;
	
	protected AnimationDirector<TextureRegion> animation;
	
	private TextureLoader textureLoader;
	protected TextureRegion idleDwarfSprite;
	private TextureRegion blockSprite;
	private TextureRegion aimMarkerSprite;
	
	private GrowingAnimationDirector<TextureRegion> darknessAnimation;
	private boolean darknessFading;
	
	public CharacterRenderer(Dwarf player) {
		this.player = player;
		
		animationManager = AnimationManager.getInstance();
		animationManager.loadAnimations(ANIMATION_DWARF_CONFIG_FILE);
		
		textureLoader = new TextureLoader(TEXTURE_CONFIG_FILE_NAME);
		idleDwarfSprite = textureLoader.loadTexture("idle");
		blockSprite = textureLoader.loadTexture("block");
		aimMarkerSprite = textureLoader.loadTexture("aim_marker");
		darknessAnimation = animationManager.getGrowingAnimationDirector("darkness_fade");
		
		animation = getAnimation();
	}
	
	private AnimationDirector<TextureRegion> getAnimation() {
		if (player.action != CharacterAction.NONE && player.action != CharacterAction.BLOCK) {
			return getAnimation(player.action);
		}
		else {
			return new DummyAnimationDirector<TextureRegion>();
		}
	}
	private AnimationDirector<TextureRegion> getAnimation(CharacterAction action) {
		return animationManager.getTextureAnimationDirector(getAnimationName(action));
	}
	private String getAnimationName(CharacterAction action) {
		return action.getAnimationName();
	}
	
	public void changeAnimation() {
		animation = getAnimation();
		animation.resetStateTime();
	}
	
	public void startDarknessFade() {
		darknessFading = true;
	}
	
	public void processDarknessFadingAnimation(float delta) {
		if (darknessFading) {
			darknessAnimation.increaseStateTime(delta);
		}
	}
	
	public void drawDwarf(SpriteBatch batch) {
		TextureRegion frame;
		if (player.action == CharacterAction.NONE) {
			frame = idleDwarfSprite;
		}
		else if (player.action == CharacterAction.BLOCK) {
			frame = blockSprite;
		}
		else {
			frame = animation.getKeyFrame();
		}
		
		if (!drawingDirectionEqualsTextureDirection(frame)) {
			frame.flip(true, false);
		}
		
		draw(batch, frame);
	}
	
	private boolean drawingDirectionEqualsTextureDirection(TextureRegion frame) {
		return player.movementHandler.isDrawDirectionRight() != frame.isFlipX();
	}
	
	private void draw(SpriteBatch batch, TextureRegion frame) {
		//use null as offset parameter to not create a new empty vector every time
		draw(batch, frame, 0, 0, frame.getRegionWidth(), frame.getRegionHeight());
	}
	
	private void draw(SpriteBatch batch, TextureRegion frame, Vector2 offset, float width, float height) {
		if (offset != null) {
			draw(batch, frame, offset.x, offset.y, width, height);
		}
		else {
			draw(batch, frame, 0, 0, width, height);
		}
	}
	
	private void draw(SpriteBatch batch, TextureRegion frame, float offsetX, float offsetY, float width, float height) {
		float originX = 0.5f * width + PHYSICS_BODY_POSITION_OFFSET.x * width;
		float originY = 0.5f * height + PHYSICS_BODY_POSITION_OFFSET.y * height;
		float x = player.bodyHandler.body.getPosition().x - originX;
		float y = player.bodyHandler.body.getPosition().y - originY;
		x += offsetX;
		y += offsetY;
		x += getDrawingDirectionOffset();
		
		batch.draw(frame, // textureRegion
				x, y, // x, y
				originX, originY, //originX, originY
				width, height, // width, height
				Constants.WORLD_TO_SCREEN, // scaleX
				Constants.WORLD_TO_SCREEN, // scaleY
				0.0f); // rotation
	}
	
	private float getDrawingDirectionOffset() {
		if (player.movementHandler.isDrawDirectionRight()) {
			return DRAWING_DIRECTION_OFFSET;
		}
		else {
			return -DRAWING_DIRECTION_OFFSET;
		}
	}
	
	public void drawAimMarker(SpriteBatch batch) {
		final float aimMarkerDistanceFactor = 0.5f;
		final float aimMarkerOffsetY = -0.1f;
		Vector2 aimMarkerOffset = player.movementHandler.getMovingDirection().getNormalizedDirectionVector().scl(aimMarkerDistanceFactor).add(0,
				aimMarkerOffsetY);
		final float aimMarkerSize = 5f;
		draw(batch, aimMarkerSprite, aimMarkerOffset, aimMarkerSize, aimMarkerSize);
	}
	
	public void renderDarkness(SpriteBatch batch, ShapeRenderer shapeRenderer) {
		batch.begin();
		draw(batch, darknessAnimation.getKeyFrame(), //
				DARKNESS_GRADIENT_POSITION_OFFSET.x, //
				// the texture seems to move while the animation is playing, which causes a gap between the texture and the black overlay underneath
				// the state time with a factor of -2f is used as a workarround against this
				DARKNESS_GRADIENT_POSITION_OFFSET.y - darknessAnimation.getStateTime() * 2f, // 
				darknessAnimation.getWidth(), darknessAnimation.getHeight());
		batch.end();
		
		shapeRenderer.begin(ShapeType.Filled);
		shapeRenderer.setColor(Color.BLACK);
		
		float x = player.bodyHandler.body.getPosition().x;
		float y = player.bodyHandler.body.getPosition().y;
		
		final float LEF_RIGHT_OFFSET_X = 0.2f;
		final float UP_OFFSET_Y = 0.25f;
		final float DOWN_OFFSET_Y = 0.5f;
		
		//left
		shapeRenderer.rect(x - darknessAnimation.getWidth() * Constants.WORLD_TO_SCREEN * 0.5f + LEF_RIGHT_OFFSET_X, //
				y + Constants.SCENE_HEIGHT, //
				-Constants.SCENE_WIDTH, //
				-Constants.SCENE_HEIGHT * 2f);
		
		//right
		shapeRenderer.rect(x + darknessAnimation.getWidth() * Constants.WORLD_TO_SCREEN * 0.5f - LEF_RIGHT_OFFSET_X, //
				y + Constants.SCENE_HEIGHT, //
				Constants.SCENE_WIDTH, //
				-Constants.SCENE_HEIGHT * 2f);
		
		//up
		shapeRenderer.rect(x - darknessAnimation.getWidth() * Constants.WORLD_TO_SCREEN * 0.5f, //
				y + darknessAnimation.getHeight() * Constants.WORLD_TO_SCREEN * 0.5f + UP_OFFSET_Y, //
				Constants.SCENE_WIDTH * 2f, //
				Constants.SCENE_HEIGHT);
		
		//down
		shapeRenderer.rect(x - darknessAnimation.getWidth() * Constants.WORLD_TO_SCREEN * 0.5f, //
				y - darknessAnimation.getHeight() * Constants.WORLD_TO_SCREEN * 0.5f + DOWN_OFFSET_Y, //
				Constants.SCENE_WIDTH * 2f, //
				-Constants.SCENE_HEIGHT);
		
		shapeRenderer.end();
		
		if (darknessAnimation.isAnimationFinished()) {
			darknessAnimation.resetStateTime();
			darknessFading = false;
			
			//set the global value for lantern used, to not render the darkness anymore
			GlobalValuesDataHandler.getInstance().put(Constants.GLOBAL_VALUE_KEY_LANTERN_USED, true);
		}
	}
}
