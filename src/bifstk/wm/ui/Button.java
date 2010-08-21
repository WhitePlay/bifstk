package bifstk.wm.ui;

import org.lwjgl.opengl.GL11;

import bifstk.config.Fonts;
import bifstk.config.Theme;
import bifstk.gl.Color;
import bifstk.wm.geom.Rectangle;

/**
 * Default clickable widget
 * 
 */
public class Button extends Actionable {

	/** dimensions of the button */
	private Rectangle bounds = null;
	/** text to display in the button */
	private String text = null;
	/** pixel width of the string when rendered */
	private int textWidth = 0;
	/** pixel height of the string when rendered */
	private int textHeight = 0;
	/** true when the button is being hovered by LMB */
	private boolean mouseHover = false;
	/** true when the button is being clicked by LMB */
	private boolean mouseClicked = false;

	/**
	 * Default constructor
	 * 
	 * @param text text to display in the button
	 */
	public Button(String text) {
		this.text = text;
		this.bounds = new Rectangle();

		this.textWidth = Fonts.getNormal().getWidth(this.text);
		this.textHeight = Fonts.getNormal().getHeight();
	}

	@Override
	public void render(float alpha) {

		int w = this.getWidth();
		int h = this.getHeight();

		if (this.mouseClicked) {
			Color.LIGHT_BLUE.use(alpha * Theme.getUiBgAlpha());
		} else if (this.mouseHover) {
			Color.LIGHT_RED.use(alpha * Theme.getUiBgAlpha());
		} else {
			Theme.getUiBgColor().use(alpha * Theme.getUiBgAlpha());
		}
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glVertex2i(0, 0);
		GL11.glVertex2i(w, 0);
		GL11.glVertex2i(w, h);
		GL11.glVertex2i(0, h);
		GL11.glEnd();

		int lx, ly;
		if (this.textWidth < w) {
			lx = (w - this.textWidth) / 2;
		} else {
			lx = 0;
		}
		if (this.textHeight < h) {
			ly = (h - this.textHeight) / 2;
		} else {
			ly = 0;
		}
		Fonts.getNormal().drawString(lx + 1, ly + 1, this.text, Color.WHITE,
				alpha);
		Fonts.getNormal().drawString(lx, ly, this.text, Color.BLACK, alpha);

	}

	@Override
	public int getPreferredWidth() {
		return Fonts.getNormal().getWidth(this.text);
	}

	@Override
	public int getPreferredHeight() {
		return Fonts.getNormal().getHeight();
	}

	@Override
	public void setWidth(int w) {
		this.bounds.setWidth(w);
	}

	@Override
	public void setHeight(int h) {
		this.bounds.setHeight(h);
	}

	public void setBounds(int w, int h) {
		this.bounds.setBounds(w, h);
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
		this.mouseHover = true;
	}

	@Override
	public void mouseOut() {
		this.mouseHover = false;
	}

	@Override
	public void mouseDown(int button) {
		if (button == 0) {
			this.mouseClicked = true;
		}
	}

	@Override
	public void mouseUp(int button, int x, int y) {
		if (button == 0) {
			this.mouseClicked = false;
			if (this.bounds.contains(x, y) && this.getHandler() != null) {
				this.getHandler().actionPerformed(this.getAction(), this);
			}
		}
	}
}
