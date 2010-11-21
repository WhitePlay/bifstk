package bifstk.wm.ui;

import bifstk.wm.Clickable;
import bifstk.wm.Drawable;
import bifstk.wm.Frame;

/**
 * Widgets are used to compose the UI held by a Frame
 * <p>
 * 
 */
public abstract class Widget implements Drawable, Clickable {

	/** Container containing this box, or null */
	private Container parent = null;

	/** frame this widget is the content of, or null */
	private Frame frame = null;

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
	 * @param max the maximum width that can be returned by this method
	 * @return the preferred width for this widget
	 */
	public abstract int getPreferredWidth(int max);

	/**
	 * @param the maximum height that can be returned by this method
	 * @return the preferred height for this widget
	 */
	public abstract int getPreferredHeight(int max);

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

	/**
	 * A widget added to a Frame as {@link Frame#setContent(Widget)} holds a
	 * reference to its Frame. Other widgets this widget may contain do not hold
	 * this reference, but it can be inferred through the {@link #getParent()}
	 * and {@link #getFrame()}
	 * 
	 * @param f the Frame holding this Widget as content
	 */
	public void setFrame(Frame f) {
		this.frame = f;
	}

	/**
	 * @return the Frame containing this Widget
	 */
	public Frame getFrame() {
		if (this.frame == null) {
			if (this.getParent() == null) {
				return null;
			} else {
				return this.getParent().getFrame();
			}
		} else {
			return this.frame;
		}
	}
}
