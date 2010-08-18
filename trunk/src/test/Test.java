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
import bifstk.wm.ui.GridBox;
import bifstk.wm.ui.GridBox.Orientation;
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

				FlowBox b1 = new FlowBox();

				Widget w1 = new Label("wéééééé1");
				Widget wc = new Label("center");
				Widget w2 = new Label("w2");

				b1.addLeft(new CustomBorder(w1, 3, Color.RED));
				b1.setExpand(new CustomBorder(wc, 3, Color.GREEN));
				b1.addRight(new CustomBorder(w2, 3, Color.BLUE));

				f.setContent(b1);

				final Frame f2 = new Frame(200, 200);

				GridBox b2 = new GridBox(Orientation.HORIZONTAL);
				GridBox b3 = new GridBox(Orientation.VERTICAL);
				b2.addChild(new CustomBorder(new Label("foo"), 3, Color.RED));
				b3.addChild(new Label("lol"));
				b3.addChild(new CustomBorder(new Label("dongs"), 1, Color.GREEN));
				b2.addChild(b3);
				f2.setContent(b2);

				f.pack();
				f2.pack();

				try {
					Bifstk.addFrame(f);
					Bifstk.addFrame(f2);
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
