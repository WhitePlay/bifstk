package bifstk.wm.ui;

/**
 * Contains multiple widgets and organizes their layout
 * <p>
 * Upon insertion, a Container should ensure that a Widget is not contained in
 * multiple Container, using {@link #add(Widget)}
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

	/**
	 * Should be called by all Container upon insertion of a new widget to
	 * ensure a Widget is not shared among several Container
	 * 
	 * @param w Widget to make unique among Container
	 */
	protected void add(Widget w) {
		if (w == null) {
			return;
		}
		Container parent = w.getParent();
		if (parent != null) {
			parent.removeChild(w);
		}
		w.setParent(this);
	}

	/**
	 * Explicitely request an update of the intenal layout.
	 */
	protected abstract void update();

}
