package test;

import org.lwjgl.input.Keyboard;

import bifstk.Bifstk;
import bifstk.BifstkException;
import bifstk.Handler;
import bifstk.Root;
import bifstk.wm.Frame;
import bifstk.wm.ui.Box;
import bifstk.wm.ui.Box.Orientation;
import bifstk.wm.ui.Label;

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

				Box b1 = new Box(Orientation.VERTICAL);
				Box b2 = new Box(Orientation.HORIZONTAL);

				b1.addChild(new Label("This is"));

				b2.addChild(new Label("some"));
				b2.addChild(new Label("text."));

				b1.addChild(b2);

				f.setContent(b1);

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
