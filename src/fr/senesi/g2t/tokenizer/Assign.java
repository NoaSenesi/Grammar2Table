package fr.senesi.g2t.tokenizer;

public class Assign extends Token {
	public Assign(int line) {
		super(line);
	}

	public String getValue() {
		return "=";
	}

	public String toString() {
		return "Assign = at line " + this.getLine();
	}
}
