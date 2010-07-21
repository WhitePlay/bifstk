package bifstk;

public class Test {

	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println("Usage: java Test config");
			System.exit(1);
		}

		Bifstk.start(args[0]);
	}
}
