package bifstk.wm;

/**
 * Immutable view of a Drawable WM widget
 * 
 */
public interface Drawable {

	/**
	 * renders this Drawable to the OpenGL display
	 */
	public void render();

	/**
	 * @return the abscissa of the bottom left corner of this widget
	 */
	public int getX();

	/**
	 * @return the ordinate of the bottom left corner of this widget
	 */
	public int getY();

	/**
	 * @return the current width of this widget
	 */
	public int getWidth();

	/**
	 * @return the current height of this widget
	 */
	public int getHeight();

}
