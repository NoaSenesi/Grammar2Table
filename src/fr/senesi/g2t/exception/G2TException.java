package fr.senesi.g2t.exception;

public class G2TException extends Exception {
	public G2TException(String message) {
		super(message);
	}

	public void printStackTrace() {
		System.out.println(getClass().getSimpleName() + ": " + getMessage());
		System.exit(1);
	}
}
