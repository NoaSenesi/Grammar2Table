package fr.senesi.g2t;

public class State {
	/*
	private int id;
	private List<Rule> rules;
	private FSM parent;
	private Map<Character, Integer> shifts;

	public State(int id, List<Rule> rules, FSM parent) {
		this.id = id;
		this.rules = rules;
		this.parent = parent;
		shifts = new HashMap<>();
	}

	public int getId() {
		return id;
	}

	public void print() {
		int maxlen = 0;

		for (Rule r : rules) {
			if (r.toString().length() > maxlen) maxlen = r.toString().length();
		}

		String s = "State: I" + id;

		if (s.length() > maxlen) maxlen = s.length();

		System.out.print("┌");
		for (int i = 0; i < maxlen+2; i++) System.out.print("─");
		System.out.println("┐");

		System.out.print("│ " + s);
		for (int i = 0; i < maxlen - s.length(); i++) System.out.print(" ");
		System.out.println(" │");

		System.out.print("├");
		for (int i = 0; i < maxlen+2; i++) System.out.print("─");
		System.out.println("┤");

		for (Rule r : rules) {
			System.out.print("│ " + r);
			for (int i = 0; i < maxlen - r.toString().length(); i++) System.out.print(" ");
			System.out.println(" │");
		}

		System.out.print("└");
		for (int i = 0; i < maxlen+2; i++) System.out.print("─");
		System.out.println("┘");
	}

	public List<Rule> getRules() {
		return rules;
	}

	public State shift(char c) {
		if (shifts.containsKey(c)) return parent.getStates().get(shifts.get(c));

		if (c == '$') return null;

		List<Rule> newRules = new ArrayList<>();

		for (Rule rule : rules) {
			if (rule.peek().equals(String.valueOf(c))) {
				Rule nr = rule.copy();
				nr.shift();
				newRules.add(nr);
			}
		}

		if (newRules.size() == 0) return null;

		State state = parent.getOrCreateStateFromRules(newRules);

		shifts.put(c, state.getId());

		return state;
	}

	public boolean equals(State state) {
		if (rules.size() != state.rules.size()) return false;

		for (Rule rule : rules) {
			boolean none = true;

			for (Rule srule : state.getRules()) {
				if (rule.equals(srule)) none = false;
			}

			if (none) return false;
		}

		return true;
	}

	public boolean coreEquals(State state) {
		if (rules.size() != state.rules.size()) return false;

		for (Rule rule : rules) {
			boolean none = true;

			for (Rule srule : state.getRules()) {
				if (rule.coreEquals(srule)) none = false;
			}

			if (none) return false;
		}

		return true;
	}

	public void factorize() {
		List<Rule> factorized = new ArrayList<>();

		for (Rule rule : rules) {
			boolean in = false;

			for (Rule frule : factorized) {
				if (rule.coreEquals(frule)) {
					frule.addContext(rule.getContext());
					in = true;
					break;
				}
			}

			if (!in) factorized.add(rule.copy());
		}

		rules = factorized;
	}
	*/
}