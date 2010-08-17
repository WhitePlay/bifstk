package test;

import org.lwjgl.input.Keyboard;

import bifstk.Bifstk;
import bifstk.BifstkException;
import bifstk.Handler;
import bifstk.Root;
import bifstk.wm.Frame;

public class Test implements Handler, Root {

	@Override
	public void init() {

	}

	@Override
	public void render() {

	}

	@Override
	public void keyEvent(int key, boolean state, char character) {
		switch (key) {

		case Keyboard.KEY_ESCAPE:
			Bifstk.stop();
			break;

		case Keyboard.KEY_C:
			if (Keyboard.getEventKeyState()) {
				final Frame f = new Frame(50, 50);

				try {
					Bifstk.addFrame(f);
				} catch (BifstkException e) {
					e.printStackTrace();
				}
			}
			break;

		}
	}

	@Override
	public void mouseEvent(int button, int x, int y, boolean state) {

	}

	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println("Usage: java Test config");
			System.exit(1);
		}

		Test test = new Test();
		Bifstk.start(args[0], test, test);
	}

}
