package bifstk.wm;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Window Manager's internal state
 * 
 */
public class InternalState implements State {

	/**
	 * ordered collection of frames: head of the list is the focused frame, the
	 * others are stacked in order
	 */
	private Deque<Frame> frames = null;

	/**
	 * Default constructor
	 */
	public InternalState() {
		this.frames = new ArrayDeque<Frame>();
	}

	@Override
	public Deque<? extends Drawable> getFrames() {
		return this.frames;
	}

	/**
	 * Creates a new Frame in the WM
	 * 
	 * @param f the frame to add
	 */
	public void addFrame(Frame f) {
		if (this.frames.size() > 0) {
			this.frames.getFirst().setFocused(false);
		}
		// frame is added on top of the stack: focused
		this.frames.addFirst(f);

		f.setFocused(true);
	}

	/**
	 * Give the focus to a frame
	 * 
	 * @param tofocus frame to focus
	 */
	public void focusFrame(Frame tofocus) {
		if (this.frames.size() > 0) {
			this.frames.getFirst().setFocused(false);
		}
		if (tofocus != null) {
			tofocus.setFocused(true);
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
		for (Frame frame : frames) {
			if (frame.contains(x, y)) {
				return frame;
			}
		}
		return null;
	}
}
