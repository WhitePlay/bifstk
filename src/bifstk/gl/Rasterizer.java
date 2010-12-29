package bifstk.gl;

/**
 * 2D drawing operations
 *
 */
public abstract class Rasterizer {

	/** default texture coord for 2D Quads texture rendering */
	protected float[] defaultCoords = {
			0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f,
	};

	/** magic numbers; some people advertise using 0.375, this works for me */
	protected final float raster_off_1 = 0.2f, raster_off_2 = 0.2f;

	/** singleton instance */
	private static Rasterizer instance = null;

	/**
	 * Create the singleton instance
	 */
	private static void init() {
		if (instance == null) {
			instance = new LegacyRasterizer();
			// TODO use VBO Rasterizer when possible
		} else {
			throw new IllegalStateException("Rasterizer was already created");
		}
	}

	/**
	 * @return the current rasterizer to use for 2D drawing operations
	 */
	public static Rasterizer getInstance() {
		if (instance == null)
			init();
		return instance;
	}

	/**
	 * Draw a textured quad
	 * 
	 * @param vertices 4 2D vertices: 8 values
	 * @param colors 4 rgba components: 16 values
	 * @param texCoords 4 2D tex coords
	 * @param texture GL texture id
	 */
	public abstract void draw2DTexturedQuad(int[] vertices, float[] colors,
			int texture);

	/**
	 * Draw a textured quad
	 * 
	 * @param vertices 4 2D vertices: 8 values
	 * @param colors 4 rgba components: 16 values
	 * @param texCoords 4 2D tex coords
	 * @param texture GL texture id
	 */
	public abstract void draw2DTexturedQuad(int[] vertices, float[] colors,
			float[] texCoords, int texture);

	/**
	 * Draws a rectangle in line mode
	 * <p>
	 * When trying to draw pixel-accurate lines in 2D coordinates, always prefer
	 * this method as it:
	 * <ul>
	 * <li>ensures that corners will not be written twice (ie. when alpha < 1.0)
	 * <li>ensures that corners will not be skipped using offset magic
	 * </ul>
	 * Note that doing glTranslatef(0.375, 0.375, 0.) in MODELVIEW as frequently
	 * advertised does NOT guarantee pixel accuracy: you have to offset 0.5 top
	 * left and -0.3 bottom right.
	 * <p>
	 * The color array must contain the color for each pixel twice: the vertex
	 * array will indeed be reconstructed so that each line is drawn
	 * individually (sending 8 vertices and not 4)
	 * 
	 * @param vertices must be of size 8: 4 2D pixels. Order matters: should be
	 *            clockwise beginning at top left
	 * @param colors must be of size 32: 4 colors of 4rgba components, repeated
	 *            twice each
	 */
	public abstract void draw2DLineLoop(int[] vertices, float[] colors);

	/**
	 * Draw arbitrary geometry in 2D space
	 * 
	 * @param vertices 2 coordinates per vertice: x,y
	 * @param colors 4 elements per color: r,g,b,a
	 * @param glMode one of the glBegin() primitives
	 */
	public abstract void draw2D(int[] vertices, float[] colors, int glMode);

	/**
	 * Draw arbitrary geometry in 2D space
	 * 
	 * @param vertices 2 coordinates per vertice: x,y
	 * @param colors 4 elements per color: r,g,b,a
	 * @param glMode one of the glBegin() primitives
	 */
	public abstract void draw2D(float[] vertices, float[] colors, int glMode);

	/**
	 * Draw arbitrary geometry in 2D space
	 * 
	 * @param vertices 2 coordinates per vertice: x,y
	 * @param colors 4 elements per color: r,g,b,a
	 * @param glMode one of the glBegin() primitives
	 */
	public abstract void draw2D(double[] vertices, float[] colors, int glMode);

}
