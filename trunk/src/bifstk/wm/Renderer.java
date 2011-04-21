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
import bifstk.gl.Rasterizer;
import bifstk.gl.Util;
import bifstk.util.BifstkException;
import bifstk.util.Logger;
import bifstk.wm.State.DockPosition;

/**
 * Root of the Window Manager
 * <p>
 * Manages the display and renders the WM
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

		int width = Config.get().getDisplayWidth();
		int height = Config.get().getDisplayHeight();
		String title = Config.get().getDisplayTitle();

		boolean vsync = Config.get().isDisplayVsync();
		Display.setVSyncEnabled(vsync);

		boolean fullscreen = Config.get().isDisplayFullscreen();

		try {
			if (fullscreen) {
				Display.setFullscreen(true);
			} else {
				Display.setDisplayMode(new DisplayMode(width, height));
			}
		} catch (LWJGLException e) {
			throw new BifstkException(e);
		}
		Display.setTitle(title);

		int samples = Config.get().getDisplayAntialiasSamples();

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

		/* send data to OpenGL */
		Rasterizer.getInstance().flush();
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

		float focusAnim = this.state.getLeftDock().get(0).getFocusAnim();
		Color c = Theme.getWindowBorderFocusedColor().blend(
				Theme.getWindowBorderUnfocusedColor(), focusAnim);
		int acc = 0;
		float baseAlpha = this.state.getLeftDock().get(0).getUiAlpha();
		/* draw the windows */
		for (Window win : this.state.getLeftDock()) {
			win.render(1.0f, win.getUiColor(), win.getUiAlpha());

			acc += win.getHeight();
			// bot border
			Util.raster().fillQuad(0, acc, x, w, c, win.getUiAlpha());

			acc += w;
		}

		/* right shadow */
		if (Theme.isWindowShadowEnabled()) {
			Color col = this.state.getLeftDock().get(0).getShadowColor();
			Util.drawRightShadowQuad(x + w, 0, height,
					Theme.getWindowShadowAlpha(), col, false);
		}

		/* right border */
		Util.raster().fillQuad(x, 0, w, height, c, baseAlpha);

		c = Theme.getWindowBorderOuterFocusedColor().blend(
				Theme.getWindowBorderOuterUnfocusedColor(), focusAnim);
		/* right outer border */
		Util.raster().fillQuad(x + w, 0, 1, height, c, baseAlpha);
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
		float focusAnim = this.state.getRightDock().get(0).getFocusAnim();
		Color c = Theme.getWindowBorderFocusedColor().blend(
				Theme.getWindowBorderUnfocusedColor(), focusAnim);
		int acc = 0;
		/* draw the windows */
		for (Window win : this.state.getRightDock()) {
			win.render(1.0f, win.getUiColor(), win.getUiAlpha());

			acc += win.getHeight();
			// bot border
			Util.raster().fillQuad(dw - x, acc, x, w, c, baseAlpha);
			acc += w;
		}

		/* left shadow */
		if (Theme.isWindowShadowEnabled()) {
			Color col = this.state.getRightDock().get(0).getShadowColor();
			Util.drawLeftShadowQuad(dw - x - w, 0, height,
					Theme.getWindowShadowAlpha(), col, false);
		}

		/* left border */
		Util.raster().fillQuad(dw - x - w, 0, w, height, c, baseAlpha);

		c = Theme.getWindowBorderOuterFocusedColor().blend(
				Theme.getWindowBorderOuterUnfocusedColor(), focusAnim);
		/* left outer border */
		Util.raster().fillQuad(dw - x - w, 0, 1, height, c, baseAlpha);
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
				float modalAlpha = Theme.getRootBackgroundModalAlpha();

				if (Config.get().isWmAnimations()) {
					long t = Sys.getTime();
					long app = this.state.getModalWindow().getApparitionTime();
					long rem = this.state.getModalWindow().getRemovalTime();
					float animLen = (float) Config.get()
							.getWmAnimationsLength();

					float aApp = Util.clampf((float) (t - app) / animLen, 0.0f,
							1.0f);
					float aRem = 1.0f - Util.clampf(
							(float) (t - rem) / animLen, 0.0f, 1.0f);
					if (aRem == 0.0f && state.getModalWindow().isActive()) {
						aRem = 1.0f;
					}
					modalAlpha *= aApp * aRem;
				}

				Util.raster().fillQuad(0, 0, width, height,
						Theme.getRootBackgroundModalColor(), modalAlpha);
			}
			float modAlpha = f.getModAlpha();

			if (Theme.isWindowShadowEnabled() && !f.isMaximized()) {
				Util.drawShadowQuad(f.getX(), f.getY(), f.getWidth(),
						f.getHeight(), Theme.getWindowShadowAlpha() * modAlpha,
						f.getShadowColor(), false);
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
		Util.raster().fillQuad(0, 0, width, height,
				Theme.getRootBackgroundColor(), 1.0f);
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
