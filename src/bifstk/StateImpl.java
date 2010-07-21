package bifstk;

import java.util.ArrayDeque;
import java.util.Deque;

import bifstk.type.Drawable;
import bifstk.type.Frame;
import bifstk.type.Window;


public class StateImpl implements State {

	// frames as Frame for internal use
	private Deque<Frame> _frames = null;
	// frames as Drawable for API exposition
	private Deque<Drawable> frames = null;

	public StateImpl() {
		this.frames = new ArrayDeque<Drawable>();
		this._frames = new ArrayDeque<Frame>();
	}

	@Override
	public Deque<Drawable> getFrames() {
		return this.frames;
	}

	public void addFrame(int x, int y) {
		Window f = new Window(x, y);

		if (this._frames.size() > 0) {
			this._frames.getFirst().setFocused(false);
		}
		// frame is added on top of the stack: focused
		this.frames.addFirst(f);
		this._frames.addFirst(f);

		f.setFocused(true);
	}

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

	public Frame findFrame(int x, int y) {
		for (Frame frame : _frames) {
			if (frame.contains(x, y)) {
				return frame;
			}
		}
		return null;
	}
}
