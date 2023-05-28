package fr.senesi.g2t.fsm;

import java.util.ArrayList;
import java.util.List;

public class Rule {
	private String left;
	private List<String> right, context, parent;
	private int cursor;

	public Rule(String left, List<String> right, List<String> context, List<String> parent) {
		this.left = left;
		this.right = right;
		this.context = context;
		this.parent = parent;
		
		cursor = 0;
	}

	public Rule(String left, List<String> right, List<String> context) {
		this(left, right, context, new ArrayList<>());

		parent.add("$");
	}

	public Rule(String left, List<String> right) {
		this(left, right, new ArrayList<>());

		context.add("$");
	}

	public String getLeft() {
		return left;
	}

	public List<String> getRight() {
		return right;
	}

	public List<String> getContext() {
		return context;
	}

	public List<String> getParent() {
		return parent;
	}

	public int getCursor() {
		return cursor;
	}

	public void shift() {
		cursor++;
	}

	public boolean isFinished() {
		return cursor >= right.size();
	}

	public String peek() {
		return peek(0);
	}

	public String peek(int offset) {
		if (cursor + offset >= right.size()) return "$";

		return right.get(cursor + offset);
	}

	public String toString() {
		String res = left + " ->";

		for (int i = 0; i < right.size(); i++) {
			res += " " + (cursor == i ? "." : "") + right.get(i);
		}

		if (cursor == right.size()) res += ".";

		res += " [" + String.join(",", context) + "]";

		return res;
	}

	public boolean equals(Object o) {
		if (!(o instanceof Rule)) return false;

		Rule r = (Rule) o;

		return left.equals(r.left) && right.equals(r.right) && context.equals(r.context) && cursor == r.cursor;
	}

	public boolean coreEquals(Object o) {
		if (!(o instanceof Rule)) return false;

		Rule r = (Rule) o;

		return left.equals(r.left) && right.equals(r.right) && context.equals(r.context);
	}

	public Rule copy() {
		return new Rule(left, right, context);
	}
}