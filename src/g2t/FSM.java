package g2t;

public class FSM {
	private Grammar augmentedGrammar;

	public FSM(Grammar g) {
		augmentedGrammar = g.copy();
		augmentedGrammar.getRules().put(augmentedGrammar.getAxiom() + "'", new String[] {String.valueOf(augmentedGrammar.getAxiom())});
	}

	public Grammar getAugmentedGrammar() {
		return augmentedGrammar;
	}
}
