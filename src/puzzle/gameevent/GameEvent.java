package puzzle.gameevent;

/**
 * Main object for game events. declares all the event types and acts
 * as a struct for events.
 * 
 * @author Heinz
 */
/**
 * Objeto principal para eventos de jogo. declara todos os tipos de eventos e atos
 * como uma estrutura para eventos.
 * 
 * @autor Heinz
 */
public class GameEvent {

	/**
	 * All the possible game events
	 * @author Heinz
	 */
	/**
	 * Todos os eventos de jogo poss�veis
	 * @autor Heinz
	 */
	public static enum State {
		START_GAME, LOAD_GAME,
		PREPARE_TO_MOVE_PIECE, MOVE_PIECE, 
		PREPARE_TO_TURN_PIECE, TURN_PIECE, 
		PREPARE_TO_SNAP_PIECE, SNAP_PIECE, 
		PREPARE_TO_HIGHLIGHT_PIECE, HIGHLIGHT_PIECE,
		END_GAME
	}

	private final State type;

	private final Object info;

	public GameEvent(State type, Object info) {
		this.type = type;
		this.info = info;
	}

	public Object getInfo() {
		return info;
	}

	public State getType() {
		return type;
	}

}
