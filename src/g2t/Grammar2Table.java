package g2t;

public class Grammar2Table {
	public static void main(String[] args) {
		if (args.length != 1) {
			System.out.println("Usage: java g2t.Grammar2Table <file>");
			System.exit(1);
		}

		Grammar g = new Grammar(args[0]);
		FSM fsm = new FSM(g);

		State f = fsm.getFirstState();

		f.print();
		f.shift('S').print();
		f.shift('n').print();
	}
}