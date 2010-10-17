package bifstk.wm;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import org.lwjgl.opengl.Display;

import bifstk.config.Config;
import bifstk.config.Theme;
import bifstk.gl.Util;
import bifstk.util.Logger;
import bifstk.util.SharedFrameException;

/**
 * Window Manager's internal state
 * 
 */
public class State {

	/**
	 * ordered collection of windows: head of the list is the foreground window,
	 * the others are stacked in order
	 */
	private LinkedList<Window> windows = null;

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
	 * Ordered left dock for Windows
	 */
	private LinkedList<Window> leftDock = null;

	/** current width of the left dock */
	private int leftDockWidth = 150;

	/**
	 * Ordered right dock for Windows
	 */
	private LinkedList<Window> rightDock = null;

	/** current width of the right dock */
	private int rightDockWidth = 150;

	/** Used for code factorizing the left and right dock */
	public static enum DockPosition {
		LEFT, RIGHT
	}

	/**
	 * Default constructor
	 */
	public State() {
		this.windows = new LinkedList<Window>();
		this.areas = new ArrayList<Area>();
		this.leftDock = new LinkedList<Window>();
		this.rightDock = new LinkedList<Window>();
	}

	/**
	 * Every Window is stacked in the WM as ordered in this list, with the head
	 * being the one on the foreground
	 * 
	 * @return a double linked list containing the Window handled by the WM
	 */
	public Deque<Window> getWindows() {
		return this.windows;
	}

	/**
	 * The left dock contains a group of window of constrained size, docked at
	 * the left of the screen
	 * 
	 * @return an ordered list of the Windows contained in the left dock
	 */
	public List<Window> getLeftDock() {
		return this.leftDock;
	}

	/**
	 * The right dock contains a group of window of constrained size, docked at
	 * the right of the screen
	 * 
	 * @return an ordered list of the Windows contained in the right dock
	 */
	public List<Window> getRightDock() {
		return this.rightDock;
	}

	/**
	 * Area object are not positioned by the user and should not overlap: the
	 * order of the list returned has no particular meaning
	 * 
	 * @return a list containing the Area handled by the WM
	 */
	public List<Area> getAreas() {
		return this.areas;
	}

	/**
	 * The modal window is always focused and on the foreground, if present, it
	 * is the only window that receives user input
	 * 
	 * @return the current modal Window, or null
	 */
	public Window getModalWindow() {
		return this.modalWindow;
	}

	/**
	 * The modal window is always focused and on the foreground, if present, it
	 * is the only window that receives user input
	 * 
	 * @param the modal window if there should be one, or null
	 * @throws SharedFrameException the Window is already held by the WM
	 */
	public void setModalWindow(Window f) {
		checkHasWindow(f);

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
	 * @throws SharedFrameException the Window is already held by the WM
	 */
	public void addWindow(Window f) {
		checkHasWindow(f);

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
	 * @return true if a Window was removed from the WM
	 */
	public boolean removeWindow(Window f) {
		if (f != null) {
			return this.windows.remove(f);
		}
		return false;
	}

	/**
	 * Adds an Area in the WM
	 * 
	 * @param a the Area to add
	 * @throws SharedFrameException the Area is already held by the WM
	 */
	public void addArea(Area a) {
		checkHasArea(a);

		if (a != null) {
			this.areas.add(a);
		}
	}

	/**
	 * Removes an Area from the WM
	 * 
	 * @param a the Area to remove
	 * @return true if an Area was removed from the WM
	 */
	public boolean removeArea(Area a) {
		if (a != null) {
			return this.areas.remove(a);
		}
		return false;
	}

	/**
	 * Add a Window to the left dock at the specified position
	 * 
	 * @param w the Window to add to the left dock
	 * @param dockPos left or right dock
	 * @return true if the Window was added, false when not enough space
	 * @throws SharedFrameException the Window is already held by the WM
	 */
	public void addToDock(Window w, DockPosition dockPos) {
		checkHasWindow(w);

		LinkedList<Window> dock = null;
		int xOrigin;
		int dockWidth;
		if (dockPos.equals(DockPosition.LEFT)) {
			dock = this.leftDock;
			xOrigin = 0;
			dockWidth = this.leftDockWidth;
		} else {
			dock = this.rightDock;
			xOrigin = Display.getDisplayMode().getWidth() - this.rightDockWidth;
			dockWidth = this.rightDockWidth;
		}

		if (w != null) {
			int pos = 0;
			// find the position in the dock depending the ordinate of the
			// Window
			for (Window win : dock) {
				if (w.getY() < win.getY() + win.getHeight() / 2) {
					break;
				}
				pos++;
			}
			if (w.getY() + w.getHeight() == Display.getDisplayMode()
					.getHeight()) {
				pos = dock.size();
			}

			dock.add(pos, w);
			focusFrame(w);

			int border = Theme.getWindowBorderWidth();
			int num = dock.size();
			int i = 0, acc = 0, totPix = 0;
			int pixels = Display.getDisplayMode().getHeight() / num;
			int hMin = Config.getWmFrameSizeMin();

			int[] pixWin = new int[num];
			// find out how much pixels each window can give
			for (Window win : dock) {
				int h = win.getHeight() - hMin;
				pixWin[i++] = h;
				totPix += h;
			}
			if (totPix < hMin && num > 1) {
				Logger.error("Not enough space available to insert "
						+ w.getTitle() + " in dock " + dockPos);
			}
			pixels = Math.min(totPix, pixels);
			int share = 0;

			if (num > 1) {
				share = pixels / (num - 1);
				// resize windows that cannot give a share
				makeSpace(dock, share, w);
			}
			i = acc = 0;
			// resize all Windows in the dock accordingly
			for (Window win : dock) {
				int h = Display.getDisplayMode().getHeight();
				if (num > 1) {
					h = win.getHeight() - share;
				}
				if (win == w) {
					h = pixels - border;
				}
				i++;
				if (i == num) {
					h = Display.getDisplayMode().getHeight() - acc;
				}

				win.setResizable(true);
				win.setPos(xOrigin, acc);
				win.setBounds(dockWidth, h);
				// sometimes the first setPos() is clamped
				// due to the height of the win
				win.setPos(xOrigin, acc);
				acc += h + border;
				win.setResizable(false);
			}
		}

		return true;
	}

	/**
	 * Resize the dock's windows to allow the insertion of a new window
	 * 
	 * @param dock
	 * @param share
	 * @param skip
	 */
	private void makeSpace(LinkedList<Window> dock, int share, Window skip) {
		int hMin = Config.getWmFrameSizeMin();
		int i = 0;

		for (Window w : dock) {
			if (w.equals(skip))
				continue;

			int j = 0;
			while (w.getHeight() - hMin < share) {
				for (Window w2 : dock) {
					if (w2.equals(skip) || w2.equals(w))
						continue;
					int px = w2.getHeight() - hMin - share;
					// w2 will give px height to w
					if (px > 0) {
						px = Math.min(px, share);
						w2.setResizable(true);
						w.setResizable(true);

						w2.setHeight(w2.getHeight() - px);
						w.setHeight(w.getHeight() + px);

						if (w.getY() < w2.getY()) {
							w2.setY(w2.getY() - px);
						} else {
							w.setY(w.getY() - px);
						}

						w2.setResizable(false);
						w.setResizable(false);
					}
				}

				if (j++ > 20) {
					return;
				}
			}

			i++;
		}
	}

	/**
	 * Remove a Window from the left dock
	 * 
	 * @param w the Window to remove
	 * @param dockPos left or right dock
	 * @return true if a Window was removed from the left dock
	 */
	public boolean removeFromDock(Window w, DockPosition dockPos) {
		if (w != null) {

			LinkedList<Window> dock = null;
			if (dockPos.equals(DockPosition.LEFT)) {
				dock = this.leftDock;
			} else {
				dock = this.rightDock;
			}

			boolean ret = dock.remove(w);
			w.setResizable(true);
			w.setHeight(w.getHeight());
			w.setResizable(false);

			int border = Theme.getWindowBorderWidth();
			int pixels = w.getHeight() + border;
			int num = dock.size();
			int i = 0;
			int acc = 0;

			// redistribute removed pixels to the remaining windows
			for (Window win : dock) {
				int h = win.getHeight() + pixels / num;
				if (++i == num) {
					h = Display.getDisplayMode().getHeight() - acc;
				}

				win.setResizable(true);
				win.setY(acc);
				win.setHeight(h);
				win.setResizable(false);
				acc += h + border;
			}

			return ret;
		}
		return false;
	}

	/**
	 * @param pos the left or the right dock
	 * @return the current width of the specified dock
	 */
	public int getDockWidth(DockPosition pos) {
		if (pos.equals(DockPosition.LEFT)) {
			return this.leftDockWidth;
		} else {
			return this.rightDockWidth;
		}
	}

	/**
	 * @param w the new width of the specified dock
	 * @param pos the left or right dock
	 */
	public void setDockWidth(int w, DockPosition pos) {
		int val = Util
				.clampi(w, Config.getWmFrameSizeMin(), Display.getDisplayMode()
						.getWidth() / 2 - Theme.getWindowBorderWidth());
		List<Window> dock = null;

		int dx = 0;
		if (pos.equals(DockPosition.LEFT)) {
			this.leftDockWidth = val;
			dock = this.leftDock;
		} else {
			dx += (this.rightDockWidth - val);
			this.rightDockWidth = val;
			dock = this.rightDock;
		}

		for (Window win : dock) {
			win.setResizable(true);
			if (dx < 0) {
				win.setX(win.getX() + dx);
			}
			win.setWidth(val);
			if (dx > 0) {
				win.setX(win.getX() + dx);
			}
			win.setResizable(false);
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
			if (this.windows.remove(tofocus)) {
				this.windows.addFirst(tofocus);
			}
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

			if (this.leftDock.contains(focusedFrame)) {
				for (Window w : leftDock) {
					w.setFocused(false);
				}
			} else if (this.rightDock.contains(focusedFrame)) {
				for (Window w : rightDock) {
					w.setFocused(false);
				}
			}
		}
		this.focusedFrame = tofocus;
		if (tofocus != null) {
			tofocus.setFocused(true);

			if (this.leftDock.contains(tofocus)) {
				for (Window w : leftDock) {
					w.setFocused(true);
				}
			} else if (this.rightDock.contains(tofocus)) {
				for (Window w : rightDock) {
					w.setFocused(true);
				}
			}
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
		for (Window win : leftDock) {
			if (win.contains(x, y)) {
				return win;
			}
		}
		for (Window win : rightDock) {
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

	/**
	 * Checks this Window is not already held by the WM
	 * 
	 * @param w a Window
	 * @throws SharedFrameException w is already held by this State
	 */
	private void checkHasWindow(Window w) throws SharedFrameException {
		if (w == null) {
			return;
		}
		if (this.windows.contains(w)) {
			throw new SharedFrameException("Window already held by the WM");
		}
		if (w.equals(this.modalWindow)) {
			throw new SharedFrameException(
					"Window is the current WM modal Window");
		}
		if (this.leftDock.contains(w)) {
			throw new SharedFrameException(
					"Window already held by the left dock");
		}
		if (this.rightDock.contains(w)) {
			throw new SharedFrameException(
					"Window already held by the right dock");
		}
	}

	/**
	 * Checks this Area is not already held by the WM
	 * 
	 * @param a an Area
	 * @throws SharedFrameException a is already held by this State
	 */
	private void checkHasArea(Area a) throws SharedFrameException {
		if (a == null) {
			return;
		}
		if (this.areas.contains(a)) {
			throw new SharedFrameException("Area already held by the WM");
		}
	}
}
