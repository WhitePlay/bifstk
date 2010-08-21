package test;

import org.lwjgl.input.Keyboard;

import bifstk.Bifstk;
import bifstk.BifstkException;
import bifstk.Handler;
import bifstk.Root;
import bifstk.gl.Color;
import bifstk.wm.Frame;
import bifstk.wm.ui.Actionable;
import bifstk.wm.ui.Button;
import bifstk.wm.ui.CustomBorder;
import bifstk.wm.ui.FlowBox;
import bifstk.wm.ui.FlowBox.Orientation;
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

				FlowBox b1 = new FlowBox(Orientation.VERTICAL);

				FlowBox b2 = new FlowBox(Orientation.HORIZONTAL);
				b2.addBefore(new Label("Foo"));
				b2.addAfter(new Button("Bar"));

				Button b = new Button("Gee");
				b.setHandler(this);
				b.setAction("bouton");

				FlowBox b3 = new FlowBox(Orientation.HORIZONTAL);
				b3.addBefore(new Label("Baz"));
				b3.addAfter(new CustomBorder(b, 10, Color.GRAY));

				b1.addBefore(b2);
				b1.addAfter(b3);

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

	@Override
	public void actionPerformed(String command, Actionable source) {
		System.out.println(command + " " + source);
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
