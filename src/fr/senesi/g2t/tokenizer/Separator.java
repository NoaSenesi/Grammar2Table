package fr.senesi.g2t.tokenizer;

public class Separator extends Token {
	public Separator(int line) {
		super(line);
	}

	public String getValue() {
		return "|";
	}

	public String toString() {
		return "Separator | at line " + this.getLine();
	}
}
