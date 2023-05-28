package fr.senesi.g2t.fsm;

import java.util.ArrayList;
import java.util.List;

import fr.senesi.g2t.grammar.Grammar;
import fr.senesi.g2t.tokenizer.Token;

public class FiniteStateMachine {
	private Grammar grammar;
	private List<State> states;

	public FiniteStateMachine(Grammar g) {
		grammar = g.copy();
		grammar.augment();

		states = new ArrayList<>();
	}

	public Grammar getGrammar() {
		return grammar;
	}

	public List<State> getStates() {
		if (states != null) return states;

		return states;
	}

	public State getInitialState() {
		if (states.size() >= 1) return states.get(0);

		List<Rule> rules = new ArrayList<>();
		List<String> right = new ArrayList<>();

		for (Token t : grammar.getRules().get(grammar.getAxiom()).get(0)) right.add(t.getValue());

		rules.add(new Rule(grammar.getAxiom(), right));

		State state = createOrGetStateFromRules(rules);
		states.add(state);

		return state;
	}

	public State createOrGetStateFromRules(List<Rule> rules) {
		List<Rule> newRules = new ArrayList<>(rules);

		for (int i = 0; i < newRules.size(); i++) {
			Rule rule = newRules.get(i);

			if (rule.isFinished()) continue;

			String next = rule.peek();
			if (!grammar.getNonTerminals().contains(next)) continue;

			List<String> parent = new ArrayList<>();
			for (int n = rule.getCursor() + 1; n < rule.getRight().size(); n++) parent.add(rule.getRight().get(n));
			parent.addAll(rule.getParent());

			List<String> context = grammar.firstsRule(parent, true);

			for (List<Token> trule : grammar.getRules().get(next)) {
				List<String> right = new ArrayList<>();
				for (Token t : trule) right.add(t.getValue());

				Rule newRule = new Rule(next, right, context, parent);

				for (int j = 0; j < newRules.size(); j++) {
					if (newRules.get(j).equals(newRule)) break;

					newRules.add(newRule);
				}
			}
		}

		State state = new State(newRules, this);

		for (State s : states) {
			if (s.equals(state)) return s;
		}

		states.add(state);

		return state;
	}
	/*
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

		for (State s : states) s.factorize();
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
		List<Rule> rules = new ArrayList<>();
		rules.addAll(fromRules);

		for (int i = 0; i < rules.size(); i++) {
			Rule rule = rules.get(i);
			String peek = rule.peek();
			if (peek == null) continue;

			if (augmentedGrammar.getNonTerminals().indexOf(peek) == -1) continue;

			for (String nr : augmentedGrammar.getRules(peek)) {
				String parent = rule.getRight().substring(rule.getCursor() + 1, rule.getRight().length()) + rule.getParentContextRule();
				String context = augmentedGrammar.firstsRule(parent);

				for (char c : context.toCharArray()) {
					Rule nrule = new Rule(peek, nr, String.valueOf(c), parent);

					boolean found = false;

					for (Rule r : rules) {
						if (r.equals(nrule)) {
							found = true;
							break;
						}
					}

					if (found) continue;

					rules.add(nrule);
				}
			}
		}

		State state = new State(states.size(), rules, this);

		for (State s : states) {
			if (s.equals(state)) return s;
		}

		states.add(state);

		return state;
	}
	*/
}
