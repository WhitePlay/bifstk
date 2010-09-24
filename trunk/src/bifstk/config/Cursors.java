package bifstk.config;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Cursor;
import org.lwjgl.input.Mouse;
import org.newdawn.slick.opengl.CursorLoader;

import bifstk.util.BifstkException;
import bifstk.util.Logger;

/**
 * Defines a set of {@link org.lwjgl.input.Cursor} that can be used statically
 * by the application
 */
public class Cursors {

	/**
	 * Available types of cursors
	 */
	public static enum Type {
		// default pointer, cannot use 'default' which is a keyword
		POINTER("pointer"),
		// used when moving or dragging an item
		MOVE("move"),
		// window resize left border
		RESIZE_LEFT("resize-left"),
		// window resize left border
		RESIZE_RIGHT("resize-right"),
		// window resize top border
		RESIZE_TOP("resize-top"),
		// window resize bottom border
		RESIZE_BOT("resize-bot"),
		// window resize top left corner
		RESIZE_TOP_LEFT("resize-top-left"),
		// window resize top right corner
		RESIZE_TOP_RIGHT("resize-top-right"),
		// window resize bottom right corner
		RESIZE_BOT_RIGHT("resize-bot-right"),
		// window resize bottom left corner
		RESIZE_BOT_LEFT("resize-bot-left");

		private String name = null;

		private Type(String n) {
			this.name = n;
		}

		public String getName() {
			return this.name;
		}
	}

	/** singleton instance */
	private static Cursors instance = null;

	/** actual cursor instance matching each {@link Cursors.Type} */
	private Map<Type, Cursor> cursors = null;

	/** currently used cursor */
	private Type current = Type.POINTER;

	/**
	 * Default constructor
	 * 
	 * @param path path to the directory containing the cursors
	 * @throws BifstkException
	 */
	private Cursors(String path) throws BifstkException {
		this.cursors = new HashMap<Type, Cursor>();

		File dir = new File(path);
		if (!dir.exists() || !dir.isDirectory()) {
			throw new BifstkException("Invalid cursors directory: " + path);
		}
		for (File cur : dir.listFiles()) {
			if (cur.isDirectory()) {
				continue;
			}

			String[] ar = cur.getName().split("_");
			if (ar.length != 3) {
				throw new BifstkException("Malformed cursor filename: "
						+ cur.getAbsolutePath());
			}
			String name = ar[0];
			int xHotspot = 0;
			int yHotspot = 0;

			try {
				xHotspot = Integer.parseInt(ar[1]);
				String y = ar[2].split("[.]")[0];
				yHotspot = Integer.parseInt(y);
			} catch (Exception e) {
				throw new BifstkException(
						"Error parsing hotspot coordinates in cursor filename "
								+ cur.getAbsolutePath(), e);
			}
			Cursor cursor = null;
			try {
				cursor = CursorLoader.get().getCursor(cur.getAbsolutePath(),
						xHotspot, yHotspot);
			} catch (Exception e) {
				throw new BifstkException("Error generating cursor", e);
			}

			for (Type t : Type.values()) {
				if (t.getName().equals(name)) {
					this.cursors.put(t, cursor);
				}
			}
		}

		// check all cursors are loaded
		for (Type t : Type.values()) {
			if (!this.cursors.containsKey(t)) {
				throw new BifstkException("Could not find cursor " + t + " ("
						+ t.getName() + ")");
			}
		}
	}

	/**
	 * Changes the cursor currently displayed in the OpenGL Display
	 * 
	 * @param type cursor to display
	 */
	public static void setCursor(Type type) {
		check();
		if (type.equals(instance.current)) {
			return;
		}
		try {
			Mouse.setNativeCursor(instance.cursors.get(type));
			instance.current = type;
		} catch (LWJGLException e) {
			Logger.error("Error setting native cursor " + e.getMessage());
		}
	}

	/**
	 * Throws a runtime exception if cursors should not be accessed
	 */
	private static void check() {
		if (!Mouse.isCreated())
			throw new IllegalStateException("Mouse is not created");
		if (instance == null)
			throw new IllegalStateException("Cursors have not been loaded");
	}

	/**
	 * Loads cursors from the directory denoted by the local path
	 * <code>path</code>.
	 * <p>
	 * each regular file in this directory should be an image file representing
	 * a cursor. its name should match the string: <code>type_x_y.ext</code>
	 * where :
	 * <ul>
	 * <li>type is one of the names of the cursors defined in
	 * {@link Cursors.Type}
	 * <li>x is a positive integer denoting the cursor's hotspot abscissa
	 * <li>y is a positive integer denoting the cursor's hotspot ordinate
	 * </ul>
	 * All cursors defined in {@link Cursors.Type} should have an associated
	 * file in directory <code>path</code>, or this method will throw an
	 * exception.
	 * 
	 * 
	 * @param path path to the directory containing the cursor files
	 * @throws BifstkException
	 */
	public static void load(String path) throws BifstkException {
		instance = new Cursors(path);
		Logger.debug("Cursors loaded from: " + path);
	}
}
