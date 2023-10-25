package fr.senesi.g2t.tokenizer;

public abstract class Token {
	private int line;

	public Token(int line) {
		this.line = line;
	}

	public int getLine() {
		return line;
	}

	public abstract String getValue();
}
