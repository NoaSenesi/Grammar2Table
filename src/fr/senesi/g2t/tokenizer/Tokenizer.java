package fr.senesi.g2t.tokenizer;

import java.util.ArrayList;
import java.util.List;

import fr.senesi.g2t.exception.TokenizationException;

public class Tokenizer {
	private List<Token> tokens;
	private String stream;

	public Tokenizer(String stream) throws TokenizationException {
		tokens = new ArrayList<>();
		this.stream = stream;

		tokenize();
	}

	public List<Token> getTokens() {
		return tokens;
	}

	private void tokenize() throws TokenizationException {
		int cursor = 0;
		int line = 1;

		while (cursor < stream.length()) {
			char c = stream.charAt(cursor);

			if (c == '#') {
				while (c != '\n' && cursor < stream.length()) {
					cursor++;
					if (cursor < stream.length()) c = stream.charAt(cursor);
				}
			} else if (c == '/') {
				if (cursor + 1 < stream.length()) {
					char c2 = stream.charAt(cursor + 1);

					if (c2 == '/') {
						while (c != '\n' && cursor < stream.length()) {
							cursor++;
							if (cursor < stream.length()) c = stream.charAt(cursor);
						}
					} else if (c2 == '*') {
						cursor++;

						while ((c != '*' || c2 != '/') && cursor < stream.length()) {
							cursor++;

							if (cursor + 1 < stream.length()) {
								c = c2;
								c2 = stream.charAt(cursor);
							}

							if (c == '\n') line++;
						}

						cursor++;
					}
				}
			}

			c = stream.charAt(cursor);

			if (c == '\n') {
				line++;
			} else if (c == '|') {
				tokens.add(new Separator(line));
			} else if (c == '=') {
				tokens.add(new Assign(line));
			} else if (String.valueOf(c).matches("[A-Za-z0-9_]")) {
				int start = cursor;

				while (String.valueOf(c).matches("[A-Za-z0-9_]") && cursor < stream.length()) {
					cursor++;
					if (cursor < stream.length()) c = stream.charAt(cursor);
				}

				tokens.add(new Identifier(line, stream.substring(start, cursor)));
				cursor--;
			} else if (c == '^') {
				tokens.add(new Epsilon(line));
			} else if (c == ';') {
				tokens.add(new SemiColon(line));
			} else if (c == '\\') {
				if (cursor + 1 < stream.length()) {
					char c2 = stream.charAt(cursor + 1);

					if (c2 == '\\' || c2 == '|' || c2 == '=' || c2 == '^' || c2 == ';') {
						tokens.add(new Value(line, String.valueOf(c2)));
						cursor++;
					} else {
						throw new TokenizationException("Invalid character after \\ at line " + line);
					}
				}
			} else if (c == ' ' || c == '\t' || c == '\r') {

			} else if (c == '$') {
				throw new TokenizationException("Invalid character " + c + " at line " + line);
			} else {
				tokens.add(new Value(line, String.valueOf(c)));
			}

			cursor++;
		}

		tokens.add(new EOF(line));
	}
}
