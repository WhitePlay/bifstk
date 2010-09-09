package bifstk.wm;

import bifstk.gl.Color;

/**
 * Immutable view of a Drawable WM widget
 * 
 */
public interface Drawable {

	/**
	 * renders this Drawable to the OpenGL display
	 * 
	 * @param alpha opacity [0.0, 1.0]
	 * @param uiBg base UI bg color
	 * @param uiBgAlpha opacity of the UI bg
	 */
	public void render(float alpha, Color uiBg, float uiBgAlpha);

}
