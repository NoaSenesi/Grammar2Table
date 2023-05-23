package g2t;

import java.util.HashMap;
import java.util.Map;

public class Grammar {
	private static final String RESERVED = "=|;$";
	private String[] lines;
	private String terminals, nonTerminals, file;
	private Map<String, String[]> rules;


	public Grammar(String file) {
		this.file = file;

		lines = Reader.getLines(file);
		lines = Reader.cleanLines(lines);

		for (String line : lines) {
			if (!line.matches("^[^=;\\|]=[^=;\\|]+(\\|[^=;\\|]+)*;$")) {
				System.out.println("Error: Invalid rule: " + line);
				System.exit(1);
			}
		}
	}

	public String[] getLines() {
		return lines;
	}

	public String getNonTerminals() {
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
	}

	public String getTerminals() {
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

		terminals = tstring;

		return terminals;
	}

	public char getAxiom() {
		return getNonTerminals().charAt(0);
	}

	public Map<String, String[]> getRules() {
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
	}

	public String[] getRules(char nonTerminal) {
		if (getNonTerminals().indexOf(nonTerminal) == -1) {
			System.out.println("Warning: " + nonTerminal + " is not a non-terminal.");
			return null;
		}

		return getRules().get(String.valueOf(nonTerminal));
	}

	public void printRules() {
		if (rules.containsKey(getAxiom() + "'")) System.out.println(getAxiom() + "' -> " + getAxiom());
		
		for (char c : getNonTerminals().toCharArray()) {
			String[] rules = getRules(c);

			for (String rule : rules) {
				System.out.println(c + " -> " + rule);
			}
		}
	}

	public String firsts(char nonTerminal) {
		String firsts = firsts(nonTerminal, "");
		if (canBeEmpty(nonTerminal)) firsts += "$";

		return firsts.replace("^", "");
	}

	private String firsts(char nonTerminal, String visited) {
		if (visited.indexOf(nonTerminal) != -1) {
			return "";
		}

		if (getNonTerminals().indexOf(nonTerminal) == -1) {
			System.out.println("Warning: " + nonTerminal + " is not a non-terminal.");
			return null;
		}

		String firsts = "";

		for (String rule : getRules(nonTerminal)) {
			char[] chars = rule.toCharArray();

			int i = 0;

			while (i < chars.length) {
				if (getNonTerminals().indexOf(chars[i]) != -1) {
					if (firsts.indexOf(chars[i]) == -1) firsts += firsts(chars[i], visited + nonTerminal);
					if (!canBeEmpty(chars[i])) break;
				} else {
					if (firsts.indexOf(chars[i]) == -1) firsts += chars[i];
					break;
				}

				i++;
			}
		}

		return firsts;
	}

	public boolean canBeEmpty(char nonTerminal) {
		return canBeEmpty(nonTerminal, "");
	}

	private boolean canBeEmpty(char nonTerminal, String visited) {
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
						if (!canBeEmpty(chars[i], visited + nonTerminal)) {
							empty = false;
							break;
						}
					}
				}
			}

			if (empty) return true;
		}

		return false;
	}

	public Grammar copy() {
		Grammar g = new Grammar(file);

		return g;
	}
}
