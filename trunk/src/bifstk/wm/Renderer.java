package bifstk.wm;

import java.util.Iterator;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
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
import bifstk.wm.State.DockPosition;

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

		/* first layer: user content */
		if (this.root != null) {
			this.root.render();
		}

		/* init rendering context */
		this.initRender(width, height);

		/* second layer: areas */
		this.renderAreas(width, height);

		/* third layer: docks */
		this.renderLeftDock(width, height);
		this.renderRightDock(width, height);

		/* top layer: windows */
		this.renderWindows(width, height);
	}

	/**
	 * Renders the Areas of the WM
	 * 
	 * @param width
	 * @param height
	 */
	private void renderAreas(int width, int height) {
		for (Area area : this.state.getAreas()) {
			area.render(area.getModAlpha(), area.getUiColor(),
					area.getUiAlpha());
		}
	}

	/**
	 * Renders the left dock of the WM
	 * 
	 * @param width
	 * @param height
	 */
	private void renderLeftDock(int width, int height) {
		if (this.state.getLeftDock().size() == 0) {
			return;
		}
		int x = this.state.getDockWidth(DockPosition.LEFT);
		int w = Theme.getWindowBorderWidth();

		boolean focus = true;
		int acc = 0;
		float baseAlpha = this.state.getLeftDock().get(0).getUiAlpha();
		/* draw the windows */
		for (Window win : this.state.getLeftDock()) {
			if (!win.isFocused()) {
				focus = false;
			}
			win.render(win.getModAlpha(), win.getUiColor(), baseAlpha);

			acc += win.getHeight();
			// bot border
			if (focus) {
				Theme.getWindowBorderFocusedColor().use(baseAlpha);
			} else {
				Theme.getWindowBorderUnfocusedColor().use(baseAlpha);
			}
			GL11.glBegin(GL11.GL_QUADS);
			GL11.glVertex2i(0, acc);
			GL11.glVertex2i(x, acc);
			GL11.glVertex2i(x, acc + w);
			GL11.glVertex2i(0, acc + w);
			GL11.glEnd();

			acc += w;
		}

		/* right shadow */
		if (Theme.isWindowShadowEnabled()) {
			int radius = Theme.getWindowShadowRadius();
			Color col = this.state.getLeftDock().get(0).getShadowColor();
			GL11.glBegin(GL11.GL_QUADS);
			col.use(Theme.getWindowShadowAlpha());
			GL11.glVertex2i(x + w, 0);
			col.use(0.0f);
			GL11.glVertex2i(x + w + radius, 0);
			GL11.glVertex2i(x + w + radius, height);
			col.use(Theme.getWindowShadowAlpha());
			GL11.glVertex2i(x + w, height);
			GL11.glEnd();
		}

		if (focus) {
			Theme.getWindowBorderFocusedColor().use(baseAlpha);
		} else {
			Theme.getWindowBorderUnfocusedColor().use(baseAlpha);
		}
		/* right border */
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glVertex2i(x, 0);
		GL11.glVertex2i(x + w, 0);
		GL11.glVertex2i(x + w, height);
		GL11.glVertex2i(x, height);
		GL11.glEnd();

		if (focus) {
			Theme.getWindowBorderOuterFocusedColor().use();
		} else {
			Theme.getWindowBorderOuterUnfocusedColor().use();
		}
		/* right outer border */
		GL11.glBegin(GL11.GL_LINES);
		GL11.glVertex2i(x + w, 0);
		GL11.glVertex2i(x + w, height);
		GL11.glEnd();
	}

	/**
	 * Renders the right dock of the WM
	 * 
	 * @param width
	 * @param height
	 */
	private void renderRightDock(int width, int height) {
		if (this.state.getRightDock().size() == 0) {
			return;
		}
		int x = this.state.getDockWidth(DockPosition.RIGHT);
		int w = Theme.getWindowBorderWidth();
		int dw = Display.getDisplayMode().getWidth();

		float baseAlpha = this.state.getRightDock().get(0).getUiAlpha();
		boolean focus = true;
		int acc = 0;
		/* draw the windows */
		for (Window win : this.state.getRightDock()) {
			if (!win.isFocused()) {
				focus = false;
			}
			win.render(win.getModAlpha(), win.getUiColor(), baseAlpha);

			acc += win.getHeight();
			// bot border
			if (focus) {
				Theme.getWindowBorderFocusedColor().use(baseAlpha);
			} else {
				Theme.getWindowBorderUnfocusedColor().use(baseAlpha);
			}
			GL11.glBegin(GL11.GL_QUADS);
			GL11.glVertex2i(dw, acc);
			GL11.glVertex2i(dw - x, acc);
			GL11.glVertex2i(dw - x, acc + w);
			GL11.glVertex2i(dw, acc + w);
			GL11.glEnd();

			acc += w;
		}

		/* left shadow */
		if (Theme.isWindowShadowEnabled()) {
			int radius = Theme.getWindowShadowRadius();
			Color col = this.state.getRightDock().get(0).getShadowColor();
			GL11.glBegin(GL11.GL_QUADS);
			col.use(Theme.getWindowShadowAlpha());
			GL11.glVertex2i(dw - (x + w), 0);
			col.use(0.0f);
			GL11.glVertex2i(dw - (x + w + radius), 0);
			GL11.glVertex2i(dw - (x + w + radius), height);
			col.use(Theme.getWindowShadowAlpha());
			GL11.glVertex2i(dw - (x + w), height);
			GL11.glEnd();
		}

		if (focus) {
			Theme.getWindowBorderFocusedColor().use(baseAlpha);
		} else {
			Theme.getWindowBorderUnfocusedColor().use(baseAlpha);
		}
		/* left border */
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glVertex2i(dw - x, 0);
		GL11.glVertex2i(dw - x - w, 0);
		GL11.glVertex2i(dw - x - w, height);
		GL11.glVertex2i(dw - x, height);
		GL11.glEnd();

		if (focus) {
			Theme.getWindowBorderOuterFocusedColor().use();
		} else {
			Theme.getWindowBorderOuterUnfocusedColor().use();
		}
		/* left outer border */
		GL11.glBegin(GL11.GL_LINES);
		GL11.glVertex2i(dw - x - w, 0);
		GL11.glVertex2i(dw - x - w, height);
		GL11.glEnd();
	}

	/**
	 * Renders the WM Windows
	 * 
	 * @param width
	 * @param height
	 */
	private void renderWindows(int width, int height) {
		Window f = null;
		// reverse iteration : frames are stacked with the head of the list
		// being the focused one
		Iterator<Window> it = this.state.getWindows().descendingIterator();
		while (it.hasNext()) {
			f = it.next();

			// display a mask when a modal is shown
			if (this.state.getModalWindow() == f) {
				long t = Sys.getTime();
				long app = this.state.getModalWindow().getApparitionTime();
				long rem = this.state.getModalWindow().getRemovalTime();
				float animLen = (float) Config.getWmAnimationsLength();

				float aApp = Util.clampf((float) (t - app) / animLen, 0.0f,
						1.0f);
				float aRem = 1.0f - Util.clampf((float) (t - rem) / animLen,
						0.0f, 1.0f);
				if (aRem == 0.0f && state.getModalWindow().isActive()) {
					aRem = 1.0f;
				}

				float modalAlpha = Theme.getRootBackgroundModalAlpha();
				Theme.getRootBackgroundModalColor().use(
						modalAlpha * aApp * aRem);
				GL11.glBegin(GL11.GL_QUADS);
				GL11.glVertex2i(0, 0);
				GL11.glVertex2i(width, 0);
				GL11.glVertex2i(width, height);
				GL11.glVertex2i(0, height);
				GL11.glEnd();
			}
			float modAlpha = f.getModAlpha();

			if (Theme.isWindowShadowEnabled() && !f.isMaximized()) {
				Util.drawDroppedShadow(f.getX(), f.getY(), f.getWidth(),
						f.getHeight(), Theme.getWindowShadowRadius(),
						Theme.getWindowShadowAlpha() * modAlpha,
						f.getShadowColor());
			}

			// render the Window
			f.render(modAlpha, f.getUiColor(), f.getUiAlpha());
		}
	}

	/**
	 * Clear the Display
	 * 
	 * @param width
	 * @param height
	 */
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
