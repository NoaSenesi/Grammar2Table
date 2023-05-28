package fr.senesi.g2t;

import fr.senesi.g2t.exception.SyntaxException;
import fr.senesi.g2t.exception.TokenizationException;
import fr.senesi.g2t.grammar.Grammar;
import fr.senesi.g2t.tokenizer.Tokenizer;

public class Grammar2Table {
	public static final String VERSION = "2.0.0";

	public static void main(String[] args) {
		if (args.length != 1) {
			System.out.println("Grammar2Table v" + VERSION);
			System.out.println("Please input a file name");
			System.exit(0);
		}

		String[] read = Reader.getLines(args[0]);

		Tokenizer tokenizer = null;

		try {
			tokenizer = new Tokenizer(String.join("\n", read));
		} catch (TokenizationException e) {
			e.printStackTrace();
		}

		if (tokenizer == null) System.exit(1);

		Grammar grammar = null;

		try {
			grammar = new Grammar(tokenizer);
		} catch (SyntaxException e) {
			e.printStackTrace();
		}

		if (grammar == null) System.exit(1);

		System.out.println(grammar.getTerminals());
		System.out.println(grammar.getNonTerminals());
		grammar.print();

		//Grammar g = new Grammar(args[0]);


		/*FSM fsm = new FSM(g);
		fsm.createAllStates();

		Table table = new Table(fsm);
		table.saveCSV(args[0] + ".csv");
		table.save(args[0] + ".txt");

		for (State s : fsm.getStates()) s.print();*/
	}
}