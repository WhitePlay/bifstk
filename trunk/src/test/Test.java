package test;

import org.lwjgl.input.Keyboard;

import bifstk.Bifstk;
import bifstk.BifstkException;
import bifstk.Handler;
import bifstk.Root;
import bifstk.gl.Color;
import bifstk.wm.Frame;
import bifstk.wm.ui.Box;
import bifstk.wm.ui.CustomBorder;
import bifstk.wm.ui.Label;
import bifstk.wm.ui.TitleBorder;

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
				Box b1 = new Box(Box.Orientation.HORIZONTAL);
				Box b2 = new Box(Box.Orientation.VERTICAL);
				Box b3 = new Box(Box.Orientation.VERTICAL);

				b1.addChild(b2);
				b1.addChild(new TitleBorder(b3, "Foo"));

				Label l1 = new Label("one");
				Label l2 = new Label("two");
				Label l3 = new Label("three");

				b2.addChild(l1);
				b2.addChild(l2);
				b3.addChild(new TitleBorder(l3, "Haha :)"));
				b3.addChild(new CustomBorder(l2, 10, Color.RED));

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
