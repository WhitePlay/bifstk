package test;

import org.lwjgl.input.Keyboard;

import bifstk.Bifstk;
import bifstk.BifstkException;
import bifstk.Handler;
import bifstk.Root;
import bifstk.gl.Color;
import bifstk.wm.Frame;
import bifstk.wm.ui.CustomBorder;
import bifstk.wm.ui.FlowBox;
import bifstk.wm.ui.FlowBox.Orientation;
import bifstk.wm.ui.Label;
import bifstk.wm.ui.Widget;

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

				FlowBox b1 = new FlowBox(Orientation.HORIZONTAL);

				Widget w1 = new Label("wéééééé1");
				Widget wc = new Label("center");
				Widget w2 = new Label("w2");

				b1.addBefore(new CustomBorder(w1, 30, Color.GRAY));
				b1.setExpand(new CustomBorder(wc, 10, Color.GREEN));
				b1.addAfter(new CustomBorder(w2, 30, Color.BLUE));

				f.setContent(b1);

				f.pack();

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
