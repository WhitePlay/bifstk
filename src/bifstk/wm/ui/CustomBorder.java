package bifstk.wm.ui;

import org.lwjgl.opengl.GL11;

import bifstk.config.Fonts;
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
	private boolean contentLeftMouseDown = false;
	/** true when RBM is down on the content */
	private boolean contentRightMouseDown = false;
	/** true when CBM is down on the content */
	private boolean contentCenterMouseDown = false;

	/** bgcolor, uses uiBgColor if null */
	private Color color = null;

	/** text label or null */
	private String label = null;

	/**
	 * Default constructor for an empty border
	 * 
	 * @param w content of the border
	 * @param border width of the border
	 */
	public CustomBorder(Widget w, int border) {
		this(w, border, border, border, border);
	}

	/**
	 * Default constructor for an empty titled border
	 * 
	 * @param w content of the border
	 * @param border width of the border
	 */
	public CustomBorder(Widget w, String label) {
		this(w, Fonts.getNormal().getHeight(), 6, 6, 6, null, label);
	}

	/**
	 * Default constructor
	 * 
	 * @param w content of the border
	 * @param border width of the border
	 * @param color border color, uses Theme default if null
	 */
	public CustomBorder(Widget w, int border, Color color) {
		this(w, border, border, border, border, color, null);
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
		this(w, top, right, bot, left, null, null);
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
			Color color, String label) {
		super(w);
		this.bounds = new Rectangle();
		this.top = top;
		this.right = right;
		this.left = left;
		this.bot = bot;
		this.color = color;
		this.label = label;
	}

	@Override
	public void render(float alpha, Color uiBg, float uiAlpha) {
		int w = Math.max(left + right, this.getWidth());
		int h = Math.max(top + bot, this.getHeight());

		if (this.hasChildren()) {
			Color c = this.color;
			if (this.color == null) {
				c = uiBg;
			}

			float[] c1 = c.toArray(4 * 4, alpha * uiAlpha);
			int[] v1 = {
					// top
					0, 0,//
					w, 0,//
					w, top,//
					0, top,//
					// right
					w, top, //
					w, h - bot, //
					w - right, h - bot, //
					w - right, top, //
					// bot
					w, h, //
					0, h, //
					0, h - bot, //
					w, h - bot, //
					// left
					0, h - bot, //
					0, top, //
					left, top, //
					left, h - bot
			};
			Util.draw2D(v1, c1, GL11.GL_QUADS);

			// content
			int nw = w - left - right;
			int nh = h - top - bot;
			if (nw > 0 && nh > 0) {
				GL11.glPushMatrix();
				GL11.glTranslatef(left, top, 0);
				Util.pushScissor(left, top, nw, nh, false);

				this.getContent().render(alpha, uiBg, uiAlpha);

				Util.popScissor();
				GL11.glPopMatrix();
			}
		} else {
			Color c = this.color;
			if (this.color == null) {
				c = uiBg;
			}

			float[] c1 = c.toArray(4, uiAlpha * alpha);
			int[] v1 = {
					0, 0,//
					w, 0,//
					w, h,//
					0, h
			};
			Util.draw2D(v1, c1, GL11.GL_QUADS);
		}

		if (this.label != null) {
		//	Util.pushScissor(0, 0, this.labelWidth, this.labelHeight, false);

			Color col = Theme.getUiFontColor();
			Fonts.getNormal().drawString(0, 0, this.label, col, alpha);

			//Util.popScissor();
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
	public int getPreferredWidth(int max) {
		int cw = 0;
		if (this.getContent() != null) {
			cw = this.getContent().getPreferredWidth(
					max - this.left - this.right);
		}
		return this.left + this.right + cw;
	}

	@Override
	public int getPreferredHeight(int max) {
		int ch = 0;
		if (this.getContent() != null) {
			ch = this.getContent()
					.getPreferredHeight(max - this.top - this.bot);
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
		if (left < x && x < this.getWidth() - right && top < y
				&& y < this.getHeight() - bot) {
			if (this.getContent() != null) {
				this.getContent().mouseHover(x - left, y - top);
				this.mouseHoverContent = true;
			}
		} else {
			mouseOut();
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
			if (!contentLeftMouseDown && button == 0) {
				this.contentLeftMouseDown = true;
			} else if (!contentRightMouseDown && button == 1) {
				this.contentRightMouseDown = true;
			} else if (!contentCenterMouseDown && button == 2) {
				this.contentCenterMouseDown = true;
			}
			if (this.getContent() != null) {
				this.getContent().mouseDown(button);
			}
		}
	}

	@Override
	public void mouseUp(int button, int x, int y) {
		boolean hadOne = false;
		if (this.contentLeftMouseDown && button == 0) {
			this.contentLeftMouseDown = false;
			hadOne = true;
		} else if (this.contentRightMouseDown && button == 1) {
			this.contentRightMouseDown = false;
			hadOne = true;
		} else if (this.contentCenterMouseDown && button == 2) {
			this.contentCenterMouseDown = false;
			hadOne = true;
		}

		if (hadOne && this.getContent() != null) {
			this.getContent().mouseUp(button, x - left, y - top);
		}
	}

}
