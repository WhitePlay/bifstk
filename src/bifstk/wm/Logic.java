package bifstk.wm;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import bifstk.Handler;
import bifstk.config.Config;
import bifstk.config.Cursors;
import bifstk.config.Cursors.Type;
import bifstk.config.Property;
import bifstk.wm.geom.Region;

/**
 * Internal logic of the WM
 * <p>
 * Handles input, propagates changes in the {@link InternalState} upon a call to
 * {@link #update()}
 * 
 */
public class Logic {

	/** Privileged view of the WM's state */
	private InternalState state = null;

	/** Client side event handler */
	private Handler handler = null;

	/** true when an event signified the app should exit */
	private boolean exitRequested = false;

	/** Internal state of a single mouse button */
	private class MouseButton {
		/** true when the left mouse button is down */
		boolean down = false;
		/**
		 * true when the mouse button was down last time {@link #update()} was
		 * called
		 */
		boolean downLastPoll = false;

		/** currently hovered frame */
		Frame hoverFrame = null;
		/** region of the hovered framed currently hovered */
		Region hoverRegion = Region.OUT;
		/** X position of the mouse cursor currently hovered */
		int hoverX = 0;
		/** Y position of the mouse cursor currently hovered */
		int hoverY = 0;

		/** true when the left mouse button just went from up to down */
		boolean clicked = false;
		/** X position of the mouse cursor when it was last clicked */
		int clickX = 0;
		/** Y position of the mouse cursor when it was last clicked */
		int clickY = 0;
		/** width of the dragged frame when it was clicked */
		int clickWidth = 0;
		/** height of the dragged frame when it was clicked */
		int clickHeight = 0;
		/** hovered frame when the mouse was clicked */
		Frame clickedFrame = null;
		/** region of the hovered framed when the mouse was clicked */
		Region clickedRegion = Region.OUT;

		/**
		 * true when the mouse cursor is moving while the left mouse button is
		 * down
		 */
		boolean dragged = false;
		/** true if mouse was dragged last time {@link #update()} was called */
		boolean draggedLastPoll = false;
		/** frame being dragged if dragged == true */
		Frame draggedFrame = null;
		/** X position of the dragged frame when it was clicked */
		int dragX = 0;
		/** Y position of the dragged frame when it was clicked */
		int dragY = 0;

	}

	/** state of the left mouse button */
	private MouseButton leftMouse = null;

	// state of the right mouse button
	// private MouseButton rightMouse = null;
	// state of the center mouse button
	// private MouseButton centerMouse = null;

	/**
	 * Default constructor
	 * 
	 * @param h user event handler, can be null
	 */
	public Logic(Handler h) {
		this.handler = h;
		this.state = new InternalState();
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
	public InternalState getState() {
		return this.state;
	}

	/**
	 * Updates the logic's state: polls input, modifies WM state
	 */
	public void update() {
		updateKeyboard();
		updateMouse();

		applyHoveringCursor();
		applyMouse();
	}

	/**
	 * Polls keyboard events
	 */
	private void updateKeyboard() {
		while (Keyboard.next()) {
			if (this.handler != null) {
				this.handler.keyEvent(Keyboard.getEventKey(),
						Keyboard.areRepeatEventsEnabled(),
						Keyboard.getEventCharacter());
			}
		}
	}

	/**
	 * Polls mouse events
	 */
	private void updateMouse() {
		// reset temporary values
		this.leftMouse.clicked = false;
		this.leftMouse.downLastPoll = this.leftMouse.down;

		// hovering state: prevents multiple calls for clicks/drags
		int mx = this.leftMouse.hoverX = getMouseX();
		int my = this.leftMouse.hoverY = getMouseY();
		this.leftMouse.hoverFrame = this.state.findFrame(mx, my);
		if (this.leftMouse.hoverFrame != null) {
			this.leftMouse.hoverRegion = this.leftMouse.hoverFrame.getRegion(
					mx, my);
		}

		// for each mouse event since last call
		while (Mouse.next()) {
			int button = Mouse.getEventButton();

			switch (button) {
			// left
			case 0:
				// mouse down
				if (this.leftMouse.down) {
					this.leftMouse.down = false;
				}
				// mouse clicked
				else {
					this.leftMouse.down = true;
					this.leftMouse.clicked = true;
					this.leftMouse.clickX = mx;
					this.leftMouse.clickY = my;
					this.leftMouse.clickedFrame = this.leftMouse.hoverFrame;
					this.leftMouse.clickedRegion = this.leftMouse.hoverRegion;
					if (this.leftMouse.clickedFrame != null) {
						this.leftMouse.clickWidth = this.leftMouse.clickedFrame
								.getWidth();
						this.leftMouse.clickHeight = this.leftMouse.clickedFrame
								.getHeight();
					}
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
		int leftDx = mx - this.leftMouse.clickX;
		int leftDy = my - this.leftMouse.clickY;

		boolean leftDiffPos = (leftDx != 0) && (leftDy != 0);

		/*
		 * the mouse is dragged if it was down the last time this method was
		 * called, and the mouse's position diff is > 0
		 */
		if (leftMouse.downLastPoll && leftMouse.down && leftDiffPos) {
			leftMouse.draggedLastPoll = leftMouse.dragged;
			leftMouse.dragged = true;
		} else if (leftMouse.dragged && !leftMouse.down) {
			leftMouse.draggedLastPoll = leftMouse.dragged;
			leftMouse.dragged = false;
		} else if (!leftMouse.downLastPoll && leftMouse.down && leftDiffPos) {
			leftMouse.draggedLastPoll = leftMouse.dragged;
			leftMouse.dragged = true;
		} else if (!leftMouse.downLastPoll && leftMouse.down) {
			leftMouse.draggedLastPoll = leftMouse.dragged;
			leftMouse.dragged = false;
		} else {
			leftMouse.draggedLastPoll = false;
		}
	}

	/**
	 * Applies mouse events
	 */
	private void applyMouse() {
		boolean focusFollowMouse = new Boolean(
				Config.getValue(Property.wmFocuseFollowmouse));

		if (focusFollowMouse && !this.leftMouse.clicked
				&& !this.leftMouse.dragged) {
			if (this.leftMouse.hoverFrame != null) {
				this.state.focusFrame(this.leftMouse.hoverFrame);
			}
		}

		if (this.leftMouse.clicked) {
			Frame f = this.leftMouse.clickedFrame;
			this.state.foregroundFrame(f);

			if (f != null) {
				this.leftMouse.draggedFrame = f;
				this.leftMouse.dragX = f.getX();
				this.leftMouse.dragY = f.getY();
			} else {
				this.leftMouse.draggedFrame = null;
			}
		}

		// mouse drag: window move/resize or delegate to child component
		if (this.leftMouse.dragged) {
			Frame dragged = this.leftMouse.draggedFrame;
			if (dragged != null) {

				// drag action effect depends on the region of the frame
				switch (this.leftMouse.clickedRegion) {
				case TITLE: {
					if (!this.leftMouse.draggedLastPoll) {
						dragged.setDragged(true);
						Cursors.setCursor(Type.MOVE);
					}
					int nx = this.leftMouse.hoverX
							- (leftMouse.clickX - leftMouse.dragX);
					int ny = this.leftMouse.hoverY
							- (leftMouse.clickY - leftMouse.dragY);
					dragged.setPos(nx, ny);
				}
					break;
				case CONTENT:
					// TODO delegate to embedded component
					break;
				case RIGHT: {
					if (!this.leftMouse.draggedLastPoll) {
						dragged.setResized(true);
						Cursors.setCursor(Type.RESIZE_RIGHT);
					}
					int nw = this.leftMouse.clickWidth
							+ (this.leftMouse.hoverX - this.leftMouse.clickX);
					dragged.setWidth(nw);
				}
					break;
				case LEFT: {
					if (!this.leftMouse.draggedLastPoll) {
						dragged.setResized(true);
						Cursors.setCursor(Type.RESIZE_LEFT);
					}
					int dx = this.leftMouse.hoverX - this.leftMouse.clickX;
					int nx = this.leftMouse.dragX
							+ Math.min(
									this.leftMouse.clickWidth
											- dragged.getMinWidth(), dx);
					int nw = this.leftMouse.clickWidth - dx;
					dragged.setX(nx);
					dragged.setWidth(nw);
				}
					break;
				case BOT: {
					if (!this.leftMouse.draggedLastPoll) {
						dragged.setResized(true);
						Cursors.setCursor(Type.RESIZE_BOT);
					}
					int nh = this.leftMouse.clickHeight
							+ (this.leftMouse.hoverY - this.leftMouse.clickY);
					dragged.setHeight(nh);
				}
					break;
				case TOP: {
					if (!this.leftMouse.draggedLastPoll) {
						dragged.setResized(true);
						Cursors.setCursor(Type.RESIZE_TOP);
					}
					int dy = this.leftMouse.hoverY - this.leftMouse.clickY;
					int ny = this.leftMouse.dragY
							+ Math.min(
									this.leftMouse.clickHeight
											- dragged.getMinHeight(), dy);
					int nh = this.leftMouse.clickHeight - dy;
					dragged.setY(ny);
					dragged.setHeight(nh);
				}
					break;
				case BOT_RIGHT: {
					if (!this.leftMouse.draggedLastPoll) {
						dragged.setResized(true);
						Cursors.setCursor(Type.RESIZE_BOT_RIGHT);
					}
					int nw = this.leftMouse.clickWidth
							+ (this.leftMouse.hoverX - this.leftMouse.clickX);
					int nh = this.leftMouse.clickHeight
							+ (this.leftMouse.hoverY - this.leftMouse.clickY);
					dragged.setBounds(nw, nh);
				}
					break;
				case TOP_RIGHT: {
					if (!this.leftMouse.draggedLastPoll) {
						dragged.setResized(true);
						Cursors.setCursor(Type.RESIZE_TOP_RIGHT);
					}
					int nw = this.leftMouse.clickWidth
							+ (this.leftMouse.hoverX - this.leftMouse.clickX);
					int dy = this.leftMouse.hoverY - this.leftMouse.clickY;
					int ny = this.leftMouse.dragY
							+ Math.min(
									this.leftMouse.clickHeight
											- dragged.getMinHeight(), dy);
					int nh = this.leftMouse.clickHeight - dy;
					dragged.setY(ny);
					dragged.setBounds(nw, nh);
				}
					break;
				case TOP_LEFT: {
					if (!this.leftMouse.draggedLastPoll) {
						dragged.setResized(true);
						Cursors.setCursor(Type.RESIZE_TOP_LEFT);
					}

					int dx = this.leftMouse.hoverX - this.leftMouse.clickX;
					int nx = this.leftMouse.dragX
							+ Math.min(
									this.leftMouse.clickWidth
											- dragged.getMinWidth(), dx);
					int nw = this.leftMouse.clickWidth - dx;
					int dy = this.leftMouse.hoverY - this.leftMouse.clickY;
					int ny = this.leftMouse.dragY
							+ Math.min(
									this.leftMouse.clickHeight
											- dragged.getMinHeight(), dy);
					int nh = this.leftMouse.clickHeight - dy;
					dragged.setX(nx);
					dragged.setY(ny);
					dragged.setBounds(nw, nh);
				}
					break;
				case BOT_LEFT: {
					if (!this.leftMouse.draggedLastPoll) {
						dragged.setResized(true);
						Cursors.setCursor(Type.RESIZE_BOT_LEFT);
					}
					int dx = this.leftMouse.hoverX - this.leftMouse.clickX;
					int nx = this.leftMouse.dragX
							+ Math.min(
									this.leftMouse.clickWidth
											- dragged.getMinWidth(), dx);
					int nw = this.leftMouse.clickWidth - dx;
					int nh = this.leftMouse.clickHeight
							+ (this.leftMouse.hoverY - this.leftMouse.clickY);
					dragged.setX(nx);
					dragged.setBounds(nw, nh);
				}
					break;
				}
			}
		} else if (this.leftMouse.draggedLastPoll) {
			Frame dragged = this.leftMouse.draggedFrame;
			if (dragged != null) {
				dragged.setDragged(false);
				dragged.setResized(false);
				Cursors.setCursor(Type.POINTER);
			}
		}
	}

	/**
	 * Changes the mouse cursor depending what it's currently hovering
	 */
	private void applyHoveringCursor() {
		if (this.leftMouse.dragged) {
			return;
		}

		Frame f = this.leftMouse.hoverFrame;
		if (f == null) {
			Cursors.setCursor(Type.POINTER);
			return;
		}

		switch (this.leftMouse.hoverRegion) {
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

	/**
	 * @return the abscissa of the Mouse cursor with the origin at the left of
	 *         the screen
	 */
	private static int getMouseX() {
		return Mouse.getX();
	}

	/**
	 * @return the ordinate of the Mouse cursor with the origin at the top of
	 *         the screen
	 */
	private static int getMouseY() {
		return Display.getDisplayMode().getHeight() - Mouse.getY();
	}
}
