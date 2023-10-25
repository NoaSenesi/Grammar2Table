package fr.senesi.g2t.table;

import fr.senesi.g2t.fsm.Rule;
import fr.senesi.g2t.fsm.State;

public class Action {
	private ActionType type;
	private Rule toRule;
	private State toState;

	public Action(ActionType type) {
		this.type = type;
	}

	public Action(ActionType type, Rule toRule) {
		this.type = type;
		this.toRule = toRule;
	}

	public Action(ActionType type, State toState) {
		this.type = type;
		this.toState = toState;
	}

	public ActionType getType() {
		return type;
	}

	public Rule getRule() {
		return toRule;
	}

	public State getState() {
		return toState;
	}

	public String toString() {
		String ret = type.toString();

		if (toState != null && toState.getId() != 0) ret += " I" + toState.getId();
		else if (toRule != null) ret += " " + toRule.toStringFree();

		return ret;
	}

	public enum ActionType {
		SHIFT,
		REDUCE,
		ACCEPT,
		GOTO,
		ERROR
	}
}