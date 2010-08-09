package bifstk.config;

import java.awt.Font;

import bifstk.BifstkException;
import bifstk.gl.TrueTypeFont;

public class Fonts {

	private static Fonts instance = null;

	private TrueTypeFont normal = null;

	private TrueTypeFont small = null;

	private Fonts() {
		Font f = new Font("Arial", Font.PLAIN, 12);
		this.normal = new TrueTypeFont(f, true);

		f = new Font("Arial", Font.PLAIN, 10);
		this.small = new TrueTypeFont(f, true);
	}

	public static void load() throws BifstkException {
		instance = new Fonts();
	}

	private static void check() {
		if (instance == null) {
			throw new IllegalStateException("Fonts have not been loaded");
		}
	}

	public static TrueTypeFont getNormal() {
		check();
		return instance.normal;
	}

	public static TrueTypeFont getSmall() {
		check();
		return instance.small;
	}

}
