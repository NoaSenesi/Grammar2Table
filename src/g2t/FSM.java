package g2t;

import java.util.ArrayList;
import java.util.List;

public class FSM {
	private Grammar grammar, augmentedGrammar;
	private List<State> states;

	public FSM(Grammar g) {
		grammar = g;
		augmentedGrammar = g.copy();
		augmentedGrammar.getRules().put(augmentedGrammar.getAxiom() + "'", new String[] {String.valueOf(augmentedGrammar.getAxiom())});
		states = new ArrayList<>();
	}

	public Grammar getGrammar() {
		return grammar;
	}

	public Grammar getAugmentedGrammar() {
		return augmentedGrammar;
	}

	public void createAllStates() {
		State state = getFirstState();

		for (int i = 0; i < states.size(); i++) {
			if (i != 0) state = states.get(i);

			for (char c : (augmentedGrammar.getTerminals() + augmentedGrammar.getNonTerminals()).toCharArray()) {
				State newState = state.shift(c);

				if (newState == null) continue;

				if (!isStateCreated(newState)) states.add(newState);
			}
		}
	}

	public List<State> getStates() {
		return states;
	}

	public boolean isStateCreated(State state) {
		for (State s : states) {
			if (s.equals(state)) return true;
		}

		return false;
	}

	public State getFirstState() {
		if (states.size() > 0) return states.get(0);

		List<Rule> fromRules = new ArrayList<>();
		fromRules.add(new Rule(augmentedGrammar.getAxiom(), augmentedGrammar.getAxiom().replace("'", ""), "$", "$"));

		State state = getOrCreateStateFromRules(fromRules);

		return state;
	}

	public State getOrCreateStateFromRules(List<Rule> fromRules) {
		List<Rule> rules = new ArrayList<>(), newRules = new ArrayList<>(), tempRules = new ArrayList<>();

		tempRules.addAll(fromRules);

		while (tempRules.size() > 0 || newRules.size() > 0) {
			rules.addAll(newRules);
			newRules.clear();
			newRules.addAll(tempRules);
			tempRules.clear();


			for (Rule rule : newRules) {
				String peek = rule.peek();
				if (peek == null) continue;

				if (augmentedGrammar.getNonTerminals().indexOf(peek) == -1) continue;

				for (String nr : augmentedGrammar.getRules(peek)) {
					String parent = rule.getRight().substring(rule.getCursor() + 1, rule.getRight().length()) + rule.getParentContextRule();
					String cond = augmentedGrammar.firstsRule(parent);

					Rule nrule = new Rule(peek, nr, cond, parent);

					boolean found = false;

					for (Rule r : rules) {
						if (r.coreEquals(nrule)) {
							found = true;
							break;
						}
					}

					if (found) continue;

					for (Rule r : newRules) {
						if (r.coreEquals(nrule)) {
							found = true;
							break;
						}
					}

					if (found) continue;

					for (Rule r : tempRules) {
						if (r.coreEquals(nrule)) {
							found = true;
							r.addContext(nrule.getContext());
						}
					}

					if (!found) tempRules.add(nrule);
				}
			}
		}

		if (rules.size() == 1 && rules.get(0).getRight().equals("^")) return null;

		State state = new State(states.size(), rules, this);

		for (State s : states) {
			if (s.equals(state)) return s;
		}

		states.add(state);

		return state;
	}
}
