package fr.senesi.g2t;

public class Rule {
	private String left, right, context, parentRule;
	private int cursor;

	public Rule(String left, String right, String context, String parentRule) {
		this.left = left;
		this.right = right;
		this.context = context;
		this.parentRule = parentRule;

		if (context.length() == 0 || context == null) context = "$";

		cursor = 0;
	}

	public String getLeft() {
		return left;
	}

	public String getRight() {
		return right;
	}

	public int getCursor() {
		return cursor;
	}

	public String getContext() {
		return context;
	}

	public String peek() {
		return peek(0);
	}

	public String peek(int offset) {
		if (cursor + offset >= right.length()) return "$";

		return String.valueOf(right.charAt(cursor + offset));
	}

	public void shift() {
		cursor++;
	}

	public String getParentContextRule() {
		return parentRule;
	}

	public String toString() {
		String r = right.equals("^") ? right : right.substring(0, cursor) + "." + right.substring(cursor, right.length());

		return left + " -> " + r + " [" + String.join("|", context.split("")) + "]";
	}

	public String toStringFree() {
		return left + " -> " + right;
	}

	public boolean canReduce() {
		return cursor >= right.length() || right.equals("^");
	}

	public Rule copy() {
		Rule rule = new Rule(left, right, context, parentRule);
		rule.cursor = cursor;

		return rule;
	}

	public void addContext(String context) {
		for (String c : context.split("")) {
			if (!this.context.contains(c)) this.context += c;
		}
	}

	public boolean coreEquals(Rule rule) {
		return left.equals(rule.left) && right.equals(rule.right) && cursor == rule.cursor;
	}

	public boolean equals(Rule rule) {
		return coreEquals(rule) && context.equals(rule.context);
	}
}
