package bifstk.wm.ui;

import org.lwjgl.opengl.GL11;

import bifstk.config.Theme;
import bifstk.gl.Color;
import bifstk.gl.Util;
import bifstk.wm.geom.Rectangle;

/**
 * Border that can have 4 different border widths
 * 
 */
public class CustomBorder extends Border {

	/** dimensions of the label */
	private Rectangle bounds = null;

	private int top = 0;
	private int right = 0;
	private int bot = 0;
	private int left = 0;

	/** true when the mouse is hovering the content */
	private boolean mouseHoverContent = false;
	/** true when LBM is down on the content */
	private boolean contentMouseDown = false;

	/** bgcolor, uses uiBgColor if null */
	private Color color = null;

	/**
	 * Default constructor
	 * 
	 * @param w content of the border
	 * @param border width of the border
	 */
	public CustomBorder(Widget w, int border) {
		this(w, border, border, border, border);
	}

	/**
	 * Default constructor
	 * 
	 * @param w content of the border
	 * @param border width of the border
	 * @param color border color, uses Theme default if null
	 */
	public CustomBorder(Widget w, int border, Color color) {
		this(w, border, border, border, border, color);
	}

	/**
	 * Default constructor
	 * 
	 * @param w content of the border
	 * @param top width of the top border
	 * @param right width of the right border
	 * @param bot width of the bottom border
	 * @param left width of the left border
	 */
	public CustomBorder(Widget w, int top, int right, int bot, int left) {
		this(w, top, right, bot, left, null);
	}

	/**
	 * Default constructor
	 * 
	 * @param w content of the border
	 * @param top width of the top border
	 * @param right width of the right border
	 * @param bot width of the bottom border
	 * @param left width of the left border
	 * @param color border color, uses Theme default if null
	 */
	public CustomBorder(Widget w, int top, int right, int bot, int left,
			Color color) {
		super(w);
		this.bounds = new Rectangle();
		this.top = top;
		this.right = right;
		this.left = left;
		this.bot = bot;
		this.color = color;
	}

	@Override
	public void render(float alpha) {
		int w = Math.max(left + right, this.getWidth());
		int h = Math.max(top + bot, this.getHeight());

		if (this.hasChildren()) {
			if (this.color == null) {
				Theme.getUiBgColor().use(alpha * Theme.getUiBgAlpha());
			} else {
				this.color.use(alpha * Theme.getUiBgAlpha());
			}
			// top
			GL11.glBegin(GL11.GL_QUADS);
			GL11.glVertex2i(0, 0);
			GL11.glVertex2i(w, 0);
			GL11.glVertex2i(w, top);
			GL11.glVertex2i(0, top);
			// right
			GL11.glVertex2i(w, top);
			GL11.glVertex2i(w, h - bot);
			GL11.glVertex2i(w - right, h - bot);
			GL11.glVertex2i(w - right, top);
			// bot
			GL11.glVertex2i(w, h);
			GL11.glVertex2i(0, h);
			GL11.glVertex2i(0, h - bot);
			GL11.glVertex2i(w, h - bot);
			// left
			GL11.glVertex2i(0, h - bot);
			GL11.glVertex2i(0, top);
			GL11.glVertex2i(left, top);
			GL11.glVertex2i(left, h - bot);
			GL11.glEnd();

			// content
			int nw = w - left - right;
			int nh = h - top - bot;
			if (nw > 0 && nh > 0) {
				Util.pushScissor(left, bot, w - left - right, h - top - bot);
				GL11.glPushMatrix();
				GL11.glTranslatef(left, top, 0);

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
				this.getContent().setWidth(w - this.left - this.right);
			}
		}
	}

	@Override
	public void setHeight(int h) {
		if (this.bounds.getHeight() != h) {
			this.bounds.setHeight(h);
			if (this.getContent() != null) {
				this.getContent().setHeight(h - this.top - this.bot);
			}
		}
	}

	@Override
	public void setBounds(int w, int h) {
		if (this.bounds.getWidth() != w || this.bounds.getHeight() != h) {
			this.bounds.setBounds(w, h);
			if (this.getContent() != null) {
				this.getContent().setBounds(w - this.left - this.right,
						h - this.top - this.bot);
			}
		}
	}

	@Override
	public int getPreferredWidth() {
		int cw = 0;
		if (this.getContent() != null) {
			cw = this.getContent().getPreferredWidth();
		}
		return this.left + this.right + cw;
	}

	@Override
	public int getPreferredHeight() {
		int ch = 0;
		if (this.getContent() != null) {
			ch = this.getContent().getPreferredHeight();
		}
		return this.top + this.bot + ch;
	}

	@Override
	public int getWidth() {
		return this.bounds.getWidth();
	}

	@Override
	public int getHeight() {
		return this.bounds.getHeight();
	}

	@Override
	public void mouseHover(int x, int y) {
		if (left < x && x < left + right + this.getWidth() && top < y
				&& y < top + bot + this.getHeight()) {
			if (this.getContent() != null) {
				this.getContent().mouseHover(x - left, y - top);
				this.mouseHoverContent = true;
			}
		}
	}

	@Override
	public void mouseOut() {
		if (mouseHoverContent) {
			mouseHoverContent = false;
		}
		if (this.getContent() != null) {
			this.getContent().mouseOut();
		}
	}

	@Override
	public void mouseDown(int button) {
		if (mouseHoverContent) {
			this.contentMouseDown = true;
		}
		if (this.getContent() != null) {
			this.getContent().mouseDown(button);
		}
	}

	@Override
	public void mouseUp(int button, int x, int y) {
		if (this.contentMouseDown) {
			this.contentMouseDown = false;
		}
		if (this.getContent() != null) {
			this.getContent().mouseUp(button, x - left, y - top);
		}
	}

}
