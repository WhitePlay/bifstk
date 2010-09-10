package test;

import org.lwjgl.input.Keyboard;

import bifstk.Bifstk;
import bifstk.Handler;
import bifstk.Root;
import bifstk.wm.Area;
import bifstk.wm.Frame;
import bifstk.wm.Window;
import bifstk.wm.ui.Actionable;
import bifstk.wm.ui.Button;
import bifstk.wm.ui.CustomBorder;
import bifstk.wm.ui.FlowBox;
import bifstk.wm.ui.Label;

public class Test implements Handler, Root {

	private int frameCount = 1;

	@Override
	public void init() {
		Area a = new Area(20, 20, 100, 60);
		Button b1 = new Button("Area");
		b1.setAction("area");
		b1.setHandler(this);
		a.setContent(b1);
		Bifstk.addArea(a);
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
				Window f = new Window(100, 100);
				f.setTitle("Frame #" + frameCount++);

				FlowBox vBox = new FlowBox(FlowBox.Orientation.VERTICAL);

				FlowBox h1 = new FlowBox(FlowBox.Orientation.HORIZONTAL);
				Button b1 = new Button("toggle");
				b1.setAction("resize");
				b1.setHandler(this);
				h1.addBefore(new Label("Resizable"));
				h1.addAfter(b1);

				FlowBox h2 = new FlowBox(FlowBox.Orientation.HORIZONTAL);
				Button b2 = new Button("toggle");
				b2.setAction("title");
				b2.setHandler(this);
				h2.addBefore(new Label("Titlebar"));
				h2.addAfter(b2);

				Button b3 = new Button("Message");
				b3.setAction("message");
				b3.setHandler(this);

				vBox.addBefore(new CustomBorder(h1, 2));
				vBox.addBefore(new CustomBorder(h2, 2));
				vBox.addAfter(b3);

				f.setContent(vBox);

				Bifstk.addWindow(f);
			}
			break;

		}
	}

	@Override
	public void mouseEvent(int button, int x, int y, boolean state) {
	}

	@Override
	public void actionPerformed(String command, Actionable source) {
		if (command.equals("resize")) {
			Frame f = source.getFrame();
			f.setResizable(!f.isResizable());
		} else if (command.equals("title")) {
			Frame f = source.getFrame();
			f.setTitlebar(!f.hasTitlebar());
		} else if (command.equals("message")) {
			Window f = new Window(300, 240, 100, 70);
			f.setContent(new Label("Boo!"));
			f.setResizable(false);
			// f.setTitlebar(false);
			Bifstk.setModalWindow(f);
		} else if (command.equals("area")) {
			Window f = new Window(300, 240, 100, 70);
			f.setContent(new Label("area <3"));
			f.setResizable(false);
			// f.setTitlebar(false);
			Bifstk.setModalWindow(f);
		}
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
