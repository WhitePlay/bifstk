package bifstk.wm.ui;

import org.lwjgl.opengl.GL11;

import bifstk.config.Fonts;
import bifstk.gl.Color;
import bifstk.wm.geom.Rectangle;

/**
 * Display text in a Widget
 * 
 */
public class Label extends Widget {

	/** dimensions of the label */
	private Rectangle bounds = null;
	/** text to display in the label */
	private String text = null;
	/** pixel width of the string when rendered */
	private int textWidth = 0;
	/** pixel height of the string when rendered */
	private int textHeight = 0;

	/**
	 * Default constructor
	 * 
	 * @param text text to display in the label
	 */
	public Label(String text) {
		this.text = text;
		this.bounds = new Rectangle();

		this.textWidth = Fonts.getNormal().getWidth(this.text);
		this.textHeight = Fonts.getNormal().getHeight();
	}

	@Override
	public void render(float alpha, Color uiBg, float uiAlpha) {

		int w = this.getWidth();
		int h = this.getHeight();

		uiBg.use(alpha * uiAlpha);
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
		Fonts.getNormal().drawString(lx, ly, this.text, Color.BLACK, alpha);

	}

	/**
	 * @param t the text to display in this Label
	 */
	public void setText(String t) {
		this.text = t;
		this.textWidth = Fonts.getNormal().getWidth(this.text);
	}

	/**
	 * @return the text currently displayed in this Label
	 */
	public String getText() {
		return this.text;
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
	}

	@Override
	public void mouseOut() {
	}

	@Override
	public void mouseDown(int button) {
	}

	@Override
	public void mouseUp(int button, int x, int y) {
	}
}
