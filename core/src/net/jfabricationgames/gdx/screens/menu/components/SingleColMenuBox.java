package net.jfabricationgames.gdx.screens.menu.components;

public class SingleColMenuBox extends MenuBox {
	
	private static final Part[] PARTS = new Part[] {Part.UP, Part.MID, Part.DOWN};
	
	public enum SingleColTextureType {
		
		GREEN_BOARD("config/menu/backgrounds/green_board_single_col_textures.json"), //
		YELLOW_BOARD("config/menu/backgrounds/yellow_board_single_col_textures.json"), //
		YELLOW_PAPER("config/menu/backgrounds/yellow_paper_single_col_textures.json"), //
		INVENTORY("config/menu/backgrounds/inventory_single_col_textures.json"); //
		
		public final String configFile;
		
		private SingleColTextureType(String configFile) {
			this.configFile = configFile;
		}
	}
	
	private SingleColTextureType type;
	
	public SingleColMenuBox(int partsY, SingleColTextureType type) {
		super(1, partsY);
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
	protected float summedWidth() {
		return textureParts.get(Part.MID).getRegionWidth();
	}
	
	@Override
	protected Part getPartForPosition(int x, int y) {
		if (y == 0) {
			return Part.DOWN;
		}
		else if (y == partsY - 1) {
			return Part.UP;
		}
		else {
			return Part.MID;
		}
	}
}
