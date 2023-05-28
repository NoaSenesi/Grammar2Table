package fr.senesi.g2t.tokenizer;

public final class EOF extends Token {
	public EOF(int line) {
		super(line);
	}

	public String getValue() {
		return "EOF";
	}

	public String toString() {
		return "EOF $ at line " + this.getLine();
	}
}
