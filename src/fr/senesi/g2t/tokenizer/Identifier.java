package fr.senesi.g2t.tokenizer;

public class Identifier extends Token {
	private String value;

	public Identifier(int line, String value) {
		super(line);
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public String toString() {
		return "Identifier " + value + " at line " + getLine();
	}
}
