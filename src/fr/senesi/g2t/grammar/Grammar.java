package fr.senesi.g2t.grammar;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import fr.senesi.g2t.exception.SyntaxException;
import fr.senesi.g2t.tokenizer.Assign;
import fr.senesi.g2t.tokenizer.EOF;
import fr.senesi.g2t.tokenizer.Identifier;
import fr.senesi.g2t.tokenizer.Ruleable;
import fr.senesi.g2t.tokenizer.SemiColon;
import fr.senesi.g2t.tokenizer.Separator;
import fr.senesi.g2t.tokenizer.Token;
import fr.senesi.g2t.tokenizer.Tokenizer;
import fr.senesi.g2t.tokenizer.Value;

public class Grammar {
	/*private static final String RESERVED = "=|;$";
	private String[] lines;
	private String terminals, nonTerminals, file;
	private Map<String, String[]> rules;*/
	private Map<String, List<List<Token>>> rules;
	private Tokenizer tokenizer;
	private List<String> terminals, nonTerminals;

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
					if (!terminals.contains(t.getValue()) && t instanceof Value) terminals.add(t.getValue());
					current.add(t);
				}

				else throw new SyntaxException("Unexpected " + t.getClass().getSimpleName() + " token at line " + t.getLine());

			}

			i = cursor;
		}

		for (String nt : nonTerminals) {
			if (terminals.contains(nt)) terminals.remove(nt);
		}
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

	/*public Grammar(String file) {
		this.file = file;

		lines = Reader.getLines(file);
		lines = Reader.cleanLines(lines);

		for (String line : lines) {
			if (!line.matches("^[^=;\\|]=[^=;\\|]+(\\|[^=;\\|]+)*;$")) {
				System.out.println("Error: Invalid rule: " + line);
				System.exit(1);
			}
		}
	}*/

	/*public String[] getLines() {
		return lines;
	}*/

	/*public String getNonTerminals() {
		String ntstring = "";

		if (nonTerminals != null) return nonTerminals;

		for (String s : lines) {
			char[] chars = s.toCharArray();

			if (ntstring.indexOf(chars[0]) == -1) {
				ntstring += chars[0];

				if (RESERVED.indexOf(chars[0]) != -1 || chars[0] == '^') {
					System.out.println("Error: " + chars[0] + " is a reserved character.");
					System.exit(1);
				}
			}
		}

		nonTerminals = ntstring;

		return nonTerminals;
	}*/

	/*public String getTerminals() {
		String tstring = "";

		if (terminals != null) return terminals;

		for (String s : lines) {
			char[] chars = s.toCharArray();

			for (int i = 0; i < chars.length; i++) {
				if (RESERVED.indexOf(chars[i]) == -1 && getNonTerminals().indexOf(chars[i]) == -1) {
					tstring += chars[i];
				}
			}
		}

		String unique = "";

		for (int i = 0; i < tstring.length(); i++) {
			if (unique.indexOf(tstring.charAt(i)) == -1) unique += tstring.charAt(i);
		}

		terminals = unique;

		return terminals;
	}*/

	/*public String getAxiom() {
		String s = String.valueOf(getNonTerminals().charAt(0));

		if (getRules().keySet().contains(s + "'")) return s + "'";

		return s;
	}*/

	/*public Map<String, String[]> getRules() {
		if (rules != null) return rules;

		rules = new HashMap<>();

		for (char nonTerminal : getNonTerminals().toCharArray()) {
			for (String line : lines) {
				char[] chars = line.toCharArray();

				if (chars[0] == nonTerminal) {
					String[] rule = line.split("=")[1].split(";")[0].split("\\|");
					rules.put(String.valueOf(nonTerminal), rule);
				}
			}
		}

		return rules;
	}*/

	/*public String[] getRules(String nonTerminal) {
		if (getNonTerminals().indexOf(nonTerminal) == -1) {
			System.out.println("Warning: " + nonTerminal + " is not a non-terminal.");
			return null;
		}

		return getRules().get(nonTerminal);
	}*/

	/*public void printRules() {
		if (rules.containsKey(getAxiom() + "'")) System.out.println(getAxiom() + "' -> " + getAxiom());

		for (char c : getNonTerminals().toCharArray()) {
			String[] rules = getRules(String.valueOf(c));

			for (String rule : rules) {
				System.out.println(c + " -> " + rule);
			}
		}
	}*/

	/*public String firsts(String nonTerminal) {
		String firsts = firsts(nonTerminal, "");
		if (canBeEmpty(nonTerminal)) firsts += "$";

		return firsts.replace("^", "");
	}*/

	/*private String firsts(String nonTerminal, String visited) {
		if (visited.indexOf(nonTerminal) != -1) {
			return "";
		}

		if (getNonTerminals().indexOf(nonTerminal) == -1) {
			return nonTerminal;
		}

		String firsts = "";

		for (String rule : getRules(String.valueOf(nonTerminal))) {
			char[] chars = rule.toCharArray();

			int i = 0;

			while (i < chars.length) {
				if (getNonTerminals().indexOf(chars[i]) != -1) {
					if (firsts.indexOf(chars[i]) == -1) firsts += firsts(String.valueOf(chars[i]), visited + nonTerminal);
					if (!canBeEmpty(String.valueOf(chars[i]))) break;
				} else {
					if (firsts.indexOf(chars[i]) == -1) firsts += chars[i];
					break;
				}

				i++;
			}
		}

		return firsts;
	}*/

	/*public String firstsRule(String rule) {
		String firsts = "";

		char[] chars = rule.toCharArray();

		int i = 0;

		while (i < chars.length) {
			firsts = firsts.replace("$", "");

			if (getNonTerminals().indexOf(chars[i]) != -1) {
				if (firsts.indexOf(chars[i]) == -1) firsts += firsts(String.valueOf(chars[i]));
				if (!canBeEmpty(String.valueOf(chars[i]))) break;
			} else {
				if (firsts.indexOf(chars[i]) == -1) firsts += chars[i];
				break;
			}

			i++;
		}

		String unique = "";

		for (char c : firsts.toCharArray()) if (unique.indexOf(c) == -1) unique += c;

		return unique;
	}*/

	/*public boolean canBeEmpty(String nonTerminal) {
		return canBeEmpty(nonTerminal, "");
	}*/

	/*private boolean canBeEmpty(String nonTerminal, String visited) {
		if (visited.indexOf(nonTerminal) != -1) return false;

		if (getNonTerminals().indexOf(nonTerminal) == -1) return false;

		for (String line : getRules(nonTerminal)) {
			if (line.equals("^")) return true;

			char[] chars = line.toCharArray();
			boolean empty = true;

			for (int i = 0; i < chars.length; i++) {
				if (getTerminals().indexOf(chars[i]) != -1) {
					empty = false;
					break;
				}
			}

			if (empty) {
				for (int i = 0; i < chars.length; i++) {
					if (getNonTerminals().indexOf(chars[i]) != -1) {
						if (!canBeEmpty(String.valueOf(chars[i]), visited + nonTerminal)) {
							empty = false;
							break;
						}
					}
				}
			}

			if (empty) return true;
		}

		return false;
	}*/

	/*public Grammar copy() {
		Grammar g = new Grammar(file);

		return g;
	}*/
}
