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
	private IntObject[] charArray = new IntObject[256];

	/** Map of user defined font characters (Character <-> IntObject) */
	private Map<Character, IntObject> customChars = new HashMap<Character, IntObject>();

	/** Boolean flag on whether AntiAliasing is enabled or not */
	private boolean antiAlias;

	/** Font's size */
	private int fontSize = 0;

	/** Font's height */
	private int fontHeight = 0;

	/** Texture used to cache the font 0-255 characters */
	private int fontTexture;

	/** Default font texture width */
	private int textureWidth = 512;

	/** Default font texture height */
	private int textureHeight = 512;

	/** A reference to Java's AWT Font that we create our font texture from */
	private java.awt.Font font;

	/** The font metrics for our Java AWT font */
	private FontMetrics fontMetrics;

	/**
	 * This is a special internal class that holds our necessary information for
	 * the font characters. This includes width, height, and where the character
	 * is stored on the font texture.
	 */
	private class IntObject {
		/** Character's width */
		public int width;

		/** Character's height */
		public int height;

		/** Character's stored x position */
		public int storedX;

		/** Character's stored y position */
		public int storedY;
	}

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
		// If there are custom chars then I expand the font texture twice
		if (customCharsArray != null && customCharsArray.length > 0) {
			textureWidth *= 2;
		}

		// In any case this should be done in other way. Texture with size
		// 512x512
		// can maintain only 256 characters with resolution of 32x32. The
		// texture
		// size should be calculated dynamicaly by looking at character sizes.

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

			IntObject newIntObject = new IntObject();

			newIntObject.width = fontImage.getWidth();
			newIntObject.height = fontImage.getHeight();

			if (positionX + newIntObject.width >= textureWidth) {
				positionX = 0;
				positionY += rowHeight;
				rowHeight = 0;
			}

			newIntObject.storedX = positionX;
			newIntObject.storedY = positionY;

			if (newIntObject.height > fontHeight) {
				fontHeight = newIntObject.height;
			}

			if (newIntObject.height > rowHeight) {
				rowHeight = newIntObject.height;
			}

			// Draw it here
			g.drawImage(fontImage, positionX, positionY, null);

			positionX += newIntObject.width;

			if (i < 256) { // standard characters
				charArray[i] = newIntObject;
			} else { // custom characters
				customChars.put(new Character(ch), newIntObject);
			}

			fontImage = null;
		}

		// fontTexture = BufferedImageUtil
		// .getTexture(font.toString(), imgTemp).getTextureID();

		ByteBuffer buf = Util.imageToByteBuffer(imgTemp);
		this.fontTexture = GL11.glGenTextures();

		int width = imgTemp.getWidth();
		int height = imgTemp.getHeight();
		int texWidth = Util.npot(width);
		int texHeight = Util.npot(height);

		int tg = GL11.GL_TEXTURE_2D;
		GL11.glEnable(tg);
		GL11.glBindTexture(tg, fontTexture);
		GL11.glTexParameteri(tg, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(tg, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		GL11.glTexImage2D(tg, 0, GL11.GL_RGBA, texWidth, texHeight, 0,
				GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buf);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);

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
		IntObject intObject = null;
		int currentChar = 0;
		for (int i = 0; i < whatchars.length(); i++) {
			currentChar = whatchars.charAt(i);
			if (currentChar < 256) {
				intObject = charArray[currentChar];
			} else {
				intObject = (IntObject) customChars.get(new Character(
						(char) currentChar));
			}

			if (intObject != null)
				totalwidth += intObject.width;
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
		float[] c = color.toArray(4, alpha);

		IntObject intObject = null;
		int charCurrent;

		int acc = 0;
		for (int i = 0; i < whatchars.length(); i++) {
			charCurrent = whatchars.charAt(i);
			if (charCurrent < 256) {
				intObject = charArray[charCurrent];
			} else {
				intObject = (IntObject) customChars.get(new Character(
						(char) charCurrent));
			}

			if (intObject != null) {
				float sx = (float) intObject.storedX / (float) textureWidth;
				float sy = (float) intObject.storedY / (float) textureHeight;
				float rx = (float) intObject.width / (float) textureWidth;
				float ry = (float) intObject.height / (float) textureHeight;

				float[] coords = {
						sx, sy, //
						sx + rx, sy, //
						sx + rx, sy + ry, //
						sx, sy + ry
				};
				int[] v = {
						x + acc, y, //
						x + acc + intObject.width, y, //
						x + acc + intObject.width, y + intObject.height, //
						x + acc, y + intObject.height
				};

				Util.raster().draw2DTexturedQuad(v, c, coords, this.fontTexture);
				acc += intObject.width;
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