package g2t;

public class Rule {
	private String left, right, condition;
	private int cursor;

	public Rule(String left, String right, String condition) {
		this.left = left;
		this.right = right;
		this.condition = condition;

		if (condition.length() == 0 || condition == null) condition = "$";

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

	public String getCondition() {
		return condition;
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

	public String toString() {
		String r = right.equals("^") ? right : right.substring(0, cursor) + "." + right.substring(cursor, right.length());

		return left + " -> " + r + " [" + String.join(";", condition.split("")) + "]";
	}

	public Rule copy() {
		Rule rule = new Rule(left, right, condition);
		rule.cursor = cursor;

		return rule;
	}

	public void addCondition(String condition) {
		for (String c : condition.split("")) {
			if (!this.condition.contains(c)) this.condition += c;
		}
	}

	public boolean contextEquals(Rule rule) {
		return left.equals(rule.left) && right.equals(rule.right) && cursor == rule.cursor;
	}

	public boolean equals(Rule rule) {
		return contextEquals(rule) && condition.equals(rule.condition);
	}
}
