package bifstk.wm.ui;

import org.lwjgl.opengl.GL11;

import bifstk.config.Fonts;
import bifstk.config.Theme;
import bifstk.gl.Color;
import bifstk.gl.Util;
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
		int w = Math.max(this.getWidth(), 2 * border);
		int h = Math.max(this.getHeight(), border + strH);

		if (this.hasChildren()) {

			Theme.getUiBgColor().use(alpha * Theme.getUiBgAlpha());
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

			int nw = w - border * 2;
			int nh = h - border - strH;
			if (nw > 0 && nh > 0) {
				// title string
				Fonts.getNormal().drawString(border * 3, 0, this.title,
						Color.BLACK, alpha);

				// content
				Util.pushScissor(border, border, nw, nh);
				GL11.glPushMatrix();
				GL11.glTranslatef(border, strH, 0);

				this.getContent().render(alpha);

				GL11.glPopMatrix();
				Util.popScissor();
			}
		} else {
			Theme.getUiBgColor().use(alpha * Theme.getUiBgAlpha());
			GL11.glBegin(GL11.GL_QUADS);
			GL11.glVertex2i(0, 0);
			GL11.glVertex2i(w, 0);
			GL11.glVertex2i(w, h);
			GL11.glVertex2i(0, h);
			GL11.glEnd();
		}
	}

	@Override
	public void setWidth(int w) {
		if (this.bounds.getWidth() != w) {
			this.bounds.setWidth(w);
			if (this.getContent() != null) {
				this.getContent().setWidth(w - this.border * 2);
			}
		}
	}

	@Override
	public void setHeight(int h) {
		if (this.bounds.getHeight() != h) {
			this.bounds.setHeight(h);
			if (this.getContent() != null) {
				this.getContent().setHeight(
						h - this.border - Fonts.getNormal().getHeight());
			}
		}
	}

	@Override
	public void setBounds(int w, int h) {
		if (this.bounds.getWidth() != w || this.bounds.getHeight() != h) {
			this.bounds.setBounds(w, h);
			if (this.getContent() != null) {
				this.getContent().setBounds(w - this.border * 2,
						h - this.border - Fonts.getNormal().getHeight());
			}
		}
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
