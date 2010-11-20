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
			Color col = Theme.getWindowBorderFocusedColor();
			// bot border
			if (!focus) {
				col = Theme.getWindowBorderUnfocusedColor();
			}
			float[] c1 = col.toArray(4, baseAlpha);
			int[] v1 = {
					0, acc, //
					x, acc, //
					x, acc + w, //
					0, acc + w
			};
			Util.draw2D(v1, c1, GL11.GL_QUADS);
			acc += w;
		}

		/* right shadow */
		if (Theme.isWindowShadowEnabled()) {
			int radius = Theme.getWindowShadowRadius();
			Color col = this.state.getLeftDock().get(0).getShadowColor();

			float[] c1 = new float[16];
			col.fillArray(c1, 0, 4, Theme.getWindowShadowAlpha());
			col.fillArray(c1, 4, 12, 0.0f);
			col.fillArray(c1, 12, 16, Theme.getWindowShadowAlpha());
			int[] v1 = {
					x + w, 0, //
					x + w + radius, 0, //
					x + w + radius, height, //
					x + w, height
			};

			Util.draw2D(v1, c1, GL11.GL_QUADS);
		}

		Color c = Theme.getWindowBorderFocusedColor();
		if (!focus) {
			c = Theme.getWindowBorderUnfocusedColor();
		}
		/* right border */
		float[] c1 = c.toArray(4, baseAlpha);
		int[] v1 = {
				x, 0, //
				x + w, 0, //
				x + w, height, //
				x, height
		};
		Util.draw2D(v1, c1, GL11.GL_QUADS);

		c = Theme.getWindowBorderOuterFocusedColor();
		if (!focus) {
			c = Theme.getWindowBorderOuterUnfocusedColor();
		}
		/* right outer border */
		c1 = c.toArray(2);
		v1 = new int[] {
				x + w, 0, //
				x + w, height
		};
		Util.draw2D(v1, c1, GL11.GL_LINES);
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
			Color c = Theme.getWindowBorderFocusedColor();
			if (!focus) {
				c = Theme.getWindowBorderUnfocusedColor();
			}
			float[] c1 = c.toArray(4, baseAlpha);
			int[] v1 = {
					dw, acc, //
					dw - x, acc, //
					dw - x, acc + w, //
					dw, acc + w
			};
			Util.draw2D(v1, c1, GL11.GL_QUADS);
			acc += w;
		}

		/* left shadow */
		if (Theme.isWindowShadowEnabled()) {
			int radius = Theme.getWindowShadowRadius();
			Color col = this.state.getRightDock().get(0).getShadowColor();

			float[] c1 = new float[16];
			col.fillArray(c1, 0, 4, Theme.getWindowShadowAlpha());
			col.fillArray(c1, 4, 12, 0.0f);
			col.fillArray(c1, 12, 16, Theme.getWindowShadowAlpha());
			int[] v1 = {
					dw - x - w, 0, //
					dw - x - w - radius, 0, //
					dw - x - w - radius, height, //
					dw - x - w, height
			};
			Util.draw2D(v1, c1, GL11.GL_QUADS);
		}

		Color c = Theme.getWindowBorderFocusedColor();
		if (!focus) {
			Theme.getWindowBorderUnfocusedColor();
		}
		/* left border */
		float[] c1 = c.toArray(4, baseAlpha);
		int[] v1 = {
				dw - x, 0, //
				dw - x - w, 0, //
				dw - x - w, height, //
				dw - x, height
		};
		Util.draw2D(v1, c1, GL11.GL_QUADS);

		c = Theme.getWindowBorderOuterFocusedColor();
		if (!focus) {
			c = Theme.getWindowBorderOuterUnfocusedColor();
		}
		/* left outer border */
		c1 = c.toArray(2);
		v1 = new int[] {
				dw - x - w, 0, //
				dw - x - w, height
		};
		Util.draw2D(v1, c1, GL11.GL_LINES);
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
				float[] c1 = Theme.getRootBackgroundModalColor().toArray(4,
						modalAlpha * aApp * aRem);
				int[] v1 = {
						0, 0, //
						width, 0, //
						width, height, //
						0, height
				};
				Util.draw2D(v1, c1, GL11.GL_QUADS);
				/*
								GL11.glBegin(GL11.GL_QUADS);
								GL11.glVertex2i(0, 0);
								GL11.glVertex2i(width, 0);
								GL11.glVertex2i(width, height);
								GL11.glVertex2i(0, height);
								GL11.glEnd();
								*/
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
		float[] c = Theme.getRootBackgroundColor().toArray(4);
		int[] v = {
				0, 0, width, 0, width, height, 0, height
		};
		Util.draw2D(v, c, GL11.GL_QUADS);
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
