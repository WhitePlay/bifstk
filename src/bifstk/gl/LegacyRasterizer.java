package bifstk.gl;

import org.lwjgl.opengl.GL11;

/**
 * Legacy 2D drawing operations for older hardware
 * <p>
 * uses the deprecated fixed opengl pipeline aka glBegin/glEnd
 * 
 */
public class LegacyRasterizer extends Rasterizer {

	@Override
	public void draw2DTexturedQuad(int[] vertices, float[] colors, int texture) {
		this.draw2DTexturedQuad(vertices, colors, defaultCoords, texture);
	}

	@Override
	public void draw2DTexturedQuad(int[] vertices, float[] colors,
			float[] texCoords, int texture) {
		if (vertices.length != 8) {
			throw new IllegalArgumentException(
					"Vertices array must be size 8: " + "4 2D vertices");
		}
		if (colors.length != 16) {
			throw new IllegalArgumentException(
					"Colors array must be of size 16: 4 rgba colors");
		}
		if (texCoords.length != 8) {
			throw new IllegalArgumentException(
					"TexCoords array must be of size 8: 4 2D coords");
		}

		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);

		GL11.glBegin(GL11.GL_QUADS);
		for (int i = 0; i < vertices.length / 2; i++) {
			GL11.glTexCoord2f(texCoords[i * 2], texCoords[i * 2 + 1]);
			GL11.glColor4f(colors[i * 4], colors[i * 4 + 1], colors[i * 4 + 2],
					colors[i * 4 + 3]);
			GL11.glVertex2i(vertices[i * 2], vertices[i * 2 + 1]);
		}
		GL11.glEnd();

		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
	}

	@Override
	public void draw2DLineLoop(int[] vertices, float[] colors) {
		if (vertices.length != 8) {
			throw new IllegalArgumentException(
					"Vertices array must be of size 8: " + "4 2D vertices");
		}
		if (colors.length != 32) {
			throw new IllegalArgumentException(
					"Colors array must be of size 32: "
							+ "4 rgba colors repeated 2 times each");
		}

		float[] verts = new float[16];

		// top left -> top right
		verts[0] = vertices[0] + raster_off_1 + 1.0f;
		verts[1] = vertices[1] + raster_off_1;
		verts[2] = vertices[2];
		verts[3] = vertices[3];

		// top right -> bot right
		verts[4] = vertices[2];
		verts[5] = vertices[3] + 1.0f;
		verts[6] = vertices[4] - raster_off_2;
		verts[7] = vertices[5] - raster_off_2;

		// bot rigth -> bot left
		verts[8] = vertices[4] - raster_off_2 - 1.0f;
		verts[9] = vertices[5] - raster_off_2;
		verts[10] = vertices[6];
		verts[11] = vertices[7];

		// bot left -> top left
		verts[12] = vertices[6];
		verts[13] = vertices[7] - 1.0f;
		verts[14] = vertices[0] + raster_off_1;
		verts[15] = vertices[1] + raster_off_1;

		this.draw2D(verts, colors, GL11.GL_LINES);
	}

	@Override
	public void draw2D(int[] vertices, float[] colors, int glMode) {
		if (vertices.length / 2 != colors.length / 4) {
			throw new IllegalArgumentException(
					"Vertices and Colors array sizes do not match ("
							+ vertices.length + "/" + colors.length + ")");
		}
		GL11.glBegin(glMode);
		for (int i = 0; i < vertices.length / 2; i++) {
			GL11.glColor4f(colors[i * 4], colors[i * 4 + 1], colors[i * 4 + 2],
					colors[i * 4 + 3]);
			GL11.glVertex2i(vertices[i * 2], vertices[i * 2 + 1]);
		}
		GL11.glEnd();
	}

	@Override
	public void draw2D(float[] vertices, float[] colors, int glMode) {
		if (vertices.length / 2 != colors.length / 4) {
			throw new IllegalArgumentException(
					"Vertices and Colors array sizes do not match");
		}
		GL11.glBegin(glMode);
		for (int i = 0; i < vertices.length / 2; i++) {
			GL11.glColor4f(colors[i * 4], colors[i * 4 + 1], colors[i * 4 + 2],
					colors[i * 4 + 3]);
			GL11.glVertex2f(vertices[i * 2], vertices[i * 2 + 1]);
		}
		GL11.glEnd();
	}

	@Override
	public void draw2D(double[] vertices, float[] colors, int glMode) {
		if (vertices.length / 2 != colors.length / 4) {
			throw new IllegalArgumentException(
					"Vertices and Colors array sizes do not match");
		}
		GL11.glBegin(glMode);
		for (int i = 0; i < vertices.length / 2; i++) {
			GL11.glColor4f(colors[i * 4], colors[i * 4 + 1], colors[i * 4 + 2],
					colors[i * 4 + 3]);
			GL11.glVertex2d(vertices[i * 2], vertices[i * 2 + 1]);
		}
		GL11.glEnd();
	}

}
