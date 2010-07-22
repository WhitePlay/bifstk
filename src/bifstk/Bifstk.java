package bifstk;

import org.lwjgl.opengl.Display;
import org.newdawn.slick.util.Log;

import bifstk.config.Config;
import bifstk.config.Cursors;
import bifstk.config.Cursors.Type;
import bifstk.config.Property;
import bifstk.util.BifstkLogSystem;
import bifstk.util.Logger;

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

				Logic logic = new Logic();

				Root root = null;
				try {
					// create the display
					root = new Root(logic.getState());

					// cursor needs to be created after the GL display
					Cursors.load(Config.getValue(Property.cursorsPath));
					Cursors.setCursor(Type.POINTER);
				} catch (Exception e) {
					Logger.error(e);
					return;
				}

				int fps = getFps();
				Logger.debug("Refresh rate: " + fps + "fps");

				/*
				 * main loop
				 */
				while (!logic.isExitRequested() || stop) {

					// poll input
					Display.processMessages();
					logic.update();

					// foreground window: maintain framerate
					if (Display.isActive()) {
						root.render();
						Display.sync(fps);
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
	 * @param configFile path to a local file containing values for all the
	 *            properties defined in {@link bifstk.config.Property}
	 * @throws IllegalStateException Bifstk was already started
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
}
