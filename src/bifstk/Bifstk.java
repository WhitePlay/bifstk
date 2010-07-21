package bifstk;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.newdawn.slick.util.Log;

import bifstk.config.Config;
import bifstk.config.Cursors;
import bifstk.config.Property;
import bifstk.config.Cursors.Type;
import bifstk.util.BifstkLogSystem;


public class Bifstk {

	private static Thread runner = null;

	private static boolean stop = false;

	private Bifstk() {
	}

	private static void internalStart() {
		runner = new Thread(new Runnable() {

			@Override
			public void run() {

				Log.setLogSystem(new BifstkLogSystem());
				Logic logic = new Logic();

				Root root = null;
				try {
					root = new Root(logic.getState());

					// cursor needs to be created after the GL display
					Cursors.load(Config.getValue(Property.cursorsPath));
					Mouse.setNativeCursor(Cursors.getCursor(Type.pointer));
				} catch (Exception e) {
					e.printStackTrace();
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
		internalStart();
		try {
			Config.load(configFile);
		} catch (BifstkException e) {
			e.printStackTrace();
			return;
		}
		runner.start();
	}

	public static void stop() {
		stop = true;
	}

}
