package g2t;

import java.util.ArrayList;
import java.util.List;

public class State {
	private int id;
	private List<Rule> rules;
	private FSM parent;

	public State(int id, List<Rule> rules, FSM parent) {
		this.id = id;
		this.rules = rules;
		this.parent = parent;
	}

	public void print() {
		int maxlen = 0;

		for (Rule r : rules) {
			if (r.toString().length() > maxlen) maxlen = r.toString().length();
		}

		String s = "State: I" + id;

		if (s.length() > maxlen) maxlen = s.length();

		System.out.print("|");
		for (int i = 0; i < maxlen+2; i++) System.out.print("-");
		System.out.println("|");

		System.out.print("| " + s);
		for (int i = 0; i < maxlen - s.length(); i++) System.out.print(" ");
		System.out.println(" |");

		System.out.print("|");
		for (int i = 0; i < maxlen+2; i++) System.out.print("-");
		System.out.println("|");

		for (Rule r : rules) {
			System.out.print("| " + r);
			for (int i = 0; i < maxlen - r.toString().length(); i++) System.out.print(" ");
			System.out.println(" |");
		}

		System.out.print("|");
		for (int i = 0; i < maxlen+2; i++) System.out.print("-");
		System.out.println("|");

		System.out.println();
	}

	public List<Rule> getRules() {
		return rules;
	}

	public State shift(char c) {
		List<Rule> newRules = new ArrayList<>();

		for (Rule rule : rules) {
			if (rule.peek().equals(String.valueOf(c))) {
				Rule nr = rule.copy();
				nr.shift();
				newRules.add(nr);
			}
		}

		if (newRules.size() == 0) return null;

		State state = parent.getOrCreateStateFromRules(newRules);

		return state;
	}

	public boolean equals(State state) {
		if (rules.size() != state.rules.size()) return false;

		for (Rule rule : rules) {
			boolean none = true;

			for (Rule srule : state.getRules()) {
				if (rule.equals(srule)) none = false;
			}

			if (none) return false;
		}

		return true;
	}

	public boolean contextEquals(State state) {
		if (rules.size() != state.rules.size()) return false;

		for (Rule rule : rules) {
			boolean none = true;

			for (Rule srule : state.getRules()) {
				if (rule.contextEquals(srule)) none = false;
			}

			if (none) return false;
		}

		return true;
	}
}