package test;

import org.lwjgl.input.Keyboard;

import bifstk.Bifstk;
import bifstk.Handler;
import bifstk.Root;
import bifstk.gl.Color;
import bifstk.wm.Frame;
import bifstk.wm.ui.Actionable;
import bifstk.wm.ui.CustomBorder;
import bifstk.wm.ui.Label;

public class Test implements Handler, Root {

	private int frameCount = 1;
	private int modalCount = 1;

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

		case Keyboard.KEY_M:
			if (Keyboard.getEventKeyState()) {
				Frame f = new Frame(300, 240, 100, 70);
				f.setContent(new Label("Boo!"));
				f.setResizable(false);
				// f.setTitlebar(false);
				Bifstk.setModalFrame(f);
			}
			break;
		case Keyboard.KEY_C:
			if (Keyboard.getEventKeyState()) {
				Frame f = new Frame(100, 100);
				f.setTitle("Frame #" + frameCount++);
				Bifstk.addFrame(f);
			}
			break;

		}
	}

	@Override
	public void mouseEvent(int button, int x, int y, boolean state) {
	}

	@Override
	public void actionPerformed(String command, Actionable source) {

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
