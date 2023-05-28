package fr.senesi.g2t.tokenizer;

public class Epsilon extends Token {
	public Epsilon(int line) {
		super(line);
	}

	public String getValue() {
		return "^";
	}

	public String toString() {
		return "Epsilon ^ at line " + this.getLine();
	}
}
