package fr.senesi.g2t;

public class Grammar2Table {
	public static final String VERSION = "1.0.0";

	public static void main(String[] args) {
		if (args.length != 1) {
			System.out.println("Grammar2Table v" + VERSION);
			System.out.println("Please input a file name");
			System.exit(0);
		}

		Grammar g = new Grammar(args[0]);

		FSM fsm = new FSM(g);
		fsm.createAllStates();

		Table table = new Table(fsm);
		table.saveCSV(args[0] + ".csv");
		table.save(args[0] + ".txt");

		for (State s : fsm.getStates()) s.print();
	}
}