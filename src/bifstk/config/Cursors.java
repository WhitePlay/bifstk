package bifstk.config;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Cursor;
import org.lwjgl.input.Mouse;

import bifstk.gl.Util;
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
		// window resize horizontal: left/right border
		RESIZE_HOR("resize-hor"),
		// window resize vertical: top/bot border
		RESIZE_VER("resize-ver"),
		// window resize top-left/bot-right corner
		RESIZE_TOP_LEFT("resize-top-left"),
		// window resize top-right/bot-left corner
		RESIZE_TOP_RIGHT("resize-top-right");
		
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
	private Type current = null;

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
				// cursor = CursorLoader.get().getCursor(cur.getAbsolutePath(),
				// xHotspot, yHotspot);
				cursor = this.loadCursor(cur.getAbsolutePath(), xHotspot,
						yHotspot);
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

	private Cursor loadCursor(String path, int xHotspot, int yHotspot)
			throws IOException, LWJGLException {

		BufferedImage img = ImageIO.read(new File(path));
		ByteBuffer byteBuf = Util.imageToByteBuffer(img, true);

		return new Cursor(Util.npot(img.getWidth()),
				Util.npot(img.getHeight()), xHotspot, img.getHeight()
						- yHotspot - 1, 1, byteBuf.asIntBuffer(), null);
	}

	/**
	 * Changes the cursor currently displayed in the OpenGL Displa y
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
