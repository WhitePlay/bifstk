package bifstk.wm.ui;

import bifstk.wm.Drawable;

/**
 * Widgets are used to compose the UI held by a Frame
 * <p>
 * 
 */
public interface Widget extends Drawable {

	/**
	 * The geometry of a Widget is handled by the frame holding it, or its
	 * parent widget when using containers.
	 * <p>
	 * When a Widget is resized by its parent, it uses this method to propagate
	 * the changes to its children.
	 * <p>
	 * Do not confuse this method for a layout manager: it does not set the
	 * 'preferred' size of the widget but the size it should have right now to
	 * be displayed correctly in its container.
	 * <p>
	 * Instead of calling consecutive {@link #setWidth(int)} and
	 * {@link #setHeight(int)}, prefer {@link #setBounds(int, int)} as a call to
	 * each of these methods recursively calls its children
	 * 
	 * @param w new width in pixels for this Widget
	 */
	public void setWidth(int w);

	/**
	 * The geometry of a Widget is handled by the frame holding it, or its
	 * parent widget when using containers.
	 * <p>
	 * When a Widget is resized by its parent, it uses this method to propagate
	 * the changes to its children.
	 * <p>
	 * Do not confuse this method for a layout manager: it does not set the
	 * 'preferred' size of the widget but the size it should have right now to
	 * be displayed correctly in its container.
	 * <p>
	 * Instead of calling consecutive {@link #setWidth(int)} and
	 * {@link #setHeight(int)}, prefer {@link #setBounds(int, int)} as a call to
	 * each of these methods recursively calls its children
	 * 
	 * @param h new height in pixels for this Widget
	 */
	public void setHeight(int h);

	/**
	 * The geometry of a Widget is handled by the frame holding it, or its
	 * parent widget when using containers.
	 * <p>
	 * When a Widget is resized by its parent, it uses this method to propagate
	 * the changes to its children.
	 * <p>
	 * Do not confuse this method for a layout manager: it does not set the
	 * 'preferred' size of the widget but the size it should have right now to
	 * be displayed correctly in its container.
	 * <p>
	 * Instead of calling consecutive {@link #setWidth(int)} and
	 * {@link #setHeight(int)}, prefer {@link #setBounds(int, int)} as a call to
	 * each of these methods recursively calls its children
	 * 
	 * @param w new width in pixels for this Widget
	 * @param h new height in pixels for this Widget
	 */
	public void setBounds(int w, int h);

	/**
	 * @return the current width in pixels of this Widget
	 */
	public int getWidth();

	/**
	 * @return the current height in pixels of this Widget
	 */
	public int getHeight();

}
