package bifstk;

import org.lwjgl.Sys;
import org.lwjgl.opengl.Display;
import org.newdawn.slick.util.Log;

import bifstk.config.Config;
import bifstk.config.Cursors;
import bifstk.config.Cursors.Type;
import bifstk.config.Fonts;
import bifstk.config.Property;
import bifstk.gl.Color;
import bifstk.util.BifstkLogSystem;
import bifstk.util.Logger;
import bifstk.wm.Logic;
import bifstk.wm.Root;

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

	/** default fps */
	private final static int default_fps = 60;

	/** minimum refresh rate in fps */
	private final static int min_fps = 10;

	/** maximum refresh rate in fps */
	private final static int max_fps = 100;

	private Bifstk() {
	}

	/**
	 * Builds the runner thread and stores is statically
	 */
	private static void internalStart() {
		runner = new Thread(new Runnable() {

			@Override
			public void run() {

				preDisplayInit();

				Logic logic = new Logic();

				Root root = null;
				try {
					// create the display
					root = new Root(logic.getState());

					postDisplayInit();
				} catch (Exception e) {
					Logger.error(e);
					return;
				}

				boolean vsync = new Boolean(
						Config.getValue(Property.displayVsync));
				int fps_target = 60;
				boolean capped = isCapped();

				String log = "Display refresh rate: ";
				if (vsync) {
					fps_target = Display.getDisplayMode().getFrequency();
					log += fps_target + " (vsync)";
					if (!capped)
						Logger.warn("Framerate will be capped due to VSync");
				} else {
					if (!capped) {
						log += "unlimited";
					} else {
						fps_target = getFps();
						log += fps_target;
					}
				}
				Logger.info(log);

				int fps_real = 0, fps_acc = 0;
				long dt = 0, dt2 = 0;

				/*
				 * main loop
				 */
				while (!logic.isExitRequested() || stop) {

					// calculate actual framerate
					dt = Sys.getTime();
					if (dt - dt2 > 1000) {
						fps_real = fps_acc;
						fps_acc = 0;
						dt2 = dt;
					} else {
						fps_acc++;
					}

					// poll input
					Display.processMessages();
					logic.update();

					// foreground window: maintain framerate
					if (Display.isActive()) {
						root.render();
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
							root.render();
						}
					}
					// draw framerate
					Fonts.draw("FPS: " + fps_real, 0, 0, Color.BLACK);

					// swap buffers
					Display.update(false);

				}

			}

		});
	}

	/**
	 * Starts Bifstk in a new Thread
	 * <p>
	 * 
	 * @param configFile
	 *            path to a local file containing values for all the properties
	 *            defined in {@link bifstk.config.Property}
	 * @throws IllegalStateException
	 *             Bifstk was already started
	 */
	public static void start(String configFile) {
		if (Bifstk.runner != null && Bifstk.runner.isAlive()) {
			throw new IllegalStateException(
					"Bifsk cannot be started while running");
		} else {
			Bifstk.stop = false;
		}

		Bifstk.config = configFile;
		internalStart();
		runner.start();
	}

	/**
	 * Asynchronously stops the Bifstk thread
	 */
	public static void stop() {
		stop = true;
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
			return;
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
		// cursor creation
		Cursors.load(Config.getValue(Property.cursorsPath));
		Cursors.setCursor(Type.POINTER);

		// load fonts
		Fonts.load();
	}

	/**
	 * Extracts FPS from config
	 * 
	 * @return the refresh rate of the display in frames per second
	 */
	private static int getFps() {
		int fps = default_fps;
		try {
			fps = Integer.parseInt(Config.getValue(Property.displayFps));
		} catch (NumberFormatException e) {
			Logger.warn("Could not parse property for " + Property.displayFps);
		}
		if (fps < min_fps) {
			Logger.warn("FPS should not be less than " + min_fps);
			fps = min_fps;
		}
		if (fps > max_fps) {
			Logger.warn("FPS should not be more than " + max_fps);
			fps = max_fps;
		}

		return fps;
	}

	private static boolean isCapped() {
		boolean cap = new Boolean(Config.getValue(Property.displayFpsCap));

		return cap;
	}
}
