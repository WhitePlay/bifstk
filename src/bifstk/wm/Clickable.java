package bifstk.wm;

/**
 * Defines a component that can receive and process various mouse events
 * 
 */
public interface Clickable {

	/**
	 * The component is being hovered by the mouse cursor
	 * 
	 * @param x abscissa of the mouse within this Clickable's coordinate system
	 * @param y ordinate of the mouse within this Clickable's coordinate system
	 */
	public abstract void mouseHover(int x, int y);

	/**
	 * The component is no longer hovered by the mouse cursor
	 */
	public abstract void mouseOut();

}
