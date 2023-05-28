package fr.senesi.g2t.grammar;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import fr.senesi.g2t.exception.SyntaxException;
import fr.senesi.g2t.tokenizer.Assign;
import fr.senesi.g2t.tokenizer.EOF;
import fr.senesi.g2t.tokenizer.Epsilon;
import fr.senesi.g2t.tokenizer.Identifier;
import fr.senesi.g2t.tokenizer.Ruleable;
import fr.senesi.g2t.tokenizer.SemiColon;
import fr.senesi.g2t.tokenizer.Separator;
import fr.senesi.g2t.tokenizer.Token;
import fr.senesi.g2t.tokenizer.Tokenizer;
import fr.senesi.g2t.tokenizer.Value;

public class Grammar {
	private Map<String, List<List<Token>>> rules;
	private Tokenizer tokenizer;
	private List<String> terminals, nonTerminals;
	private boolean augmented = false;

	public Grammar(Tokenizer tokenizer) throws SyntaxException {
		this.tokenizer = tokenizer;

		rules = new LinkedHashMap<>();
		terminals = new ArrayList<>();
		nonTerminals = new ArrayList<>();

		makeRules();
	}

	private void makeRules() throws SyntaxException {
		for (int i = 0; i < tokenizer.getTokens().size(); i++) {
			Token token = tokenizer.getTokens().get(i);

			if (token instanceof EOF) break;

			if (token instanceof SemiColon) continue;
			if (!(token instanceof Identifier)) throw new SyntaxException("Unexpected " + token.getClass().getSimpleName() + ", expected Identifier instead at line " + token.getLine());

			if (!nonTerminals.contains(token.getValue())) nonTerminals.add(token.getValue());

			if (i + 1 >= tokenizer.getTokens().size()) throw new SyntaxException("Unexpected end of file, expected = instead");

			Token next = tokenizer.getTokens().get(i + 1);
			if (!(next instanceof Assign)) throw new SyntaxException("Unexpected " + next.getClass().getSimpleName() + " token, expected = instead at line " + next.getLine());

			List<List<Token>> rule = new ArrayList<>();
			List<Token> current = new ArrayList<>();

			int cursor = i + 2;
			boolean epsilon = false;

			for (int j = cursor; j < tokenizer.getTokens().size(); j++) {
				Token t = tokenizer.getTokens().get(j);

				if (t instanceof EOF) throw new SyntaxException("Unexpected end of file, expected ; instead");

				if (t instanceof SemiColon) {
					cursor = j;

					if (current.size() == 0) throw new SyntaxException("Unexpected ; at line " + t.getLine());
					rule.add(current);
					if (rules.containsKey(token.getValue())) rules.get(token.getValue()).addAll(rule);

					else {
						rules.put(token.getValue(), rule);
					}

					break;
				}

				if (t instanceof Separator) {
					if (current.size() == 0) throw new SyntaxException("Unexpected " + t.getClass().getSimpleName() + " token at line " + t.getLine());

					rule.add(current);
					current = new ArrayList<>();

					continue;
				}

				if (t instanceof Ruleable) {
					if (epsilon) throw new SyntaxException("Unexpected " + t.getClass().getSimpleName() + " token at line " + t.getLine());

					if (!terminals.contains(t.getValue()) && t instanceof Value) terminals.add(t.getValue());
					current.add(t);

					if (t instanceof Epsilon) {
						if (current.size() > 1) throw new SyntaxException("Unexpected " + t.getClass().getSimpleName() + " token at line " + t.getLine());
						epsilon = true;
					}
				}

				else throw new SyntaxException("Unexpected " + t.getClass().getSimpleName() + " token at line " + t.getLine());

			}

			i = cursor;
		}

		for (String nt : nonTerminals) {
			if (terminals.contains(nt)) terminals.remove(nt);
		}
	}

	public void augment() {
		if (augmented) return;

		augmented = true;

		String axiom = nonTerminals.get(0);
		List<List<Token>> rules = new ArrayList<>();
		List<Token> rule = new ArrayList<>();

		rule.add(new Identifier(0, axiom));

		rules.add(rule);

		Map<String, List<List<Token>>> newRules = new LinkedHashMap<>();
		newRules.put(axiom + "'", rules);
		newRules.putAll(this.rules);

		this.rules = newRules;

		List<String> newNonTerminals = new ArrayList<>();
		newNonTerminals.add(axiom + "'");
		newNonTerminals.addAll(nonTerminals);

		nonTerminals = newNonTerminals;
	}

	public boolean isAugmented() {
		return augmented;
	}

	public Map<String, List<List<Token>>> getRules() {
		return rules;
	}

	public List<String> getTerminals() {
		return terminals;
	}

	public List<String> getNonTerminals() {
		return nonTerminals;
	}

	public String getAxiom() {
		return nonTerminals.get(0);
	}

	public void print() {
		for (String nt : rules.keySet()) {
			System.out.print(nt + " = ");

			for (List<Token> rule : rules.get(nt)) {
				for (Token t : rule) {
					if (t instanceof Value && !(t instanceof Identifier)) System.out.print("\"" + t.getValue() + "\" ");
					else System.out.print(t.getValue() + " ");
				}

				System.out.print("| ");
			}

			System.out.println("\b\b ");
		}
	}

	public List<String> firstsRule(List<String> values) {
		List<String> firsts = new ArrayList<>();

		for (String value : values) {
			if (canBeEmpty(value)) {
				firsts.addAll(firsts(value));
			} else {
				firsts.addAll(firsts(value));
				break;
			}
		}

		List<String> unique = new ArrayList<>();

		for (String first : firsts) if (!unique.contains(first)) unique.add(first);

		return unique;
	}

	public List<String> firsts(String value) {
		return firsts(value, new ArrayList<>());
	}

	private List<String> firsts(String value, List<String> visited) {
		if (visited.contains(value)) return new ArrayList<>();

		List<String> firsts = new ArrayList<>();

		if (!nonTerminals.contains(value)) firsts.add(value);

		else {
			for (List<Token> rule : rules.get(value)) {
				if (rule.size() == 0) continue;

				for (Token token : rule) {
					if (terminals.contains(token.getValue())) {
						firsts.add(token.getValue());
						break;
					} else {
						List<String> visited2 = new ArrayList<>(visited);
						visited2.add(value);

						List<String> firsts2 = firsts(token.getValue(), visited2);

						for (String first : firsts2) {
							if (firsts.indexOf(first) == -1) firsts.add(first);
						}

						if (!canBeEmpty(token.getValue())) break;
					}
				}
			}
		}

		if (firsts.contains("^")) firsts.remove("^");

		return firsts;
	}

	public boolean canBeEmpty(String nonTerminal) {
		return canBeEmpty(nonTerminal, new ArrayList<>());
	}

	private boolean canBeEmpty(String nonTerminal, List<String> visited) {
		if (nonTerminal.equals("^")) return true;
		if (terminals.contains(nonTerminal)) return false;
		if (visited.contains(nonTerminal)) return false;
		if (!rules.containsKey(nonTerminal)) return false;

		for (List<Token> rule : rules.get(nonTerminal)) {
			if (rule.size() == 0 || rule.size() == 1 && rule.get(0) instanceof Epsilon) return true;

			boolean empty = true;

			for (Token token : rule) {
				ArrayList<String> visited2 = new ArrayList<>(visited);
				visited2.add(nonTerminal);

				if (!canBeEmpty(token.getValue(), visited2)) {
					empty = false;
					break;
				}
			}

			if (empty) return true;
		}

		return false;
	}

	public Grammar copy() {
		Grammar grammar = null;

		try {
			grammar = new Grammar(tokenizer);
		} catch (SyntaxException e) {
			e.printStackTrace();
		}

		if (augmented) grammar.augment();

		return grammar;
	}
}
