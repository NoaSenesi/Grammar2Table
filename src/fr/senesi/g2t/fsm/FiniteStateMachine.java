package fr.senesi.g2t.fsm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

import fr.senesi.g2t.grammar.Grammar;
import fr.senesi.g2t.tokenizer.Token;

public class FiniteStateMachine {
	private Grammar grammar;
	private LinkedHashSet<State> states;
	private boolean quiet = false;

	public FiniteStateMachine(Grammar g) {
		grammar = g.copy();
		grammar.augment();

		states = new LinkedHashSet<>();
	}

	public Grammar getGrammar() {
		return grammar;
	}

	public List<State> getStates() {
		return new ArrayList<>(states);
	}

	public State getInitialState() {
		if (states.size() >= 1) return getStates().get(0);

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
			if (!quiet) System.out.print("Creating state " + i + "...\r");

			if (i != 0) state = getStates().get(i);

			for (String token : tokens) {
				State newState = state.shift(token);

				if (newState == null) continue;

				if (!states.contains(newState)) states.add(newState);
			}
		}

		if (!quiet) System.out.println(states.size() + " states created in total");

		for (State s : states) s.factorize();
	}

	public List<List<State>> findDuplicates() {
		List<List<State>> duplicates = new ArrayList<>();
		
        List<State> sortedStates = new ArrayList<>(states);

        Collections.sort(sortedStates, (s1, s2) -> {
            if (s1.coreEquals(s2)) return 0;

            return s1.coreHashCode() < s2.coreHashCode() ? -1 : 1;
        });

        for (int i = 1, start = 0; i < states.size(); i++) {
            while (sortedStates.get(start).coreEquals(sortedStates.get(i))) {
                i++;

                if (i >= states.size()) {
                    //i = states.size() - 1;
                    break;
                }
            }

            if (i - start <= 1) {
                start = i;
                continue;
            }

            List<State> group = new ArrayList<>();
            while (start < i) group.add(sortedStates.get(start++));

            duplicates.add(group);
        }

		return duplicates;
	}

	public void setQuiet(boolean quiet) {
		this.quiet = quiet;
	}
}
