package net.jfabricationgames.gdx.character.state;

public interface CharacterStateListener {
	
	public void enteringState(CharacterState state);
	
	public void leavingState(CharacterState state);
}
