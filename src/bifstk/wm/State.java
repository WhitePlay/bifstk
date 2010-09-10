package bifstk.wm;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * Window Manager's internal state
 * 
 */
public class State {

	/**
	 * ordered collection of windows: head of the list is the foreground window,
	 * the others are stacked in order
	 */
	private Deque<Window> windows = null;

	/**
	 * areas: always behind frames, not ordered
	 */
	private ArrayList<Area> areas = null;

	/**
	 * Currently focused frame
	 */
	private Frame focusedFrame = null;

	/**
	 * Modal window: user can interact only with it while not null
	 */
	private Window modalWindow = null;

	/**
	 * Default constructor
	 */
	public State() {
		this.windows = new ArrayDeque<Window>();
		this.areas = new ArrayList<Area>();
	}

	public Deque<Window> getWindows() {
		return this.windows;
	}

	public List<Area> getAreas() {
		return this.areas;
	}

	public Window getModalWindow() {
		return this.modalWindow;
	}

	/**
	 * The modal window is always focused and on the foreground, if present, it
	 * is the only window that receives user input
	 * 
	 * @param the modal window if there should be one, or null
	 */
	public void setModalWindow(Window f) {
		if (this.modalWindow != null) {
			removeWindow(modalWindow);
		}
		if (f != null) {
			addWindow(f);
		}
		this.modalWindow = f;
	}

	/**
	 * Adds a new Window in the WM
	 * 
	 * @param f the window to add
	 */
	public void addWindow(Window f) {
		if (f != null) {
			// window is added on top of the stack: foreground
			this.windows.addFirst(f);
			focusFrame(f);
		}
	}

	/**
	 * Remove an existing Window from the WM
	 * 
	 * @param f window to remove
	 */
	public void removeWindow(Window f) {
		if (f != null) {
			this.windows.remove(f);
		}
	}

	/**
	 * Adds an Area in the WM
	 * 
	 * @param a the Area to add
	 */
	public void addArea(Area a) {
		if (a != null) {
			this.areas.add(a);
		}
	}

	/**
	 * Removes an Area from the WM
	 * 
	 * @param a the Area to remove
	 */
	public void removeArea(Area a) {
		if (a != null) {
			this.areas.remove(a);
		}
	}

	/**
	 * Puts a frame window in the foreground and focus it
	 * 
	 * @param tofocus window to put in foreground, can be null
	 */
	public void foregroundWindow(Window tofocus) {
		focusFrame(tofocus);
		if (tofocus != null) {
			this.windows.remove(tofocus);
			this.windows.addFirst(tofocus);
		}
	}

	/**
	 * Focus a Frame ; do not put if in the foreground
	 * 
	 * @param tofocus Frame to focus, can be null
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
	 * Finds a Frame in the WM
	 * 
	 * @param x abscissa
	 * @param y ordinate
	 * @return the Frame under the provided coordinates, or null
	 */
	public Frame findFrame(int x, int y) {
		for (Window win : windows) {
			if (win.contains(x, y)) {
				return win;
			}
		}
		for (Area a : areas) {
			if (a.contains(x, y)) {
				return a;
			}
		}
		return null;
	}
}
