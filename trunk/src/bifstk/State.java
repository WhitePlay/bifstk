package bifstk;

import java.util.Deque;

import bifstk.wm.Drawable;

/**
 * Provides a immutable view of the window manager's state that can be safely
 * observed by a View component
 */
public interface State {

	/**
	 * The window manager's frames can be viewed as simple Drawable
	 * <p>
	 * their rendering is delegated through {@link Drawable#render()} Frames are
	 * arranged so that the head of the returned list is the top of the stack
	 * (although not necessarily the focused one)
	 * 
	 * @return an ordered double-ended list of Drawable representing the window
	 *         manager's frames
	 */
	public Deque<Drawable> getFrames();

}
