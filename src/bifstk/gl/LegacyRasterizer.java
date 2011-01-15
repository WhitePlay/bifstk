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
	protected void draw2DTexturedQuad(int[] vertices, float[] colors,
			float[] texCoords) {
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
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, Atlas.getInstance().getTexId());

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
	public void flush() {

	}
}
