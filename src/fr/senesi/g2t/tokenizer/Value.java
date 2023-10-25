package fr.senesi.g2t.tokenizer;

public class Value extends Ruleable {
	private String value;

	public Value(int line, String value) {
		super(line);
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public String toString() {
		return "Value " + value + " at line " + getLine();
	}
}
