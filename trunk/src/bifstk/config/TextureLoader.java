package bifstk.config;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import bifstk.gl.Atlas;
import bifstk.gl.Image;
import bifstk.util.BifstkException;

/**
 * Loads textures and statically holds references to them
 * <p>
 * The coordinates of the individual images in the map are hard-coded. Deal with
 * it.
 */
public class TextureLoader {

	/** singleton instance */
	private static TextureLoader instance = null;

	private Image blank;

	private Image bifstk256;
	private Image bifstk128;
	private Image bifstk64;
	private Image bifstk32;
	private Image bifstk16;

	private Image shadowLeft;
	private Image shadowTopLeft;

	private Image windowMaximize;
	private Image windowClose;

	private Image uiCornerDraw;
	private Image uiCornerFill;
	private Image uiCornerInv;

	private TextureLoader(String path) throws BifstkException {
		File f = new File(path);
		BufferedImage img = null;
		try {
			img = ImageIO.read(f);
		} catch (IOException e) {
			throw new BifstkException("Failed to load texture map at " + path,
					e);
		}
		Atlas.getInstance().load(img, img.getHeight());

		// this is hardcoded, deal with it
		this.blank = new Image(323, 147, 16, 16);

		this.bifstk256 = new Image(1, 1, 256, 256);
		this.bifstk128 = new Image(258, 1, 128, 128);
		this.bifstk64 = new Image(258, 130, 64, 64);
		this.bifstk32 = new Image(258, 195, 32, 32);
		this.bifstk16 = new Image(291, 196, 16, 16);

		this.shadowLeft = new Image(323, 130, 16, 16);
		this.shadowTopLeft = new Image(340, 130, 16, 16);

		this.windowMaximize = new Image(357, 130, 16, 16);
		this.windowClose = new Image(374, 130, 16, 16);

		this.uiCornerDraw = new Image(308, 195, 3, 3);
		this.uiCornerFill = new Image(312, 195, 3, 3);
		this.uiCornerInv = new Image(316, 195, 3, 3);

	}

	/**
	 * @return a completely blank Image
	 */
	public static Image getBlank() {
		return instance.blank;
	}

	/**
	 * @return the Bifstk logo as 256x256
	 */
	public static Image getBifstk256() {
		return instance.bifstk256;
	}

	/**
	 * @return the Bifstk logo as 128x128
	 */
	public static Image getBifstk128() {
		return instance.bifstk128;
	}

	/**
	 * @return the Bifstk logo as 64x64
	 */
	public static Image getBifstk64() {
		return instance.bifstk64;
	}

	/**
	 * @return the Bifstk logo as 32x32
	 */
	public static Image getBifstk32() {
		return instance.bifstk32;
	}

	/**
	 * @return the Bifstk logo as 16x16
	 */
	public static Image getBifstk16() {
		return instance.bifstk16;
	}

	/**
	 * @return the left gradient shadow
	 */
	public static Image getShadowLeft() {
		return instance.shadowLeft;
	}

	/**
	 * @return the top left gradient shadow
	 */
	public static Image getShadowTopLeft() {
		return instance.shadowTopLeft;
	}

	/**
	 * @return the Window maximize title control
	 */
	public static Image getWindowMaximize() {
		return instance.windowMaximize;
	}

	/**
	 * @return the Window close title control
	 */
	public static Image getWindowClose() {
		return instance.windowClose;
	}

	/**
	 * @return the line drawing rounded ui corner
	 */
	public static Image getUiCornerDraw() {
		return instance.uiCornerDraw;
	}

	/**
	 * @return the filling rounded ui corner
	 */
	public static Image getUiCornerFill() {
		return instance.uiCornerFill;
	}

	/**
	 * @return the inverse of the filling corner; so that both images combined
	 *         fill a square
	 */
	public static Image getUiCornerInv() {
		return instance.uiCornerInv;
	}

	/**
	 * Loads all textures
	 * 
	 * @param path path to the textures map
	 * @throws BifstkException loading failed
	 * @throws IllegalStateException textures have already been loaded
	 */
	public static void load(String path) throws BifstkException {
		if (instance == null) {
			instance = new TextureLoader(path);
		} else {
			//	throw new IllegalStateException("Textures have already been loaded");
			// Logger.debug("Textures have already been loaded");
		}
	}
}
