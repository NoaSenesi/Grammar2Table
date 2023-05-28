package fr.senesi.g2t.table;

import fr.senesi.g2t.fsm.Rule;

public class Action {
	private ActionType type;
	private Rule toRule;
	private int toState;

	public Action(ActionType type) {
		this.type = type;
	}

	public Action(ActionType type, Rule toRule) {
		this.type = type;
		this.toRule = toRule;
	}

	public Action(ActionType type, int toState) {
		this.type = type;
		this.toState = toState;
	}

	public ActionType getType() {
		return type;
	}

	public Rule getRule() {
		return toRule;
	}

	public int getState() {
		return toState;
	}

	public String toString() {
		String ret = type.toString();

		if (toState != 0) ret += " I" + toState;
		else if (toRule != null) ret += " " + toRule.toStringFree();

		return ret;
	}

	public enum ActionType {
		SHIFT,
		REDUCE,
		ACCEPT,
		ERROR
	}
}