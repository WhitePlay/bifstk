package bifstk.wm;

import java.util.Iterator;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.PixelFormat;

import bifstk.Root;
import bifstk.config.Config;
import bifstk.config.Theme;
import bifstk.gl.Color;
import bifstk.gl.Util;
import bifstk.util.BifstkException;
import bifstk.util.Logger;

/**
 * Root of the Window Manager
 * <p>
 * Managed the display and renders the WM
 * 
 */
public class Renderer {

	/** WM state */
	private State state = null;

	/** Client-side root renderer */
	private Root root = null;

	/**
	 * Default constructor Creates the opengl display
	 * 
	 * @param state view of the WM's state
	 * @throws BifstkException
	 */
	public Renderer(State state) throws BifstkException {
		this(state, null);
	}

	/**
	 * Default constructor Creates the opengl display
	 * 
	 * @param state view of the WM's state
	 * @param root Client-side root renderer, can be null
	 * @throws BifstkException
	 */
	public Renderer(State state, Root root) throws BifstkException {

		this.state = state;
		this.root = root;

		int width = Config.getDisplayWidth();
		int height = Config.getDisplayHeight();
		String title = Config.getDisplayTitle();

		boolean vsync = Config.isDisplayVsync();
		Display.setVSyncEnabled(vsync);

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

		boolean fullscreen = Config.isDisplayFullscreen();

		try {
			if (fullscreen) {
				Display.setFullscreen(true);
			} else {
				Display.setDisplayMode(mode);
			}
		} catch (LWJGLException e) {
			throw new BifstkException(e);
		}
		Display.setTitle(title);

		int samples = Config.getDisplayAntialiasSamples();

		try {
			Display.create(new PixelFormat(8, 8, 0, samples));
		} catch (LWJGLException e) {
			Display.destroy();
			if (samples > 0) {
				Logger.debug("Unable to activate multisampling", e);
			}
			try {
				Display.create();
			} catch (LWJGLException e1) {
				Display.destroy();
				throw new BifstkException("", e1);
			}
		}

		Logger.info("Created display: " + Display.getDisplayMode().toString());
	}

	/**
	 * Renders the WM
	 * <p>
	 * Does not perform any form of Display synchronization; this should be done
	 * by the caller
	 */
	public void render() {
		DisplayMode mode = Display.getDisplayMode();
		int width = mode.getWidth();
		int height = mode.getHeight();

		/* clear display */
		this.clear(width, height);

		/* draw user content */
		if (this.root != null) {
			this.root.render();
		}

		/* init rendering context */
		this.initRender(width, height);

		/* draw all areas */
		for (Area area : this.state.getAreas()) {
			Color col = Theme.getAreaFocusedColor();
			float alpha = 1.0f;
			if (!area.isFocused()) {
				col = Theme.getAreaUnfocusedColor();
				alpha *= Theme.getAreaUnfocusedAlpha();
			}

			area.render(alpha, col, Theme.getAreaUiAlpha());
		}

		/* draw all windows */
		Window f = null;
		// reverse iteration : frames are stacked with the head of the list
		// being the focused one
		Iterator<Window> it = this.state.getWindows().descendingIterator();
		while (it.hasNext()) {
			f = it.next();

			// display a mask when a modal is shown
			if (this.state.getModalWindow() == f) {
				float modalAlpha = Theme.getRootBackgroundModalAlpha();
				Theme.getRootBackgroundModalColor().use(modalAlpha);
				GL11.glBegin(GL11.GL_QUADS);
				GL11.glVertex2i(0, 0);
				GL11.glVertex2i(width, 0);
				GL11.glVertex2i(width, height);
				GL11.glVertex2i(0, height);
				GL11.glEnd();
			}

			float alpha = 1.0f;
			if (f.isDragged()) {
				alpha *= Theme.getWindowMovedAlpha();
			} else if (f.isResized()) {
				alpha *= Theme.getWindowResizedAlpha();
			}
			if (!f.isFocused()) {
				alpha *= Theme.getWindowUnfocusedAlpha();
			}
			if (Theme.isWindowShadowEnabled() && !f.isMaximized()) {
				Util.drawDroppedShadow(f.getX(), f.getY(), f.getWidth(),
						f.getHeight(), Theme.getWindowShadowRadius(),
						Theme.getWindowShadowAlpha() * alpha);
			}

			if (f.equals(this.state.getWindowDraggedTop())) {
				Theme.getWindowSnapColor().use(Theme.getWindowSnapAlpha());
				GL11.glBegin(GL11.GL_QUADS);
				GL11.glVertex2i(5, 5);
				GL11.glVertex2i(width - 5, 5);
				GL11.glVertex2i(width - 5, height - 5);
				GL11.glVertex2i(5, height - 5);
				GL11.glEnd();
				Theme.getWindowSnapColor().use();
				GL11.glBegin(GL11.GL_LINE_LOOP);
				GL11.glVertex2i(5, 5);
				GL11.glVertex2i(width - 5, 5);
				GL11.glVertex2i(width - 5, height - 5);
				GL11.glVertex2i(5, height - 5);
				GL11.glEnd();
			}

			// render the Window
			f.render(alpha, Theme.getWindowUiColor(), Theme.getWindowUiAlpha());
		}
	}

	private void clear(int width, int height) {
		// GL11.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
		// GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

		// drawing a quad seems to be faster than glClear for some reason
		Theme.getRootBackgroundColor().use();
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glVertex2i(0, 0);
		GL11.glVertex2i(width, 0);
		GL11.glVertex2i(width, height);
		GL11.glVertex2i(0, height);
		GL11.glEnd();
	}

	/**
	 * Sets up OpenGL's state for rendering: 2D ortho projection and stuff.
	 * 
	 * @param width
	 * @param height
	 */
	private void initRender(int width, int height) {
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		// 0,0 is the top-left corner, unlike LWJGL's mouse coordinates
		GL11.glOrtho(0.0d, width, height, 0.0d, -1.0d, 1.0d);

		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();
		GL11.glViewport(0, 0, width, height);

		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_TEXTURE_2D);

		// transparency
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	}
}