package bifstk.wm.ui;

import org.lwjgl.opengl.GL11;

import bifstk.config.Fonts;
import bifstk.config.Theme;
import bifstk.gl.Color;
import bifstk.gl.Util;

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
		this.text = text;
		this.textWidth = Fonts.getNormal().getWidth(this.text);
		this.textHeight = Fonts.getNormal().getHeight();
	}

	@Override
	public void render(float alpha, Color uiBg, float uiAlpha) {

		int w = this.getWidth();
		int h = this.getHeight();
		float a = uiAlpha * alpha;
		Color fillCol = null;

		if (this.isMouseClicked() && this.isMouseHover()) {
			fillCol = Theme.getUiButtonClickColor();
		} else if (this.isMouseHover()) {
			fillCol = Theme.getUiButtonHoverColor();
		} else {
			fillCol = Theme.getUiButtonColor();
		}

		float[] c1 = fillCol.toArray(4, a);
		int[] v1 = new int[] {
				0, 0, //
				w, 0, //
				w, h, //
				0, h
		};
		Util.draw2D(v1, c1, GL11.GL_QUADS);

		float[] c2 = Theme.getUiButtonBorderColor().toArray(8, a);
		int[] v2 = {
				0, 0, //
				w, 0, //
				w, h, //
				0, h
		};
		Util.draw2DLineLoop(v2, c2);

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
		Color fontCol = Theme.getUiFontColor();
		Fonts.getNormal().drawString(lx, ly, this.text, fontCol, alpha);
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