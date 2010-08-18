package bifstk.wm.ui;

/**
 * Contains multiple widgets and organizes their layout
 * 
 */
public abstract class Container extends Widget {

	/**
	 * Removes a Widget from this container
	 * 
	 * @param w Widget to remove
	 */
	public abstract void removeChild(Widget w);

	/**
	 * @return true if this Box contains widgets, or false
	 */
	public abstract boolean hasChildren();

	/**
	 * Remove all widgets contained
	 */
	public abstract void clearChildren();

}
