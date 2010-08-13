package bifstk.config;

import java.awt.Font;
import java.io.File;

import bifstk.BifstkException;
import bifstk.gl.TrueTypeFont;
import bifstk.util.Logger;

public class Fonts {

	private static Fonts instance = null;

	private static final String defaultFont = "Arial";

	private TrueTypeFont normal = null;

	private TrueTypeFont small = null;

	private Font loadedFont = null;

	private Fonts() {
		Font f;
		String path = Theme.getFontPath();
		try {
			f = Font.createFont(Font.TRUETYPE_FONT, new File(path));
			f = f.deriveFont(Font.PLAIN, Theme.getFontNormalSize());
		} catch (Exception e) {
			Logger.error("Could not load font " + path + ", defaulting to '"
					+ defaultFont + "'", e);
			f = new Font("Arial", Font.PLAIN, Theme.getFontNormalSize());
		}
		this.loadedFont = f;

		this.normal = new TrueTypeFont(f, true);

		f = f.deriveFont(Font.PLAIN, Theme.getFontSmallSize());
		this.small = new TrueTypeFont(f, true);
	}

	public static void load() throws BifstkException {
		instance = new Fonts();

		String strStyle;
		if (instance.loadedFont.isBold()) {
			strStyle = instance.loadedFont.isItalic() ? "bolditalic" : "bold";
		} else {
			strStyle = instance.loadedFont.isItalic() ? "italic" : "plain";
		}
		Logger.debug("Loaded font: " + instance.loadedFont.getName() + " "
				+ strStyle);
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
