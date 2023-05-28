package fr.senesi.g2t.fsm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class State {
	private int id;
	private List<Rule> rules;
	private FiniteStateMachine fsm;
	private Map<String, State> transitions;
	private boolean factorized = false;

	public State(List<Rule> rules, FiniteStateMachine fsm) {
		this.id = fsm.getStates().size();
		this.rules = rules;
		this.fsm = fsm;
		transitions = new HashMap<>();
	}

	public int getId() {
		return id;
	}

	public List<Rule> getRules() {
		return rules;
	}

	public FiniteStateMachine getFSM() {
		return fsm;
	}

	public Map<String, State> getTransitions() {
		return transitions;
	}

	public void factorize() {
		if (factorized) return;
		factorized = true;

		List<Rule> newRules = new ArrayList<>();

		for (Rule r : rules) {
			boolean found = false;

			for (Rule nr : newRules) {
				if (r.coreEquals(nr)) {
					nr.addContext(r.getContext());
					found = true;
					break;
				}
			}

			if (!found) newRules.add(r.copy());
		}

		rules = newRules;
	}

	public boolean isFactorized() {
		return factorized;
	}

	public State shift(String token) {
		if (transitions.containsKey(token)) return transitions.get(token);

		List<Rule> rules = new ArrayList<>();

		for (Rule r : this.rules) {
			if (r.isFinished()) continue;

			if (r.peek().equals(token)) {
				Rule newRule = r.copy();
				newRule.shift();
				rules.add(newRule);
			}
		}

		if (rules.isEmpty()) return null;

		State state = fsm.createOrGetStateFromRules(rules);
		transitions.put(token, state);

		return state;
	}

	public boolean equals(Object o) {
		if (!(o instanceof State)) return false;

		State s = (State) o;

		if (rules.size() != s.getRules().size()) return false;

		for (int i = 0; i < rules.size(); i++) {
			if (!rules.get(i).equals(s.getRules().get(i))) return false;
		}

		return true;
	}

	public boolean coreEquals(Object o) {
		if (!(o instanceof State)) return false;

		State s = (State) o;

		if (rules.size() != s.getRules().size()) return false;

		for (int i = 0; i < rules.size(); i++) {
			if (!rules.get(i).coreEquals(s.getRules().get(i))) return false;
		}

		return true;
	}

	public void print() {
		int maxlen = 0;

		for (Rule r : rules) {
			if (r.toString().length() > maxlen) maxlen = r.toString().length();
		}

		String s = "State: I" + id;

		if (s.length() > maxlen) maxlen = s.length();

		System.out.print("┌");
		for (int i = 0; i < maxlen+2; i++) System.out.print("─");
		System.out.println("┐");

		System.out.print("│ " + s);
		for (int i = 0; i < maxlen - s.length(); i++) System.out.print(" ");
		System.out.println(" │");

		System.out.print("├");
		for (int i = 0; i < maxlen+2; i++) System.out.print("─");
		System.out.println("┤");

		for (Rule r : rules) {
			System.out.print("│ " + r);
			for (int i = 0; i < maxlen - r.toString().length(); i++) System.out.print(" ");
			System.out.println(" │");
		}

		System.out.print("└");
		for (int i = 0; i < maxlen+2; i++) System.out.print("─");
		System.out.println("┘");
	}
}