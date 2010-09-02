package bifstk.wm;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Window Manager's internal state
 * 
 */
public class InternalState implements State {

	/**
	 * ordered collection of frames: head of the list is the foreground frame,
	 * the others are stacked in order
	 */
	private Deque<Frame> frames = null;

	/**
	 * Currently focused frame
	 */
	private Frame focusedFrame = null;

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
		if (f != null) {
			// frame is added on top of the stack: foreground
			this.frames.addFirst(f);
			focusFrame(f);
		}
	}

	/**
	 * Remove an existing Frame from the WM
	 * 
	 * @param f frame to remove
	 */
	public void removeFrame(Frame f) {
		if (f != null) {
			this.frames.remove(f);
		}
	}

	/**
	 * Puts a frame in the foreground and focus it
	 * 
	 * @param tofocus frame to put in foreground, can be null
	 */
	public void foregroundFrame(Frame tofocus) {
		focusFrame(tofocus);
		if (tofocus != null) {
			this.frames.remove(tofocus);
			this.frames.addFirst(tofocus);
		}
	}

	/**
	 * Focus a frame ; do not put if in the foreground
	 * 
	 * @param tofocus frame to focus, can be null
	 */
	public void focusFrame(Frame tofocus) {
		if (this.focusedFrame != null) {
			this.focusedFrame.setFocused(false);
		}
		this.focusedFrame = tofocus;
		if (tofocus != null) {
			tofocus.setFocused(true);
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
