package bifstk.wm.ui;

import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import bifstk.config.Fonts;
import bifstk.gl.Color;
import bifstk.wm.geom.Rectangle;

/**
 * Surrounds a widget by a frame, displays a title on top
 * 
 */
public class TitleBorder extends Border {

	/** dimensions of the label */
	private Rectangle bounds = null;

	/** title to display on top of the border */
	private String title = "";

	/** pixel width of the border on the non-title sides */
	private final int border = 4;

	/**
	 * Default constructor
	 * 
	 * @param content content of the border
	 * @param title title to display
	 */
	public TitleBorder(Widget content, String title) {
		super(content);
		this.title = title;
		this.bounds = new Rectangle();
	}

	@Override
	public void render(float alpha) {
		int strW = Fonts.getNormal().getWidth(this.title);
		int strH = Fonts.getNormal().getHeight();
		int w = this.getWidth();
		int h = this.getHeight();

		Color.LIGHT_BLUE.use(alpha);
		// fill outline: top
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glVertex2i(0, 0);
		GL11.glVertex2i(w, 0);
		GL11.glVertex2i(w, strH);
		GL11.glVertex2i(0, strH);
		// fill outline: right
		GL11.glVertex2i(w, strH);
		GL11.glVertex2i(w, h - border);
		GL11.glVertex2i(w - border, h - border);
		GL11.glVertex2i(w - border, strH);
		// fill outline: bot
		GL11.glVertex2i(w, h - border);
		GL11.glVertex2i(w, h);
		GL11.glVertex2i(0, h);
		GL11.glVertex2i(0, h - border);
		// fill outline: left
		GL11.glVertex2i(0, strH);
		GL11.glVertex2i(border, strH);
		GL11.glVertex2i(border, h - border);
		GL11.glVertex2i(0, h - border);
		GL11.glEnd();

		int b2 = border / 2;
		int h2 = 2 * strH / 3;
		Color.BLACK.use(alpha);
		GL11.glLineWidth(1.0f);
		// outline frame
		GL11.glBegin(GL11.GL_LINE_STRIP);
		GL11.glVertex2i(b2 + 3 * border + strW, h2);
		GL11.glVertex2i(w - b2, h2);
		GL11.glVertex2i(w - b2, h - b2);
		GL11.glVertex2i(b2, h - b2);
		GL11.glVertex2i(b2, h2);
		GL11.glVertex2i(b2 + 2 * border, h2);
		GL11.glEnd();

		// title string
		Fonts.getNormal().drawString(border * 3, 0, this.title, Color.BLACK);

		// content
		IntBuffer buf = BufferUtils.createIntBuffer(16);
		GL11.glGetInteger(GL11.GL_SCISSOR_BOX, buf);
		int bx = buf.get(), by = buf.get();
		int bw = buf.get(), bh = buf.get();
		GL11.glScissor(bx + border, by + border, w - border * 2, h - border
				- strH);
		GL11.glPushMatrix();
		GL11.glTranslatef(border, strH, 0);

		this.getContent().render(alpha);

		GL11.glPopMatrix();
		GL11.glScissor(bx, by, bw, bh);
	}

	@Override
	public void setWidth(int w) {
		this.bounds.setWidth(w);
		this.getContent().setWidth(w - this.border * 2);
	}

	@Override
	public void setHeight(int h) {
		this.bounds.setHeight(h);
		this.getContent().setHeight(
				h - this.border - Fonts.getNormal().getHeight());
	}

	@Override
	public void setBounds(int w, int h) {
		this.bounds.setBounds(w, h);
		this.getContent().setBounds(w - this.border * 2,
				h - this.border - Fonts.getNormal().getHeight());
	}

	@Override
	public int getWidth() {
		return this.bounds.getWidth();
	}

	@Override
	public int getHeight() {
		return this.bounds.getHeight();
	}

}
