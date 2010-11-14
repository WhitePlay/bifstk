package bifstk.wm.ui;

import org.lwjgl.opengl.GL11;

import bifstk.config.Fonts;
import bifstk.config.Theme;
import bifstk.gl.Color;

/**
 * Stateless button that can display text
 * 
 */
public class Button extends AbstractButton {

	/** text to display in the button */
	private String text = null;
	/** pixel width of the string when rendered */
	private int textWidth = 0;
	/** pixel height of the string when rendered */
	private int textHeight = 0;

	/**
	 * Default constructor
	 * 
	 * @param text text to display in the button
	 */
	public Button(String text) {
		super();
		this.text = text;

		this.textWidth = Fonts.getNormal().getWidth(this.text);
		this.textHeight = Fonts.getNormal().getHeight();
	}

	@Override
	public void render(float alpha, Color uiBg, float uiAlpha) {

		int w = this.getWidth();
		int h = this.getHeight();
		float a = uiAlpha * alpha;
		Color col = Theme.getUiFontColor();

		if (this.isMouseClicked() && this.isMouseHover()) {
			Theme.getUiButtonClickColor().use(a);
		} else if (this.isMouseHover()) {
			Theme.getUiButtonHoverColor().use(a);
		} else {
			Theme.getUiButtonColor().use(a);
		}
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glVertex2i(0, 0);
		GL11.glVertex2i(w, 0);
		GL11.glVertex2i(w, h);
		GL11.glVertex2i(0, h);
		GL11.glEnd();

		Theme.getUiButtonBorderColor().use(a);
		GL11.glBegin(GL11.GL_LINES);
		GL11.glVertex2i(0, 1);
		GL11.glVertex2i(w, 1);

		GL11.glVertex2i(w, 1);
		GL11.glVertex2i(w, h);

		GL11.glVertex2i(w, h);
		GL11.glVertex2i(1, h);

		GL11.glVertex2i(1, h);
		GL11.glVertex2i(0, 1);
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
		Fonts.getNormal().drawString(lx, ly, this.text, col, alpha);
	}

	@Override
	public int getPreferredWidth() {
		return Fonts.getNormal().getWidth(this.text + "  ");
	}

	@Override
	public int getPreferredHeight() {
		return Fonts.getNormal().getHeight() + 4;
	}

}
