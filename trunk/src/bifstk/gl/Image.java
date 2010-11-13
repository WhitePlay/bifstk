package bifstk.gl;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import org.lwjgl.opengl.GL11;

import bifstk.util.BifstkException;
import bifstk.util.Logger;

/**
 * A raster image loaded as a Texture in the current GL context
 * 
 * 
 */
public class Image {

	/** height of the raster image */
	private int height;
	/** width of the raster image */
	private int width;

	/** width of the texture */
	private int texWidth;
	/** height of the texture */
	private int texHeight;

	/** GL identifier for the texture */
	private int texId;

	/** true if the texture uses alpha */
	private boolean hasAlpha;

	/**
	 * Reads the image data and creates a new Texture in the GL context
	 * 
	 * @param path path to the image on the local filesystem
	 * @throws BifstkException
	 */
	public Image(String path) throws BifstkException {
		try {
			this.load(path);
		} catch (IOException e) {
			throw new BifstkException("Could not load image file", e);
		}
	}

	/**
	 * internal load
	 * 
	 * @param path
	 * @throws IOException
	 */
	private void load(String path) throws IOException {
		File f = new File(path);
		BufferedImage img = ImageIO.read(f);
		ByteBuffer buf = Util.imageToByteBuffer(img);

		this.width = img.getWidth();
		this.height = img.getHeight();
		this.texId = GL11.glGenTextures();
		this.texWidth = Util.npot(this.width);
		this.texHeight = Util.npot(this.height);
		this.hasAlpha = img.getColorModel().hasAlpha();

		int srcPixelFormat = hasAlpha ? GL11.GL_RGBA : GL11.GL_RGB;
		int tg = GL11.GL_TEXTURE_2D;

		GL11.glEnable(tg);
		GL11.glBindTexture(tg, this.texId);
		GL11.glTexParameteri(tg, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(tg, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		GL11.glTexImage2D(tg, 0, GL11.GL_RGBA, this.texWidth, this.texHeight,
				0, srcPixelFormat, GL11.GL_UNSIGNED_BYTE, buf);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
	}

	@Override
	public void finalize() throws Throwable {
		try {
			GL11.glDeleteTextures(this.texId);
		} catch (Throwable e) {
			Logger.error("Could not finalize texture", e);
		}
		super.finalize();
	}

	/**
	 * @return the width of the original image; not the width of the actual
	 *         texture
	 */
	public int getWidth() {
		return this.width;
	}

	/**
	 * @return the height of the original image; not the height of the actual
	 *         texture
	 */
	public int getHeight() {
		return this.height;
	}

	/**
	 * @return the width of the actual texture (must be a power of two: may be
	 *         larger than the original image)
	 */
	public int getTexWidth() {
		return this.texWidth;
	}

	/**
	 * @return the width of the actual texture (must be a power of two: may be
	 *         larger than the original image)
	 */
	public int getTexHeight() {
		return this.texHeight;
	}

	/**
	 * @return the texture ID in the current GL context
	 */
	public int getTexId() {
		return this.texId;
	}

	/**
	 * @return true if this image has transparency
	 */
	public boolean hasAlpha() {
		return this.hasAlpha;
	}
}
