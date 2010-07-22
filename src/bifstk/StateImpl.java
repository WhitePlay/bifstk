package bifstk;

import java.util.ArrayDeque;
import java.util.Deque;

import bifstk.wm.Drawable;
import bifstk.wm.Frame;

/**
 * Window Manager's internal state
 * <p>
 * Maintains the window manager's Frames in two separate collections with
 * different visibilities
 * 
 */
public class StateImpl implements State {

	/** frames as Frame for internal use */
	private Deque<Frame> _frames = null;
	/** frames as Drawable for API exposition */
	private Deque<Drawable> frames = null;

	public StateImpl() {
		this.frames = new ArrayDeque<Drawable>();
		this._frames = new ArrayDeque<Frame>();
	}

	@Override
	public Deque<Drawable> getFrames() {
		return this.frames;
	}

	/**
	 * Creates a new Frame in the WM
	 * 
	 * @param x
	 * @param y
	 */
	public void addFrame(int x, int y) {
		Frame f = new Frame(x, y);

		if (this._frames.size() > 0) {
			this._frames.getFirst().setFocused(false);
		}
		// frame is added on top of the stack: focused
		this.frames.addFirst(f);
		this._frames.addFirst(f);

		f.setFocused(true);
	}

	/**
	 * Give the focus to a frame
	 * 
	 * @param tofocus frame to focus
	 */
	public void focusFrame(Frame tofocus) {
		if (this._frames.size() > 0) {
			this._frames.getFirst().setFocused(false);
		}
		if (tofocus != null) {
			tofocus.setFocused(true);
			this._frames.remove(tofocus);
			this._frames.addFirst(tofocus);
			this.frames.remove(tofocus);
			this.frames.addFirst(tofocus);
		}
	}

	/**
	 * Finds a frame in the WM
	 * 
	 * @param x abscissa
	 * @param y ordinate
	 * @return the frame under the provided coordinates, or null
	 */
	public Frame findFrame(int x, int y) {
		for (Frame frame : _frames) {
			if (frame.contains(x, y)) {
				return frame;
			}
		}
		return null;
	}
}
