package bifstk;

import org.lwjgl.opengl.Display;
import org.newdawn.slick.util.Log;

import bifstk.config.Config;
import bifstk.config.Cursors;
import bifstk.config.Cursors.Type;
import bifstk.config.Fonts;
import bifstk.config.Theme;
import bifstk.util.BifstkException;
import bifstk.util.BifstkLogSystem;
import bifstk.util.Logger;
import bifstk.util.ThreadAccessException;
import bifstk.wm.Area;
import bifstk.wm.Logic;
import bifstk.wm.Renderer;
import bifstk.wm.Window;

/**
 * Main class for Bifstk
 * <p>
 * Entry point for the whole system
 */
public class Bifstk {

	/** thread in which the application will be run */
	private static Thread runner = null;

	/** path to the configuration file */
	private static String config = null;

	/** exit flag : thread will stop if set to true */
	private static boolean stop = false;

	/** static pointer to the WM's logic, used by the API */
	private static Logic logic = null;

	private Bifstk() {
	}

	/**
	 * Builds the runner thread and stores is statically
	 * 
	 * @param h the user event handler
	 * @param r the user root renderer
	 */
	private static void internalStart(final Handler h, final Root r) {
		runner = new Thread(new Runnable() {

			@Override
			public void run() {

				preDisplayInit();

				logic = new Logic(h);

				Renderer renderer = null;
				try {
					// create the display
					renderer = new Renderer(logic.getState(), r);

					postDisplayInit();
				} catch (Exception e) {
					Logger.error("Display initialization failed", e);
					return;
				}

				boolean vsync = Config.isDisplayVsync();
				int fps_target = 60;
				boolean capped = Config.isDisplayFpsCap();

				String log = "Display refresh rate: ";
				if (vsync) {
					fps_target = Display.getDisplayMode().getFrequency();
					log += "vsync";
					if (!capped)
						Logger.warn("Framerate will be capped due to VSync");
				} else {
					if (!capped) {
						log += "unlimited";
					} else {
						fps_target = Config.getDisplayFps();
						log += fps_target;
					}
				}
				Logger.info(log);

				// user initialization
				h.init();

				/* main loop */
				while (!(logic.isExitRequested() || stop)) {

					try {
						// poll input
						Display.processMessages();
						logic.update();

						// foreground window: maintain framerate
						if (Display.isActive()) {
							renderer.render();
							if (capped) {
								Display.sync(fps_target);
							}
						}
						// background window: lazy update
						else {
							try {
								Thread.sleep(100);
							} catch (InterruptedException e) {
							}
							// do not repaint if window is not visible
							if (Display.isVisible() || Display.isDirty()) {
								renderer.render();
							}
						}

						// swap buffers
						Display.update(false);

					} catch (Throwable t) {
						Logger.error("Fatal error, exiting", t);
						Display.destroy();
						System.exit(0);
					}

				}

			}

		});
	}

	/**
	 * Starts Bifstk in a new Thread
	 * <p>
	 * 
	 * @param configFile path to a local file containing values for all the
	 *            properties defined in {@link bifstk.config.ConfigProperty}
	 * @throws IllegalStateException Bifstk was already started
	 */
	public static void start(String configFile) {
		start(configFile, null, null);
	}

	/**
	 * Starts Bifstk in a new Thread
	 * <p>
	 * 
	 * @param configFile path to a local file containing values for all the
	 *            properties defined in {@link bifstk.config.ConfigProperty}
	 * @param r root renderer for WM background, can be null
	 * @throws IllegalStateException Bifstk was already started
	 */
	public static void start(String configFile, Root r) {
		start(configFile, null, r);
	}

	/**
	 * Starts Bifstk in a new Thread
	 * <p>
	 * 
	 * @param configFile path to a local file containing values for all the
	 *            properties defined in {@link bifstk.config.ConfigProperty}
	 * @param h handler for keyboard and mouse inputs, can be null
	 * @throws IllegalStateException Bifstk was already started
	 */
	public static void start(String configFile, Handler h) {
		start(configFile, h, null);
	}

	/**
	 * Starts Bifstk in a new Thread
	 * <p>
	 * 
	 * @param configFile path to a local file containing values for all the
	 *            properties defined in {@link bifstk.config.ConfigProperty}
	 * @param h handler for keyboard and mouse inputs, can be null
	 * @param r root renderer for WM background, can be null
	 * @throws IllegalStateException Bifstk was already started
	 */
	public static void start(String configFile, Handler h, Root r) {
		if (Bifstk.runner != null && Bifstk.runner.isAlive()) {
			throw new IllegalStateException(
					"Bifsk cannot be started while running");
		} else {
			Bifstk.stop = false;
		}

		Bifstk.config = configFile;
		internalStart(h, r);
		runner.setName("bifstk-runner");
		runner.start();
	}

	/**
	 * Asynchronously stops the Bifstk thread, can be called from outside the
	 * Bifstk thread
	 */
	public static void stop() {
		stop = true;
	}

	/**
	 * Adds a new Window in the Bifstk Window Manager
	 * 
	 * @param f the Window to add in the WM
	 * @throws ThreadAccessException method was called outside the Bifstk thread
	 */
	public static void addWindow(Window f) throws ThreadAccessException {
		checkThread();
		Bifstk.logic.getState().addWindow(f);
	}

	/**
	 * Removes a Window from the Bifstk Window Manager
	 * 
	 * @param f the Window to remove from the WM
	 * @throws ThreadAccessException method was called outside the Bifstk thread
	 */
	public static void removeWindow(Window f) throws ThreadAccessException {
		checkThread();

		Bifstk.logic.getState().removeWindow(f);
	}

	/**
	 * Sets the current modal Window of the WM
	 * <p>
	 * If the WM has a modal Window, it becomes the only Window the user can
	 * interact with, and stays focused in the foreground until closed using
	 * setModalWindow(null)
	 * <p>
	 * Setting a modal frame when the WM already holds a modal Window causes the
	 * old one to be replaced
	 * 
	 * @param f the Window to define as modal, or null
	 * @throws ThreadAccessException method was called outside the Bifstk thread
	 */
	public static void setModalWindow(Window f) throws ThreadAccessException {
		checkThread();
		Bifstk.logic.getState().setModalWindow(f);
	}

	/**
	 * Adds an Area to the WM
	 * 
	 * @param a the Area to add
	 * @throws ThreadAccessException method was called outside the Bifstk thread
	 */
	public static void addArea(Area a) throws ThreadAccessException {
		checkThread();
		Bifstk.logic.getState().addArea(a);
	}

	/**
	 * Removes an Area from the WM
	 * 
	 * @param a the Area to remove
	 * @throws ThreadAccessException method was called outside the Bifstk thread
	 */
	public static void removeArea(Area a) throws ThreadAccessException {
		checkThread();
		Bifstk.logic.getState().removeArea(a);
	}

	/**
	 * Checks that the current Thread is the Bifstk thread
	 * 
	 * @throws ThreadAccessException
	 */
	private static void checkThread() throws ThreadAccessException {
		if (!Thread.currentThread().equals(Bifstk.runner)) {
			throw new ThreadAccessException(
					"This method cannot be called outside the Bifstk thread");
		}
	}

	/**
	 * Static initialization that doesn't require display creation
	 */
	private static void preDisplayInit() {
		// load configuration
		try {
			Config.load(config);
		} catch (BifstkException e) {
			// the logger needs the config, can't be used yet
			e.printStackTrace();
			System.exit(0);
		}

		// create logsystem
		Logger.init();
		Log.setLogSystem(new BifstkLogSystem());
		Logger.info("Config loaded from: " + config);
	}

	/**
	 * Static initialization that requires display creation
	 */
	private static void postDisplayInit() throws BifstkException {
		// load theme
		String themePath = Config.getThemePath();
		Theme.load(themePath);

		// cursor creation
		Cursors.load(Config.getCursorsPath());
		Cursors.setCursor(Type.POINTER);

		// load fonts
		Fonts.load();
	}
}
