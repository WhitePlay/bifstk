package bifstk.gl;

/**
 * A raster image loaded as a Texture in the current GL context
 * <p>
 * The actual texture is handled by the Atlas which loads every image in a
 * single texture to avoid context switch
 * <p>
 * This class actually allows calculating the right coordinates in the Atlas to
 * access the right image fragment
 * 
 * 
 */
public class Image {

	/** height of the raster image */
	private int height;
	/** width of the raster image */
	private int width;

	/** abscissa of the Image in the texture */
	private int texX;
	/** ordinate of the Image in the texture */
	private int texY;

	/** true if the texture uses alpha */
	private boolean hasAlpha;

	/**
	 * Creates a new image for an existing texture
	 * 
	 * @param x abscissa of the image in the underlying texture
	 * @param y ordinate of the image in the underlying texture
	 * @param w width of the image
	 * @param h height of the image
	 * @param tw width of the underlying texture
	 * @param th height of the underlying texture
	 * @param id id of the underlying texture
	 */
	public Image(int x, int y, int w, int h) {
		this.texX = x;
		this.texY = y;
		this.width = w;
		this.height = h;
		this.hasAlpha = true;
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
		return Atlas.getInstance().getWidth();
	}

	/**
	 * @return the width of the actual texture (must be a power of two: may be
	 *         larger than the original image)
	 */
	public int getTexHeight() {
		return Atlas.getInstance().getHeight();
	}

	/**
	 * @return the abscissa of the image in the texture
	 */
	public int getTexX() {
		return this.texX;
	}

	/**
	 * @return the ordinate of the image in the texture
	 */
	public int getTexY() {
		return this.texY;
	}

	/**
	 * @return the texture ID in the current GL context
	 */
	public int getTexId() {
		return Atlas.getInstance().getTexId();
	}

	/**
	 * @return true if this image has transparency
	 */
	public boolean hasAlpha() {
		return this.hasAlpha;
	}
}
