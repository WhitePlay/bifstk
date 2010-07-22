package bifstk;

import org.lwjgl.opengl.Display;
import org.newdawn.slick.util.Log;

import bifstk.config.Config;
import bifstk.config.Cursors;
import bifstk.config.Cursors.Type;
import bifstk.config.Property;
import bifstk.util.BifstkLogSystem;
import bifstk.util.Logger;
import bifstk.util.Logger.Visibility;

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

				Logger.init(Visibility.BOTH, "bifstk.log", true, true);
				Log.setLogSystem(new BifstkLogSystem());

				try {
					Config.load(config);
				} catch (BifstkException e) {
					Logger.error(e);
					return;
				}

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
