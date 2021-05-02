package net.jfabricationgames.gdx.character.enemy.ai;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;

import net.jfabricationgames.gdx.character.ai.ArtificialIntelligence;
import net.jfabricationgames.gdx.character.ai.implementation.FollowAI;
import net.jfabricationgames.gdx.character.ai.move.AIPositionChangingMove;
import net.jfabricationgames.gdx.character.ai.move.MoveType;
import net.jfabricationgames.gdx.character.player.PlayableCharacter;
import net.jfabricationgames.gdx.character.state.CharacterState;
import net.jfabricationgames.gdx.event.EventConfig;
import net.jfabricationgames.gdx.event.EventHandler;
import net.jfabricationgames.gdx.event.EventListener;
import net.jfabricationgames.gdx.event.EventType;

public class TeamMovementAI extends FollowAI implements EventListener {
	
	private PlayableCharacter teamTargetToFollow;
	
	private float distanceToInformTeamMates;
	private String teamId;
	
	public TeamMovementAI(ArtificialIntelligence subAI, CharacterState movingState, CharacterState idleState, float distanceToInformTeamMates,
			String teamId) {
		super(subAI, movingState, idleState);
		this.distanceToInformTeamMates = distanceToInformTeamMates;
		this.teamId = teamId;
		
		EventHandler.getInstance().registerEventListener(this);
	}
	
	@Override
	public void calculateMove(float delta) {
		subAI.calculateMove(delta);
		
		if (targetToFollow != null || teamTargetToFollow != null) {
			AIPositionChangingMove move = new AIPositionChangingMove(this);
			
			if (targetToFollow != null && distanceToTarget(targetToFollow) > minDistanceToTarget) {
				move.movementTarget = targetToFollow.getPosition();
			}
			else if (teamTargetToFollow != null && distanceToTarget(teamTargetToFollow) > minDistanceToTarget) {
				move.movementTarget = teamTargetToFollow.getPosition();
			}
			
			setMove(MoveType.MOVE, move);
		}
	}
	
	private float distanceToTarget(PlayableCharacter target) {
		return distanceToPosition(target.getPosition());
	}
	private float distanceToPosition(Vector2 position) {
		return character.getPosition().sub(position).len();
	}
	
	@Override
	public void beginContact(Contact contact) {
		PlayableCharacter collidingCharacter = getObjectCollidingWithEnemySensor(contact, PlayableCharacter.class);
		// if the sensor touches a PlayableCharacter -> start following him
		if (collidingCharacter != null) {
			followTarget(collidingCharacter);
			if (teamTargetToFollow == null) {
				informTeamMatesAboutNewTarget(collidingCharacter);
			}
		}
		
		subAI.beginContact(contact);
	}
	
	private void informTeamMatesAboutNewTarget(PlayableCharacter collidingCharacter) {
		if (teamId != null && !teamId.isEmpty()) {
			TeamCallEventDto eventDto = new TeamCallEventDto() //
					.setTeamId(teamId) //
					.setTarget(collidingCharacter) //
					.setCallersPosition(character.getPosition());
			
			EventHandler.getInstance().fireEvent(new EventConfig().setEventType(EventType.AI_TEAM_CALL).setParameterObject(eventDto));
		}
	}
	
	@Override
	public void endContact(Contact contact) {
		PlayableCharacter collidingCharacter = getObjectCollidingWithEnemySensor(contact, PlayableCharacter.class);
		if (collidingCharacter != null) {
			stopFollowingPlayer();
			informMatesAboutLostTarget();
		}
		
		subAI.endContact(contact);
	}
	
	private void informMatesAboutLostTarget() {
		informTeamMatesAboutNewTarget(null);
	}
	
	@Override
	public void handleEvent(EventConfig event) {
		if (event.eventType == EventType.AI_TEAM_CALL) {
			TeamCallEventDto dto = (TeamCallEventDto) event.parameterObject;
			if (isMyTeam(dto)) {
				if (isTargetFound(dto)) {
					if (isMateInRange(dto)) {
						teamTargetToFollow = dto.target;
					}
				}
				else {
					//target lost
					if (targetToFollow != null) {
						//we still have a target -> inform team mates
						informTeamMatesAboutNewTarget(targetToFollow);
					}
					else {
						teamTargetToFollow = null;
					}
				}
			}
		}
	}
	
	private boolean isMyTeam(TeamCallEventDto dto) {
		return dto.teamId.equals(teamId);
	}
	
	private boolean isTargetFound(TeamCallEventDto dto) {
		return dto.target != null;
	}
	
	private boolean isMateInRange(TeamCallEventDto dto) {
		return distanceToPosition(dto.callersPosition) < distanceToInformTeamMates;
	}
	
	@Override
	public void characterRemovedFromMap() {
		EventHandler.getInstance().removeEventListener(this);
	}
	
	private class TeamCallEventDto {
		
		public String teamId;
		public Vector2 callersPosition;
		public PlayableCharacter target;
		
		public TeamCallEventDto setTeamId(String teamId) {
			this.teamId = teamId;
			return this;
		}
		
		public TeamCallEventDto setCallersPosition(Vector2 callersPosition) {
			this.callersPosition = callersPosition;
			return this;
		}
		
		public TeamCallEventDto setTarget(PlayableCharacter target) {
			this.target = target;
			return this;
		}
	}
}
