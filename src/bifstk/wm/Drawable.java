package bifstk.wm;

import bifstk.gl.Color;

/**
 * Immutable view of a Drawable WM widget
 * 
 */
public interface Drawable {

	/**
	 * Renders this Drawable to the display in the current OpenGL context
	 * 
	 * @param alpha overall opacity
	 * @param uiBg base UI background color
	 * @param uiBgAlpha base UI background opacity
	 */
	public void render(float alpha, Color uiBg, float uiBgAlpha);

}
