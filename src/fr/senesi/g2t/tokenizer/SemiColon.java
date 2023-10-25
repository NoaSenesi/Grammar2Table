package fr.senesi.g2t.tokenizer;

public final class SemiColon extends Token {
	public SemiColon(int line) {
		super(line);
	}

	public String getValue() {
		return ";";
	}

	public String toString() {
		return "SemiColon ; at line " + getLine();
	}
}
