package net.jfabricationgames.gdx.screen.menu.components;

public class SingleLineMenuBox extends MenuBox {
	
	private static final Part[] PARTS = new Part[] {Part.LEFT, Part.MID, Part.RIGHT};
	
	public enum SingleLineTextureType {
		
		SMALL_BANNER("config/menu/backgrounds/small_banner_textures.json"), //
		GREEN_BOARD("config/menu/backgrounds/green_board_single_line_textures.json"), //
		YELLOW_BOARD("config/menu/backgrounds/yellow_board_single_line_textures.json"), //
		YELLOW_PAPER("config/menu/backgrounds/yellow_paper_single_line_textures.json"), //
		INVENTORY("config/menu/backgrounds/inventory_single_line_textures.json"); //
		
		public final String configFile;
		
		private SingleLineTextureType(String configFile) {
			this.configFile = configFile;
		}
	}
	
	private SingleLineTextureType type;
	
	public SingleLineMenuBox(int partsX, SingleLineTextureType type) {
		super(partsX, 1);
		this.type = type;
		
		loadTextureParts();
	}
	
	@Override
	protected Part[] getUsedParts() {
		return PARTS;
	}
	
	@Override
	protected String getTextureConfigFile() {
		return type.configFile;
	}
	
	@Override
	protected float summedHeight() {
		return textureParts.get(Part.MID).getRegionHeight();
	}
	
	@Override
	protected Part getPartForPosition(int x, int y) {
		if (x == 0) {
			return Part.LEFT;
		}
		else if (x == partsX - 1) {
			return Part.RIGHT;
		}
		else {
			return Part.MID;
		}
	}
}
