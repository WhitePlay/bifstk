package bifstk.wm;

import java.util.Deque;

/**
 * View of the WM's state
 * 
 * 
 */
public interface State {

	/**
	 * Head of the queue is the focused frame, the rest of the frames are
	 * stacked and ordered by z-index
	 * 
	 * @return a view of the frames handled by the WM as an immutable collection
	 */
	public Deque<? extends Drawable> getFrames();

}
