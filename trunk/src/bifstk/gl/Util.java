package bifstk.gl;

import java.awt.Graphics2D;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Hashtable;

import bifstk.config.TextureLoader;
import bifstk.gl.Rasterizer.Rotation;

/**
 * Misc GL utilities
 * 
 */
public class Util {

	/**
	 * Uses Java2D to convert an AWT image to a LWJGL ByteBuffer
	 * 
	 * @param image an AWT image
	 * @return a ByteBuffer containing the image
	 */
	public static ByteBuffer imageToByteBuffer(BufferedImage image) {
		return imageToByteBuffer(image, false);
	}

	/**
	 * Uses Java2D to convert an AWT image to a LWJGL ByteBuffer
	 * 
	 * @param image an AWT image
	 * @param flip vertically mirrors the image if true
	 * @return a ByteBuffer containing the image
	 */
	public static ByteBuffer imageToByteBuffer(BufferedImage image, boolean flip) {
		ByteBuffer imageBuffer = null;
		WritableRaster raster;
		BufferedImage texImage;
		int texWidth = Util.npot(image.getWidth());
		int texHeight = Util.npot(image.getHeight());
		int height = image.getHeight();

		ColorModel glAlphaColorModel = new ComponentColorModel(
				ColorSpace.getInstance(ColorSpace.CS_sRGB), new int[] {
						8, 8, 8, 8
				}, true, false, ComponentColorModel.TRANSLUCENT,
				DataBuffer.TYPE_BYTE);

		ColorModel glColorModel = new ComponentColorModel(
				ColorSpace.getInstance(ColorSpace.CS_sRGB), new int[] {
						8, 8, 8, 0
				}, false, false, ComponentColorModel.OPAQUE,
				DataBuffer.TYPE_BYTE);

		// create a raster that can be used by OpenGL as a source
		// for a texture
		boolean useAlpha = image.getColorModel().hasAlpha();

		if (useAlpha) {
			raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE,
					texWidth, texHeight, 4, null);
			texImage = new BufferedImage(glAlphaColorModel, raster, false,
					new Hashtable<Object, Object>());
		} else {
			raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE,
					texWidth, texHeight, 3, null);
			texImage = new BufferedImage(glColorModel, raster, false,
					new Hashtable<Object, Object>());
		}

		// copy the source image into the produced image
		Graphics2D g = (Graphics2D) texImage.getGraphics();

		if (useAlpha) {
			g.setColor(new java.awt.Color(0f, 0f, 0f, 0f));
			g.fillRect(0, 0, texWidth, texHeight);
		}

		if (flip) {
			g.scale(1.0, -1.0);
			g.drawImage(image, 0, -height, null);
		} else {
			g.drawImage(image, 0, 0, null);
		}

		// build a byte buffer from the temporary image
		// that be used by OpenGL to produce a texture.
		byte[] data = ((DataBufferByte) texImage.getRaster().getDataBuffer())
				.getData();

		imageBuffer = ByteBuffer.allocateDirect(data.length);
		imageBuffer.order(ByteOrder.nativeOrder());
		imageBuffer.put(data, 0, data.length);
		imageBuffer.flip();
		g.dispose();

		return imageBuffer;
	}

	/**
	 * @return {@link Rasterizer#getInstance()}
	 */
	public static Rasterizer raster() {
		return Rasterizer.getInstance();
	}

	/**
	 * Draws a smooth shadow at the left of an edge
	 * 
	 * @param x top abscissa coordinate
	 * @param y top ordinate coordinate
	 * @param h height of the edge
	 * @param alpha transparency
	 * @param col color
	 * @param smaller the shadow will be slightly thinner if true
	 */
	public static void drawLeftShadowQuad(int x, int y, int h, float alpha,
			Color col, boolean smaller) {
		Image img = TextureLoader.getShadowLeft();

		int w = img.getWidth();
		if (smaller)
			w /= 3;

		raster().fillQuad(x - w, y, w, h, img, col, alpha, Rotation.ROTATE_0);
	}

	/**
	 * Draws a smooth shadow at the right of an edge
	 * 
	 * @param x top abscissa coordinate
	 * @param y top ordinate coordinate
	 * @param h height of the edge
	 * @param alpha transparency
	 * @param col color
	 * @param smaller the shadow will be slightly thinner if true
	 */
	public static void drawRightShadowQuad(int x, int y, int h, float alpha,
			Color col, boolean smaller) {
		Image img = TextureLoader.getShadowLeft();

		int w = img.getWidth();
		if (smaller)
			w /= 3;

		raster().fillQuad(x, y, w, h, img, col, alpha, Rotation.ROTATE_180);
	}

	/**
	 * Draws a smooth shadow around a rectangle
	 * 
	 * @param x top left abscissa coordinate
	 * @param y top left ordinate coordinate
	 * @param w width of the rectangle
	 * @param h height of the rectangle
	 * @param alpha transparency
	 * @param col color
	 * @param smaller the shadow will be slightly thinner if true
	 */
	public static void drawShadowQuad(int x, int y, int w, int h, float alpha,
			Color col, boolean smaller) {
		Image corner = TextureLoader.getShadowTopLeft();
		Image side = TextureLoader.getShadowLeft();

		int sw = corner.getWidth();
		if (smaller)
			sw /= 3;

		drawLeftShadowQuad(x, y, h, alpha, col, smaller);
		drawRightShadowQuad(x + w, y, h, alpha, col, smaller);
		raster().fillQuad(x, y - sw, w, sw, side, col, alpha,
				Rotation.ROTATE_90);
		raster().fillQuad(x, y + h, w, sw, side, col, alpha,
				Rotation.ROTATE_270);

		raster().fillQuad(x - sw, y - sw, sw, sw, corner, col, alpha,
				Rotation.ROTATE_0);
		raster().fillQuad(x + w, y - sw, sw, sw, corner, col, alpha,
				Rotation.ROTATE_90);
		raster().fillQuad(x + w, y + h, sw, sw, corner, col, alpha,
				Rotation.ROTATE_180);
		raster().fillQuad(x - sw, y + h, sw, sw, corner, col, alpha,
				Rotation.ROTATE_270);
	}

	/**
	 * Draw a quad with rounded corners
	 * 
	 * @param x top left abscissa
	 * @param y top left ordinate
	 * @param w width
	 * @param h height
	 * @param fillCol color
	 * @param alpha opacity [0.0, 1.0]
	 */
	public static void drawRoundedQuad(int x, int y, int w, int h,
			Color fillCol, float alpha) {

		Image cd = TextureLoader.getUiCornerDraw();

		int sw = cd.getWidth();

		raster().fillQuad(x, y, sw, sw, cd, fillCol, alpha, Rotation.ROTATE_0);
		raster().fillQuad(x + w - sw, y, sw, sw, cd, fillCol, alpha,
				Rotation.ROTATE_90);
		raster().fillQuad(x + w - sw, y + h - sw, sw, sw, cd, fillCol, alpha,
				Rotation.ROTATE_180);
		raster().fillQuad(x, y + h - sw, sw, sw, cd, fillCol, alpha,
				Rotation.ROTATE_270);

		raster().fillQuad(x, y + sw, 1, h - 2 * sw, fillCol, alpha);
		raster().fillQuad(x + sw, y, w - sw * 2, 1, fillCol, alpha);
		raster().fillQuad(x + w - 1, y + sw, 1, h - 2 * sw, fillCol, alpha);
		raster().fillQuad(x + sw, y + h - 1, w - sw * 2, 1, fillCol, alpha);

	}

	/**
	 * Fill a quad with rounded corners
	 * 
	 * @param x top left abscissa
	 * @param y top left ordinate
	 * @param w width
	 * @param h height
	 * @param fillCol color
	 * @param negCol color of the corner, use {@link Color#TRANSP_WHITE} for
	 *            full transparency
	 * @param alpha opacity
	 */
	public static void fillRoundedQuad(int x, int y, int w, int h,
			Color fillCol, Color negCol, float alpha) {
		Image cf = TextureLoader.getUiCornerFill();
		Image cn = TextureLoader.getUiCornerInv();

		int sw = cf.getWidth();

		raster().fillQuad(x, y, sw, sw, cn, negCol, alpha, Rotation.ROTATE_0);
		raster().fillQuad(x + w - sw, y, sw, sw, cn, negCol, alpha,
				Rotation.ROTATE_90);
		raster().fillQuad(x + w - sw, y + h - sw, sw, sw, cn, negCol, alpha,
				Rotation.ROTATE_180);
		raster().fillQuad(x, y + h - sw, sw, sw, cn, negCol, alpha,
				Rotation.ROTATE_270);

		raster().fillQuad(x, y, sw, sw, cf, fillCol, alpha, Rotation.ROTATE_0);
		raster().fillQuad(x + w - sw, y, sw, sw, cf, fillCol, alpha,
				Rotation.ROTATE_90);
		raster().fillQuad(x + w - sw, y + h - sw, sw, sw, cf, fillCol, alpha,
				Rotation.ROTATE_180);
		raster().fillQuad(x, y + h - sw, sw, sw, cf, fillCol, alpha,
				Rotation.ROTATE_270);

		raster().fillQuad(x, y + sw, sw, h - 2 * sw, fillCol, alpha);
		raster().fillQuad(x + sw, y, w - 2 * sw, sw, fillCol, alpha);
		raster().fillQuad(x + w - sw, y + sw, sw, h - 2 * sw, fillCol, alpha);
		raster().fillQuad(x + sw, y + h - sw, w - 2 * sw, sw, fillCol, alpha);

		raster().fillQuad(x + sw, y + sw, w - 2 * sw, h - 2 * sw, fillCol,
				alpha);

	}

	/**
	 * Clamp integer in specified range
	 * 
	 * @param val value to clamp
	 * @param min min value
	 * @param max max value
	 * @return a value comprised between min and max
	 */
	public static int clampi(int val, int min, int max) {
		return Math.max(Math.min(val, max), min);
	}

	/**
	 * Clamp float in specified range
	 * 
	 * @param val value to clamp
	 * @param min min value
	 * @param max max value
	 * @return a value comprised between min and max
	 */
	public static float clampf(float val, float min, float max) {
		return Math.max(Math.min(val, max), min);
	}

	/**
	 * Next Power Of Two
	 * 
	 * @param n a positive integer
	 * @return the lesser power of two greater than n
	 */
	public static int npot(int n) {
		int k = 1;
		while (k < n) {
			k *= 2;
		}
		return k;
	}

}
