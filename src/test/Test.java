package test;

import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;

import bifstk.Bifstk;
import bifstk.Handler;
import bifstk.Root;
import bifstk.config.Config;
import bifstk.config.Theme;
import bifstk.gl.Color;
import bifstk.gl.Image;
import bifstk.gl.Util;
import bifstk.util.BifstkException;
import bifstk.util.Logger;
import bifstk.util.SharedFrameException;
import bifstk.wm.Area;
import bifstk.wm.Frame;
import bifstk.wm.Window;
import bifstk.wm.ui.AbstractButton;
import bifstk.wm.ui.Actionable;
import bifstk.wm.ui.Button;
import bifstk.wm.ui.Checkbox;
import bifstk.wm.ui.CustomBorder;
import bifstk.wm.ui.FlowBox;
import bifstk.wm.ui.Label;
import bifstk.wm.ui.Text;
import bifstk.wm.util.ConfigWindow;

public class Test implements Handler, Root {

	private int frameCount = 1;

	private Image bgImg = null;

	private int fps_acc = 0;
	private long dt = 0, dt2 = 0;

	private Label fpsLabel = null;

	@Override
	public void init() {
		Area fpsArea = new Area(5, 5, 80, 40);
		fpsLabel = new Label("FPS: ");
		fpsArea.setContent(fpsLabel);
		Bifstk.addArea(fpsArea);

		try {
			this.bgImg = new Image("gfx/art/bifstk_256.png");
		} catch (BifstkException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	@Override
	public void render() {

		// calculate framerate
		dt = Sys.getTime();
		if (dt - dt2 > 1000) {
			this.fpsLabel.setText("FPS: " + fps_acc);
			fps_acc = 0;
			dt2 = dt;
		} else {
			fps_acc++;
		}

		// render background image
		int w = this.bgImg.getTexWidth();
		int h = this.bgImg.getTexHeight();
		int dw = Display.getDisplayMode().getWidth();
		int dh = Display.getDisplayMode().getHeight();

		int imgX = (dw - w) / 2;
		int imgY = (dh - h) / 2;

		int[] v1 = {
				imgX, imgY, //
				imgX + w, imgY, //
				imgX + w, imgY + h, //
				imgX, imgY + h
		};
		Util.draw2DTexturedQuad(v1, Color.WHITE.toArray(4),
				this.bgImg.getTexId());
	}

	@Override
	public void keyEvent(int key, boolean state, char character) {
		switch (key) {

		case Keyboard.KEY_ESCAPE:
			Bifstk.stop();
			break;

		case Keyboard.KEY_C:
			if (state) {
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

				FlowBox h3 = new FlowBox(FlowBox.Orientation.HORIZONTAL);
				Checkbox c1 = new Checkbox(Config.get().isWmDebugLayout());
				c1.setAction("debugLayout");
				c1.setHandler(this);
				h3.addBefore(new Label("Debug layout"));
				h3.addAfter(c1);

				AbstractButton b3 = new Button("Message");
				b3.setAction("message");
				b3.setHandler(this);

				Text t1 = new Text();

				//vBox.addBefore(new CustomBorder(h1, 2));
				//vBox.addBefore(new CustomBorder(h2, 2));
				vBox.addBefore(new CustomBorder(h3, 2));
				vBox.addBefore(new CustomBorder(new Text(0), 2));
				vBox.setExpand(new CustomBorder(t1.getScrollBox(), 2));
				vBox.addAfter(b3);

				f.setContent(vBox);
				f.pack();
				Bifstk.addWindow(f);
			}
			break;

		case Keyboard.KEY_K:
			try {
				Bifstk.addWindow(ConfigWindow.getInstance());
			} catch (SharedFrameException e) {
			}
			break;

		case Keyboard.KEY_R:
			if (state) {
				try {
					Theme.load(Config.get().getThemePath());
				} catch (BifstkException e) {
					Logger.error("Could not reload config", e);
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
		} else if (command.equals("debugLayout")) {
			Config.get().setWmDebugLayout(((Checkbox) source).isChecked());
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
