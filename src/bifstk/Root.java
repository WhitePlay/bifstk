package bifstk;

import java.util.Iterator;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

import bifstk.type.Drawable;


public class Root {

	private State state;

	public Root(State state) throws BifstkException {

		this.state = state;

		int width = 800;
		int height = 600;
		String title = "Foo";

		DisplayMode[] modes = null;
		try {
			modes = Display.getAvailableDisplayModes();
		} catch (LWJGLException e) {
			throw new BifstkException("", e);
		}
		DisplayMode mode = null;
		for (int i = 0; i < modes.length; i++) {
			if ((modes[i].getWidth() == width)
					&& (modes[i].getHeight() == height)) {
				mode = modes[i];
				break;
			}
		}

		if (mode == null) {
			throw new BifstkException(
					"Could not find DisplayMode for resolution " + width + "*"
							+ height);
		}

		try {
			Display.setDisplayMode(mode);
		} catch (LWJGLException e) {
			throw new BifstkException(e);
		}
		Display.setTitle(title);

		try {
			Display.create();
		} catch (LWJGLException e) {
			throw new BifstkException(e);
		}
	}

	public void render() {
		DisplayMode mode = Display.getDisplayMode();
		int width = mode.getWidth();
		int height = mode.getHeight();

		this.initRender(width, height);

		/*
		 * draw all frames and windows
		 */
		Drawable f = null;
		// reverse iteration : frames are stacked with the head of the list
		// being the focused one
		Iterator<Drawable> it = this.state.getFrames().descendingIterator();
		while (it.hasNext()) {
			f = it.next();
			f.render();
		}
	}

	private void initRender(int width, int height) {
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		// 0,0 is the bottom-left corner, like LWJGL's mouse coordinates
		GL11.glOrtho(0.0d, width, 0.0d, height, -1.0d, 1.0d);

		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();
		GL11.glViewport(0, 0, width, height);

		GL11.glDisable(GL11.GL_DEPTH_TEST);

		GL11.glClearColor(0.8f, 0.8f, 0.8f, 1.0f);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

		// transparency
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	}
}
