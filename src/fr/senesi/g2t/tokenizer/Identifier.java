package fr.senesi.g2t.tokenizer;

public final class Identifier extends Value {
	private String value;

	public Identifier(int line, String value) {
		super(line, value);
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public String toString() {
		return "Identifier " + value + " at line " + getLine();
	}
}
