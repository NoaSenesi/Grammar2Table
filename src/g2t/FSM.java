package g2t;

import java.util.ArrayList;
import java.util.List;

public class FSM {
	private Grammar augmentedGrammar;
	private List<State> states;

	public FSM(Grammar g) {
		augmentedGrammar = g.copy();
		augmentedGrammar.getRules().put(augmentedGrammar.getAxiom() + "'", new String[] {String.valueOf(augmentedGrammar.getAxiom())});
		states = new ArrayList<>();
	}

	public Grammar getAugmentedGrammar() {
		return augmentedGrammar;
	}

	public State getFirstState() {
		if (states.size() > 0) return states.get(0);

		List<Rule> rules = new ArrayList<>(), newRules = new ArrayList<>(), tempRules = new ArrayList<>();

		tempRules.add(new Rule(augmentedGrammar.getAxiom(), augmentedGrammar.getAxiom().replace("'", ""), "$"));

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
					Rule nrule = new Rule(peek, nr, augmentedGrammar.firsts(rule.peek(1)));

					boolean found = false;

					for (Rule r : rules) {
						if (r.contextEquals(nrule)) {
							found = true;
							break;
						}
					}

					if (found) continue;

					for (Rule r : newRules) {
						if (r.contextEquals(nrule)) {
							found = true;
							break;
						}
					}

					if (found) continue;

					for (Rule r : tempRules) {
						if (r.contextEquals(nrule)) {
							found = true;
							r.addCondition(nrule.getCondition());
						}
					}

					if (!found) tempRules.add(nrule);
				}
			}
		}

		State state = new State(0, rules);
		states.add(state);

		return state;
	}
}
