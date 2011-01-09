package bifstk.gl;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.GL11;

/**
 * A TrueType font implementation
 * <p>
 * Based on the depreciated TrueTrypeFont from Slick, which outperforms its
 * replacement in some regards.
 * 
 * 
 * 
 * @author James Chambers (Jimmy)
 * @author Jeremy Adams (elias4444)
 * @author Kevin Glass (kevglass)
 * @author Peter Korzuszek (genail)
 * 
 */
public class TrueTypeFont {

	/** Array that holds necessary information about the font characters */
	private Image[] charArray = new Image[256];

	/** Map of user defined font characters (Character <-> IntObject) */
	private Map<Character, Image> customChars = new HashMap<Character, Image>();

	/** Boolean flag on whether AntiAliasing is enabled or not */
	private boolean antiAlias;

	/** Font's size */
	private int fontSize = 0;

	/** Font's height */
	private int fontHeight = 0;

	/** A reference to Java's AWT Font that we create our font texture from */
	private java.awt.Font font;

	/** The font metrics for our Java AWT font */
	private FontMetrics fontMetrics;

	/**
	 * Constructor for the TrueTypeFont class Pass in the preloaded standard
	 * Java TrueType font, and whether you want it to be cached with
	 * AntiAliasing applied.
	 * 
	 * @param font Standard Java AWT font
	 * @param antiAlias Whether or not to apply AntiAliasing to the cached font
	 * @param additionalChars Characters of font that will be used in addition
	 *            of first 256 (by unicode).
	 */
	public TrueTypeFont(java.awt.Font font, boolean antiAlias,
			char[] additionalChars) {

		this.font = font;
		this.fontSize = font.getSize();
		this.antiAlias = antiAlias;

		createSet(additionalChars);
	}

	/**
	 * Constructor for the TrueTypeFont class Pass in the preloaded standard
	 * Java TrueType font, and whether you want it to be cached with
	 * AntiAliasing applied.
	 * 
	 * @param font Standard Java AWT font
	 * @param antiAlias Whether or not to apply AntiAliasing to the cached font
	 */
	public TrueTypeFont(java.awt.Font font, boolean antiAlias) {
		this(font, antiAlias, null);
	}

	/**
	 * Create a standard Java2D BufferedImage of the given character
	 * 
	 * @param ch The character to create a BufferedImage for
	 * 
	 * @return A BufferedImage containing the character
	 */
	private BufferedImage getFontImage(char ch) {
		// Create a temporary image to extract the character's size
		BufferedImage tempfontImage = new BufferedImage(1, 1,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = (Graphics2D) tempfontImage.getGraphics();
		if (antiAlias == true) {
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
		}
		g.setFont(font);
		fontMetrics = g.getFontMetrics();
		int charwidth = fontMetrics.charWidth(ch);

		if (charwidth <= 0) {
			charwidth = 1;
		}
		int charheight = fontMetrics.getHeight();
		if (charheight <= 0) {
			charheight = fontSize;
		}

		// Create another image holding the character we are creating
		BufferedImage fontImage;
		fontImage = new BufferedImage(charwidth, charheight,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D gt = (Graphics2D) fontImage.getGraphics();
		if (antiAlias == true) {
			gt.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
		}
		gt.setFont(font);

		gt.setColor(Color.WHITE);
		int charx = 0;
		int chary = 0;
		gt.drawString(String.valueOf(ch), (charx),
				(chary) + fontMetrics.getAscent());

		return fontImage;

	}

	/**
	 * Create and store the font
	 * 
	 * @param customCharsArray Characters that should be also added to the
	 *            cache.
	 */
	private void createSet(char[] customCharsArray) {
		int textureWidth = Atlas.getInstance().getWidth();
		int textureHeight = Atlas.getInstance().getHeight();
		int offset = Atlas.getInstance().getOffset();

		BufferedImage imgTemp = new BufferedImage(textureWidth, textureHeight,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = (Graphics2D) imgTemp.getGraphics();

		g.setColor(new Color(255, 255, 255, 1));
		g.fillRect(0, 0, textureWidth, textureHeight);

		int rowHeight = 0;
		int positionX = 0;
		int positionY = 0;

		int customCharsLength = (customCharsArray != null) ? customCharsArray.length
				: 0;

		for (int i = 0; i < 256 + customCharsLength; i++) {

			// get 0-255 characters and then custom characters
			char ch = (i < 256) ? (char) i : customCharsArray[i - 256];

			BufferedImage fontImage = getFontImage(ch);

			int nw = fontImage.getWidth();
			int nh = fontImage.getHeight();

			if (positionX + nw >= textureWidth) {
				positionX = 0;
				positionY += rowHeight;
				rowHeight = 0;
			}

			if (nh > fontHeight) {
				fontHeight = nh;
			}

			if (nh > rowHeight) {
				rowHeight = nh;
			}

			Image newIntObject = new Image(positionX, positionY + offset, nw,
					nh);

			// Draw it here
			g.drawImage(fontImage, positionX, positionY, null);

			positionX += newIntObject.getWidth();

			if (i < 256) { // standard characters
				charArray[i] = newIntObject;
			} else { // custom characters
				customChars.put(new Character(ch), newIntObject);
			}

			fontImage = null;
		}

		Atlas.getInstance().load(imgTemp, positionY + rowHeight);

	}

	/**
	 * Get the width of a given String
	 * 
	 * @param whatchars The characters to get the width of
	 * 
	 * @return The width of the characters
	 */
	public int getWidth(String whatchars) {
		int totalwidth = 0;
		Image intObject = null;
		int currentChar = 0;
		for (int i = 0; i < whatchars.length(); i++) {
			currentChar = whatchars.charAt(i);
			if (currentChar < 256) {
				intObject = charArray[currentChar];
			} else {
				intObject = customChars.get(new Character((char) currentChar));
			}

			if (intObject != null)
				totalwidth += intObject.getWidth();
		}
		return totalwidth;
	}

	/**
	 * Get the font's height
	 * 
	 * @return The height of the font
	 */
	public int getHeight() {
		return fontHeight;
	}

	/**
	 * Get the height of a String
	 * 
	 * @return The height of a given string
	 */
	public int getHeight(String HeightString) {
		return fontHeight;
	}

	/**
	 * Get the font's line height
	 * 
	 * @return The line height of the font
	 */
	public int getLineHeight() {
		return fontHeight;
	}

	/**
	 * Draw a string
	 * 
	 * @param x The x position to draw the string
	 * @param y The y position to draw the string
	 * @param whatchars The string to draw
	 * @param color The color to draw the text
	 * @param alpha opacity
	 */
	public void drawString(int x, int y, String whatchars,
			bifstk.gl.Color color, float alpha) {
		Image intObject = null;
		int charCurrent;

		int acc = 0;
		for (int i = 0; i < whatchars.length(); i++) {
			charCurrent = whatchars.charAt(i);
			if (charCurrent < 256) {
				intObject = charArray[charCurrent];
			} else {
				intObject = customChars.get(new Character((char) charCurrent));
			}

			if (intObject != null) {
				Util.raster().fillQuad(x + acc, y, intObject, alpha);

				acc += intObject.getWidth();
			}
		}
	}

	/**
	 * @return the font size in points
	 */
	public int getFontSize() {
		return this.fontSize;
	}
}