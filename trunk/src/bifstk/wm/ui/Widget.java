package bifstk.wm.ui;

import bifstk.wm.Clickable;
import bifstk.wm.Drawable;
import bifstk.wm.geom.Rectangle;

/**
 * Widgets are used to compose the UI held by a Frame
 * <p>
 * 
 */
public abstract class Widget implements Drawable, Clickable {

	/** Container containing this box, or null */
	private Container parent = null;

	/** dimension to attain if possible */
	private Rectangle preferredBounds = new Rectangle();

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
	public abstract void setWidth(int w);

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
	public abstract void setHeight(int h);

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
	public abstract void setBounds(int w, int h);

	/**
	 * @return the current width in pixels of this Widget
	 */
	public abstract int getWidth();

	/**
	 * @return the current height in pixels of this Widget
	 */
	public abstract int getHeight();

	/**
	 * Request for a preferred width for this Widget
	 * <p>
	 * This Widget's container will try its best to satisfy this request, but
	 * there is no guarantee that the actual size (see {@link #getWidth()} will
	 * be the one specified here
	 * 
	 * @param w the preferred width for this widget
	 */
	public void setPreferredWidth(int w) {
		this.preferredBounds.setWidth(w);
	}

	/**
	 * Request for a preferred height for this Widget
	 * <p>
	 * This Widget's container will try its best to satisfy this request, but
	 * there is no guarantee that the actual size (see {@link #getHeight()} will
	 * be the one specified here
	 * 
	 * @param h the preferred height for this widget
	 */
	public void setPreferredHeight(int h) {
		this.preferredBounds.setHeight(h);
	}

	/**
	 * Request for a preferred size for this Widget
	 * <p>
	 * This Widget's container will try its best to satisfy this request, but
	 * there is no guarantee that the actual size (see {@link #getWidth()},
	 * {@link #getHeight()}) will be the one specified here
	 * 
	 * @param w the preferred width for this widget
	 * @param h the preferred height for this widget
	 */
	public void setPreferredBounds(int w, int h) {
		this.preferredBounds.setBounds(w, h);
	}

	/**
	 * @return the preferred width for this widget
	 */
	public int getPreferredWidth() {
		return this.preferredBounds.getWidth();
	}

	/**
	 * @return the preferred height for this widget
	 */
	public int getPreferredHeight() {
		return this.preferredBounds.getHeight();
	}

	/**
	 * Widgets can be put in containers, which have to maintain a Widget ->
	 * Container dependency. A Widget cannot be inserted in two Container
	 * simultaneously.
	 * 
	 * @param c the Container containing this widget
	 */
	public void setParent(Container c) {
		this.parent = c;
	}

	/**
	 * Widgets can be put in containers, which have to maintain a Widget ->
	 * Container dependency. A Widget cannot be inserted in two Container
	 * simultaneously.
	 * 
	 * @return the Container containing this widget, or null
	 */
	public Container getParent() {
		return this.parent;
	}
}
