package fr.senesi.g2t.fsm;

import java.util.ArrayList;
import java.util.List;

public class Rule {
	private String left;
	private List<String> right, context, parent;
	private int cursor;

	public Rule(String left, List<String> right, List<String> context, List<String> parent) {
		this.left = left;
		this.right = right.size() == 1 && right.get(0).equals("^") ? new ArrayList<>() : right;
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

	public void addContext(List<String> context) {
		for (String c : context) {
			if (!this.context.contains(c)) this.context.add(c);
		}
	}

	public String toString() {
		String res = left + " ->";
		if (right.size() == 0) res += " ";

		for (int i = 0; i < right.size(); i++) {
			res += " " + (cursor == i ? "." : "") + right.get(i);
		}

		if (cursor == right.size()) res += ".";

		res += " [" + String.join(",", context) + "]";

		return res;
	}

	public String toStringFree() {
		String r = String.join(" ", right);
		if (r.equals("")) r = "^";

		return left + " -> " + r;
	}

	@Override
	public int hashCode() {
		int code = left.hashCode() * cursor;
		for (String s : right) code += s.hashCode();
		for (String s : context) code += s.hashCode();

		return code * right.size() * context.size();
	}

	public int coreHashCode() {
		int code = left.hashCode() * cursor;
		for (String s : right) code += s.hashCode();

		return code * right.size();
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Rule)) return false;

		Rule r = (Rule) o;

		return left.equals(r.left) && right.equals(r.right) && context.equals(r.context) && cursor == r.cursor;
	}

	public boolean coreEquals(Object o) {
		if (!(o instanceof Rule)) return false;

		Rule r = (Rule) o;

		return left.equals(r.left) && right.equals(r.right) && cursor == r.cursor;
	}

	public Rule copy() {
		Rule rule = new Rule(left, right, context, parent);
		rule.cursor = cursor;

		return rule;
	}
}
