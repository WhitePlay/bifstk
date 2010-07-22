package bifstk;

import org.lwjgl.opengl.Display;
import org.newdawn.slick.util.Log;

import bifstk.config.Config;
import bifstk.config.Cursors;
import bifstk.config.Cursors.Type;
import bifstk.config.Property;
import bifstk.util.BifstkLogSystem;
import bifstk.util.Logger;

public class Bifstk {

	private static Thread runner = null;

	private static String config = null;

	private static boolean stop = false;

	private Bifstk() {
	}

	private static void internalStart() {
		runner = new Thread(new Runnable() {

			@Override
			public void run() {

				try {
					Config.load(config);
				} catch (BifstkException e) {
					// the logger needs the config, can't be used yet
					e.printStackTrace();
					return;
				}

				Logger.init();
				Log.setLogSystem(new BifstkLogSystem());
				Logger.info("Config loaded from: " + config);

				Logic logic = new Logic();

				Root root = null;
				try {
					root = new Root(logic.getState());

					// cursor needs to be created after the GL display
					Cursors.load(Config.getValue(Property.cursorsPath));
					Cursors.setCursor(Type.POINTER);
				} catch (Exception e) {
					Logger.error(e);
					return;
				}

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
						Display.sync(60);
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

	public static void start(String configFile) {
		Bifstk.config = configFile;
		internalStart();
		runner.start();
	}

	public static void stop() {
		stop = true;
	}

}
