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

import bifstk.config.Theme;

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

	private static float[] shadow_coord_l = {
			0.0f, 0.0f, //
			1.0f, 0.0f, //
			1.0f, 1.0f, //
			0.0f, 1.0f
	};
	private static float[] shadow_coord_t = {
			0.0f, 1.0f, //
			0.0f, 0.0f, //
			1.0f, 0.0f, //
			1.0f, 1.0f
	};
	private static float[] shadow_coord_r = {
			1.0f, 1.0f, //
			0.0f, 1.0f, //
			0.0f, 0.0f, //
			1.0f, 0.0f
	};
	private static float[] shadow_coord_b = {
			1.0f, 0.0f, //
			1.0f, 1.0f, //
			0.0f, 1.0f, //
			0.0f, 0.0f
	};

	public static void drawLeftShadowQuad(int x, int y, int h, float alpha,
			Color col, boolean smaller) {
		Image shadowSide = Theme.getWindowShadowSideImage();

		int divide = (smaller ? 2 : 1);

		int rl = shadowSide.getWidth() / divide;
		int wl = shadowSide.getTexWidth() / divide;
		float[] c = col.toArray(4, alpha);

		int[] v_l = {
				x - rl, y, //
				x - rl + wl, y, //
				x - rl + wl, y + h, //
				x - rl, y + h
		};

		raster().draw2DTexturedQuad(v_l, c, shadow_coord_l,
				shadowSide.getTexId());
	}

	public static void drawRightShadowQuad(int x, int y, int h, float alpha,
			Color col, boolean smaller) {
		Image shadowSide = Theme.getWindowShadowSideImage();

		int divide = (smaller ? 2 : 1);

		int rl = shadowSide.getWidth() / divide;
		int wl = shadowSide.getTexWidth() / divide;

		float[] c = col.toArray(4, alpha);

		int[] v_r = {
				x + rl - wl, y, //
				x + rl, y, //
				x + rl, y + h, //
				x + rl - wl, y + h,
		};

		raster().draw2DTexturedQuad(v_r, c, shadow_coord_r,
				shadowSide.getTexId());
	}

	public static void drawShadowQuad(int x, int y, int w, int h, float alpha,
			Color col, boolean smaller) {
		Image shadowCorner = Theme.getWindowShadowCornerImage();
		Image shadowSide = Theme.getWindowShadowSideImage();

		int divide = (smaller ? 2 : 1);

		int rl = shadowSide.getWidth() / divide;
		int wl = shadowSide.getTexWidth() / divide;

		int rc = shadowCorner.getWidth() / divide;
		int wc = shadowCorner.getTexWidth() / divide;

		float[] c = col.toArray(4, alpha);

		int[] v_tl = {
				x - rc, y - rc, //
				x - rc + wc, y - rc, //
				x - rc + wc, y - rc + wc, //
				x - rc, y - rc + wc
		};

		int[] v_t = {
				x, y - rl, //
				x + w, y - rl, //
				x + w, y - rl + wl, //
				x, y - rl + wl
		};

		int[] v_tr = {
				x + w + rc - wc, y - rc, //
				x + w + rc, y - rc,//
				x + w + rc, y - rc + wc,//
				x + w + rc - wc, y - rc + wc
		};

		int[] v_br = {
				x + w + rc - wc, y + h + rc - wc, //
				x + w + rc, y + h + rc - wc, //
				x + w + rc, y + h + rc, //
				x + w + rc - wc, y + h + rc,
		};

		int[] v_b = {
				x, y + h + rl - wl, //
				x + w, y + h + rl - wl, //
				x + w, y + h + rl, //
				x, y + h + rl
		};

		int[] v_bl = {
				x - rc, y + h + rc - wc, //
				x - rc + wc, y + h + rc - wc, //
				x - rc + wc, y + h + rc, //
				x - rc, y + h + rc
		};

		drawLeftShadowQuad(x, y, h, alpha, col, smaller);
		drawRightShadowQuad(x + w, y, h, alpha, col, smaller);

		raster().draw2DTexturedQuad(v_t, c, shadow_coord_t,
				shadowSide.getTexId());

		raster().draw2DTexturedQuad(v_b, c, shadow_coord_b,
				shadowSide.getTexId());

		raster().draw2DTexturedQuad(v_tl, c, shadow_coord_l,
				shadowCorner.getTexId());
		raster().draw2DTexturedQuad(v_tr, c, shadow_coord_t,
				shadowCorner.getTexId());
		raster().draw2DTexturedQuad(v_br, c, shadow_coord_r,
				shadowCorner.getTexId());
		raster().draw2DTexturedQuad(v_bl, c, shadow_coord_b,
				shadowCorner.getTexId());
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
