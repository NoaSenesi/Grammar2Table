package g2t;

import java.util.List;

public class State {
	private int id;
	private List<Rule> rules;

	public State(int id, List<Rule> rules) {
		this.id = id;
		this.rules = rules;
	}

	public void print() {
		int maxlen = 0;

		for (Rule r : rules) {
			if (r.toString().length() > maxlen) maxlen = r.toString().length();
		}

		String s = "State: I" + id;

		if (s.length() > maxlen) maxlen = s.length();

		System.out.print("|");
		for (int i = 0; i < maxlen+2; i++) System.out.print("-");
		System.out.println("|");

		System.out.print("| " + s);
		for (int i = 0; i < maxlen - s.length(); i++) System.out.print(" ");
		System.out.println(" |");

		System.out.print("|");
		for (int i = 0; i < maxlen+2; i++) System.out.print("-");
		System.out.println("|");

		for (Rule r : rules) {
			System.out.print("| " + r);
			for (int i = 0; i < maxlen - r.toString().length(); i++) System.out.print(" ");
			System.out.println(" |");
		}

		System.out.print("|");
		for (int i = 0; i < maxlen+2; i++) System.out.print("-");
		System.out.println("|");

		System.out.println();
	}
}