package bifstk.gl;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

/**
 * Legacy 2D drawing operations for older hardware
 * <p>
 * uses the deprecated fixed opengl pipeline
 * 
 */
public class LegacyRasterizer extends Rasterizer {

	private static final int MAX_SIZE = 32768;

	private IntBuffer vertexBuffer = null;
	private FloatBuffer colorBuffer = null;
	private FloatBuffer coordBuffer = null;

	private int quads = 0;
	private int quadsAcc = 0;

	/** counts the number of objects cached since last flush */
	private int indexCount = 0;

	public LegacyRasterizer() {
		super();

		vertexBuffer = BufferUtils.createIntBuffer(MAX_SIZE);
		colorBuffer = BufferUtils.createFloatBuffer(MAX_SIZE);
		coordBuffer = BufferUtils.createFloatBuffer(MAX_SIZE);
	}

	@Override
	protected void draw2DTexturedQuad(int[] vertices, float[] colors,
			float[] texCoords) {
		if (vertices.length != 8)
			throw new IllegalArgumentException("");

		if (colors.length != 16)
			throw new IllegalArgumentException("");

		if (texCoords.length != 8)
			throw new IllegalArgumentException("");

		vertexBuffer.put(vertices);
		colorBuffer.put(colors);
		coordBuffer.put(texCoords);

		this.indexCount += 4;

		if ((indexCount + 4) * 8 > MAX_SIZE) {
			this.flush(false);
		}
	}

	@Override
	public void flush() {
		this.flush(true);
	}

	private void flush(boolean ext) {
		this.quadsAcc += indexCount / 4;

		if (ext) {
			this.quads = quadsAcc;
			this.quadsAcc = 0;
		}

		if (indexCount == 0)
			return;

		this.vertexBuffer.flip();
		this.colorBuffer.flip();
		this.coordBuffer.flip();

		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, Atlas.getInstance().getTexId());

		GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
		GL11.glEnableClientState(GL11.GL_COLOR_ARRAY);
		GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);

		GL11.glVertexPointer(2, 0, this.vertexBuffer);
		GL11.glColorPointer(4, 0, this.colorBuffer);
		GL11.glTexCoordPointer(2, 0, this.coordBuffer);

		GL11.glDrawArrays(GL11.GL_QUADS, 0, this.indexCount);

		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
		GL11.glDisableClientState(GL11.GL_COLOR_ARRAY);
		GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);

		this.vertexBuffer.clear();
		this.colorBuffer.clear();
		this.coordBuffer.clear();
		this.indexCount = 0;
	}

	@Override
	public int getQuadCount() {
		return this.quads;
	}
}
