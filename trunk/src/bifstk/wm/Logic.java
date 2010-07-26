package bifstk.wm;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import bifstk.config.Cursors;
import bifstk.config.Cursors.Type;

/**
 * Internal logic of the WM
 * <p>
 * Handles input, propagates changes in the {@link State}
 * 
 */
public class Logic {

	// Privileged view of the WM's state
	private StateImpl state = null;

	// true when an event signified the app should exit
	private boolean exitRequested = false;

	// Internal state of a single mouse button,
	private class MouseButton {
		// true when the left mouse button is down
		boolean down = false;
		// true when the left mouse button just went from up to down
		boolean clicked = false;
		// true when the mouse cursor is moving while the left mouse button is
		// down
		boolean dragged = false;
		// true if mouse was dragged last time {@link #update()} was called
		boolean lastDragged = false;
		// frame being dragged if dragged == true
		Frame draggedFrame = null;
		// true when the mouse button was down last time {@link #update()}
		// was called
		boolean lastPollDown = false;
		// X position of the mouse cursor when it was last clicked
		int clickX = 0;
		// Y position of the mouse cursor when it was last clicked
		int clickY = 0;
		// X position of the dragged frame when it was clicked
		int dragX = 0;
		// Y position of the dragged frame when it was clicked
		int dragY = 0;
	}

	// state of the left mouse button
	private MouseButton leftMouse = null;

	// state of the right mouse button
	// private MouseButton rightMouse = null;
	// state of the center mouse button
	// private MouseButton centerMouse = null;

	/**
	 * Default constructor
	 */
	public Logic() {
		this.state = new StateImpl();
		this.exitRequested = false;
		this.leftMouse = new MouseButton();
		// this.rightMouse = new MouseButton();
		// this.centerMouse = new MouseButton();
	}

	/**
	 * @return true if the user inputs signified the app should exit
	 */
	public boolean isExitRequested() {
		return (this.exitRequested || Display.isCloseRequested());
	}

	/**
	 * @return an immutable view of the WM's state
	 */
	public State getState() {
		return this.state;
	}

	/**
	 * Updates the logic's state: polls input, modifies WM state
	 */
	public void update() {
		updateKeyboard();
		updateMouse();

		cursorHover();
		applyMouse();
	}

	/**
	 * Polls keyboard events
	 */
	private void updateKeyboard() {
		while (Keyboard.next()) {
			int event = Keyboard.getEventKey();

			switch (event) {

			case Keyboard.KEY_ESCAPE:
				this.exitRequested = true;
				break;

			case Keyboard.KEY_C:
				if (Keyboard.getEventKeyState()) {
					this.state.addFrame(50, 50);
				}
				break;

			}
		}
	}

	/**
	 * Polls mouse events
	 */
	private void updateMouse() {
		this.leftMouse.clicked = false;
		this.leftMouse.lastPollDown = this.leftMouse.down;

		while (Mouse.next()) {
			int button = Mouse.getEventButton();

			switch (button) {
			// left
			case 0:
				if (this.leftMouse.down) {
					this.leftMouse.down = false;
				} else {
					this.leftMouse.down = true;
					this.leftMouse.clicked = true;
					this.leftMouse.clickX = getMouseX();
					this.leftMouse.clickY = getMouseY();
				}
				break;
			// right
			case 1:

				break;
			// middle
			case 2:

				break;
			}
		}

		Mouse.poll();
		int leftDx = getMouseX() - this.leftMouse.clickX;
		int leftDy = getMouseY() - this.leftMouse.clickY;

		boolean leftDiffPos = (leftDx != 0) && (leftDy != 0);

		/*
		 * the mouse is dragged if it was down the last time this method was
		 * called, and the mouse's position diff is > 0
		 */
		if (leftMouse.lastPollDown && leftMouse.down && leftDiffPos) {
			leftMouse.lastDragged = leftMouse.dragged;
			leftMouse.dragged = true;
		} else if (leftMouse.dragged && !leftMouse.down) {
			leftMouse.lastDragged = leftMouse.dragged;
			leftMouse.dragged = false;
		} else if (!leftMouse.lastPollDown && leftMouse.down && leftDiffPos) {
			leftMouse.lastDragged = leftMouse.dragged;
			leftMouse.dragged = true;
		} else if (!leftMouse.lastPollDown && leftMouse.down) {
			leftMouse.lastDragged = leftMouse.dragged;
			leftMouse.dragged = false;
		} else {
			leftMouse.lastDragged = false;
		}
	}

	/**
	 * Applies mouse events
	 */
	private void applyMouse() {
		if (this.leftMouse.clicked) {
			Frame f = this.state.findFrame(this.leftMouse.clickX,
					this.leftMouse.clickY);
			this.state.focusFrame(f);

			if (f != null) {
				this.leftMouse.draggedFrame = f;
				this.leftMouse.dragX = f.getX();
				this.leftMouse.dragY = f.getY();
			} else {
				this.leftMouse.draggedFrame = null;
			}
		}
		if (this.leftMouse.dragged) {
			Frame dragged = this.leftMouse.draggedFrame;
			if (dragged != null) {
				if (!this.leftMouse.lastDragged) {
					dragged.setDragged(true);
					Cursors.setCursor(Type.MOVE);
				}
				int nx = getMouseX() - (leftMouse.clickX - leftMouse.dragX);
				int ny = getMouseY() - (leftMouse.clickY - leftMouse.dragY);
				dragged.setPos(nx, ny);
			}
		} else if (this.leftMouse.lastDragged) {
			Frame dragged = this.leftMouse.draggedFrame;
			if (dragged != null) {
				dragged.setDragged(false);
				Cursors.setCursor(Type.POINTER);
			}
		}
	}

	/**
	 * Changes the mouse cursor depending what it's currently hovering
	 */
	private void cursorHover() {
		if (this.leftMouse.dragged) {
			return;
		}

		int mx = getMouseX();
		int my = getMouseY();

		Frame f = this.state.findFrame(mx, my);
		if (f == null) {
			Cursors.setCursor(Type.POINTER);
			return;
		}

		switch (f.getRegion(mx, my)) {
		case CONTENT:
		case TITLE:
		case OUT:
			Cursors.setCursor(Type.POINTER);
			break;
		case TOP_LEFT:
			Cursors.setCursor(Type.RESIZE_TOP_LEFT);
			break;
		case TOP:
			Cursors.setCursor(Type.RESIZE_TOP);
			break;
		case TOP_RIGHT:
			Cursors.setCursor(Type.RESIZE_TOP_RIGHT);
			break;
		case LEFT:
			Cursors.setCursor(Type.RESIZE_LEFT);
			break;
		case RIGHT:
			Cursors.setCursor(Type.RESIZE_RIGHT);
			break;
		case BOT_LEFT:
			Cursors.setCursor(Type.RESIZE_BOT_LEFT);
			break;
		case BOT:
			Cursors.setCursor(Type.RESIZE_BOT);
			break;
		case BOT_RIGHT:
			Cursors.setCursor(Type.RESIZE_BOT_RIGHT);
			break;
		}
	}

	private static int getMouseX() {
		return Mouse.getX();
	}

	private static int getMouseY() {
		return Display.getDisplayMode().getHeight() - Mouse.getY();
	}
}