package bifstk.config;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Cursor;
import org.lwjgl.input.Mouse;
import org.newdawn.slick.opengl.CursorLoader;

import bifstk.BifstkException;
import bifstk.util.Logger;

public class Cursors {

	public static enum Type {
		pointer("pointer"), move("move"), left("left"), right("right"), top(
				"top"), bot("bot"), topLeft("top-left"), topRight("top-right"), botRight(
				"bot-right"), botLeft("bot-left");

		private String name = null;

		private Type(String n) {
			this.name = n;
		}

		public String getName() {
			return this.name;
		}
	}

	private static Cursors instance = null;

	private Map<Type, Cursor> cursors = null;

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
				Integer.parseInt(ar[1]);
				String y = ar[2].split("[.]")[0];
				Integer.parseInt(y);
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
	}

	public static void setCursor(Type type) {
		check();
		try {
			Mouse.setNativeCursor(instance.cursors.get(type));
		} catch (LWJGLException e) {
			Logger.error("Error setting native cursor " + e.getMessage());
		}
	}

	private static void check() {
		if (!Mouse.isCreated())
			throw new IllegalStateException("Mouse is not created");
		if (instance == null)
			throw new IllegalStateException("Cursors have not been loaded");
	}

	public static void load(String path) throws BifstkException {
		instance = new Cursors(path);
	}
}
