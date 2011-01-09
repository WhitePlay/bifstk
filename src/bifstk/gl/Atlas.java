package bifstk.gl;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL11;

import bifstk.util.Logger;

/**
 * Texture atlas
 * <p>
 * All Bifstk textures are drawn in a single actual texture atlas handled by
 * this class
 * <p>
 * This allows geometry to be rendered in batch using only one texture and
 * different coords to address the right sub texture
 * 
 */
public class Atlas {

	/** singleton instance */
	private static Atlas instance = null;

	/** width in pixels of the atlas texture */
	private int width = 512;
	/** height in pixels of the atlas texture */
	private int height = 512;

	/** id of the tex atlas in the GL context */
	private int texId = -1;

	private int offset = 0;
	private BufferedImage img = null;

	/**
	 * @return the current texture atlas
	 */
	public static Atlas getInstance() {
		if (instance == null)
			instance = new Atlas();
		return instance;
	}

	private Atlas() {
		this.img = new BufferedImage(this.width, this.height,
				BufferedImage.TYPE_INT_ARGB);
		this.texId = GL11.glGenTextures();
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
	 * @return pixel width of the texture atlas
	 */
	public int getWidth() {
		return this.width;
	}

	/**
	 * @return pixel height of the texture atlas
	 */
	public int getHeight() {
		return this.height;
	}

	/**
	 * @return the GL texture ID of the atlas
	 */
	public int getTexId() {
		return this.texId;
	}

	public int getOffset() {
		return this.offset;
	}

	public void load(BufferedImage buf, int height) {
		Graphics2D g = (Graphics2D) this.img.getGraphics();
		g.drawImage(buf, 0, this.offset, null);

		this.offset += height;

		this.update();
	}

	private void update() {
		ByteBuffer buf = Util.imageToByteBuffer(img);

		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.texId);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER,
				GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER,
				GL11.GL_NEAREST);

		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, this.width,
				this.height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buf);

		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
	}

}
