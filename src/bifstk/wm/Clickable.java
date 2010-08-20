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

	/**
	 * One mouse button has been clicked and is down
	 * <p>
	 * Location of the click can be determined using
	 * {@link #mouseHover(int, int)}
	 * 
	 * @param button Mouse button: 0 for left, 1 for right, 2 for center
	 */
	public abstract void mouseDown(int button);

	/**
	 * One mouse button has been released and is up
	 * <p>
	 * 
	 * @param button Mouse button: 0 for left, 1 for right, 2 for center
	 * @param x abscissa of the mouse upon release, in this Clickable's
	 *            coordinate system
	 * @param y ordinate of the mouse upon release, in this Clickable's
	 *            coordinate system
	 */
	public abstract void mouseUp(int button, int x, int y);

}
