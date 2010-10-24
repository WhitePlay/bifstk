package test;

import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

import bifstk.Bifstk;
import bifstk.Handler;
import bifstk.Root;
import bifstk.gl.Color;
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
		} catch (SlickException e) {
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
		float w = this.bgImg.getTexture().getTextureWidth();
		float h = this.bgImg.getTexture().getTextureHeight();
		int dw = Display.getDisplayMode().getWidth();
		int dh = Display.getDisplayMode().getHeight();

		float imgX = (dw - w) / 2;
		float imgY = (dh - h) / 2;

		GL11.glEnable(GL11.GL_TEXTURE_2D);
		Color.WHITE.use();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.bgImg.getTexture()
				.getTextureID());
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glTexCoord2f(0.0f, 0.0f);
		GL11.glVertex2f(imgX, imgY);
		GL11.glTexCoord2f(0.0f, 1.0f);
		GL11.glVertex2f(imgX, imgY + h);
		GL11.glTexCoord2f(1.0f, 1.0f);
		GL11.glVertex2f(imgX + w, imgY + h);
		GL11.glTexCoord2f(1.0f, 0.0f);
		GL11.glVertex2f(imgX + w, imgY);
		GL11.glEnd();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
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
