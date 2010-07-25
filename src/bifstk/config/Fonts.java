package bifstk.config;

import java.awt.Font;

import bifstk.BifstkException;
import bifstk.gl.Color;
import bifstk.gl.TrueTypeFont;

public class Fonts {

	private static TrueTypeFont font = null;

	public static void load() throws BifstkException {
		Font f = new Font("Arial", Font.PLAIN, 12);
		font = new TrueTypeFont(f, true);

	}

	public static void draw(String message, int x, int y, Color color) {
		font.drawString(x, y, message, color);
	}

}
