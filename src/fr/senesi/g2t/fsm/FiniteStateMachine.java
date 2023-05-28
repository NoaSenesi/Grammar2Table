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

				boolean exists = false;

				for (Rule r : newRules) {
					if (r.equals(newRule)) {
						exists = true;
						break;
					}
				}

				if (!exists) newRules.add(newRule);
			}
		}

		State state = new State(newRules, this);

		for (State s : states) {
			if (s.equals(state)) return s;
		}

		return state;
	}

	public void createAllStates() {
		State state = getInitialState();
		List<String> tokens = new ArrayList<>();
		tokens.addAll(grammar.getNonTerminals());
		tokens.addAll(grammar.getTerminals());

		for (int i = 0; i < states.size(); i++) {
			if (i != 0) state = states.get(i);

			for (String token : tokens) {
				State newState = state.shift(token);

				if (newState == null) continue;

				if (!isStateCreated(newState)) states.add(newState);
			}
		}

		for (State s : states) s.factorize();
	}

	public boolean isStateCreated(State state) {
		for (State s : states) {
			if (s.equals(state)) return true;
		}

		return false;
	}
}