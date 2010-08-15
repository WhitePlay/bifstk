package bifstk.wm.ui;

/**
 * Contains multiple widgets and organizes their layout
 * 
 */
public interface Container extends Widget {

	/**
	 * Adds a Widget in this container
	 * 
	 * @param w Widget to add in this container
	 */
	public void addChild(Widget w);

	/**
	 * Removes a Widget from this container
	 * 
	 * @param w Widget to remove
	 */
	public void removeChild(Widget w);

	/**
	 * @return true if this Box contains widgets, or false
	 */
	public boolean hasChildren();

	/**
	 * Remove all widgets contained
	 */
	public void clearChildren();

}
