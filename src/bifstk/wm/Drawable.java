package bifstk.wm;

/**
 * Immutable view of a Drawable WM widget
 * 
 */
public interface Drawable {

	/**
	 * renders this Drawable to the OpenGL display
	 * 
	 * @param alpha opacity [0.0, 1.0]
	 */
	public void render(float alpha);

}
