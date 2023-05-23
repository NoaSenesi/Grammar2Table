package g2t;

import java.util.HashMap;
import java.util.Map;

public class Grammar {
	private static final String RESERVED = "=|;$";
	private String[] lines;
	private String terminals, nonTerminals;
	private Map<Character, String[]> rules;


	public Grammar(String file) {
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

	public Map<Character, String[]> getRules() {
		if (rules != null) return rules;

		rules = new HashMap<>();

		for (char nonTerminal : getNonTerminals().toCharArray()) {
			for (String line : lines) {
				char[] chars = line.toCharArray();

				if (chars[0] == nonTerminal) {
					String[] rule = line.split("=")[1].split(";")[0].split("\\|");
					rules.put(nonTerminal, rule);
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

		return getRules().get(nonTerminal);
	}

	public void printRules() {
		for (char c : getNonTerminals().toCharArray()) {
			String[] rules = getRules(c);

			for (String rule : rules) {
				System.out.println(c + " -> " + rule);
			}
		}
	}
}
