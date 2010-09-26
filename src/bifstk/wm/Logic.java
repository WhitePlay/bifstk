package bifstk.wm;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import bifstk.Bifstk;
import bifstk.Handler;
import bifstk.config.Config;
import bifstk.config.Cursors;
import bifstk.config.Cursors.Type;
import bifstk.config.Theme;
import bifstk.wm.geom.Region;

/**
 * Internal logic of the WM
 * <p>
 * Handles input, propagates changes in the {@link State} upon a call to
 * {@link #update()}
 * 
 */
public class Logic {

	/** Privileged view of the WM's state */
	private State state = null;

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
		/** last X and Y position of the mouse cursor */
		int lastHoverX = 0, lastHoverY = 0;
		/** hovered frame last time update was called */
		Frame lastHoverFrame = null;
		/** true if hover frame has titlebar */
		boolean hoverFrameHasTitle = true;

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
		/** true when the mouse is dragged to the top of the screen */
		boolean dragTop = false;

		/** true when the mouse is dragged to the left of the screen */
		boolean dragLeft = false;
		/** true when the left dock border is hovered */
		boolean leftDockBorderHover = false;
		/** true when the left dock border was clicked last click */
		boolean leftDockBorderClicked = false;

	}

	/** state of the left mouse button */
	private MouseButton leftMouse = null;
	/** state of the right mouse button */
	private MouseButton rightMouse = null;
	/** state of the center mouse button */
	private MouseButton centerMouse = null;

	/**
	 * Default constructor
	 * 
	 * @param h user event handler, can be null
	 */
	public Logic(Handler h) {
		this.handler = h;
		this.state = new State();
		this.exitRequested = false;
		this.leftMouse = new MouseButton();
		this.rightMouse = new MouseButton();
		this.centerMouse = new MouseButton();
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
		this.centerMouse.clicked = false;
		this.centerMouse.downLastPoll = this.centerMouse.down;
		this.rightMouse.clicked = false;
		this.rightMouse.downLastPoll = this.rightMouse.down;

		// hovering state: prevents multiple calls for clicks/drags
		this.leftMouse.lastHoverX = this.leftMouse.hoverX;
		this.leftMouse.lastHoverY = this.leftMouse.hoverY;
		int mx = this.leftMouse.hoverX = getMouseX();
		int my = this.leftMouse.hoverY = getMouseY();
		this.leftMouse.lastHoverFrame = this.leftMouse.hoverFrame;
		this.leftMouse.hoverFrame = this.state.findFrame(mx, my);
		if (this.leftMouse.hoverFrame != null) {
			this.leftMouse.hoverRegion = this.leftMouse.hoverFrame.getRegion(
					mx, my);
		}
		if (this.state.getLeftDock().size() > 0
				&& this.state.getLeftDockWidth() <= mx
				&& mx < this.state.getLeftDockWidth()
						+ Theme.getWindowBorderWidth()) {
			this.leftMouse.leftDockBorderHover = true;
		} else {
			this.leftMouse.leftDockBorderHover = false;
		}

		// for each mouse event since last call
		while (Mouse.next()) {
			int button = Mouse.getEventButton();

			switch (button) {
			// left
			case 0:
				if (this.leftMouse.down) {
					this.leftMouse.down = false;
					this.leftMouse.leftDockBorderClicked = false;
				} else {
					this.leftMouse.down = true;
					this.leftMouse.clicked = true;
					this.leftMouse.clickX = mx;
					this.leftMouse.clickY = my;
					this.leftMouse.clickedFrame = this.leftMouse.hoverFrame;
					this.leftMouse.clickedRegion = this.leftMouse.hoverRegion;
					this.leftMouse.leftDockBorderClicked = this.leftMouse.leftDockBorderHover;
				}
				break;
			// right
			case 1:
				if (this.rightMouse.down) {
					this.rightMouse.down = false;
				} else {
					this.rightMouse.down = true;
					this.rightMouse.clicked = true;
					this.rightMouse.clickX = mx;
					this.rightMouse.clickY = my;
					this.rightMouse.clickedFrame = this.leftMouse.hoverFrame;
					this.rightMouse.clickedRegion = this.leftMouse.hoverRegion;
				}
				break;
			// middle
			case 2:
				if (this.centerMouse.down) {
					this.centerMouse.down = false;
				} else {
					this.centerMouse.down = true;
					this.centerMouse.clicked = true;
					this.centerMouse.clickX = mx;
					this.centerMouse.clickY = my;
					this.centerMouse.clickedFrame = this.leftMouse.hoverFrame;
					this.centerMouse.clickedRegion = this.leftMouse.hoverRegion;
				}
				break;
			}
		}

		Mouse.poll();
		int leftDx = mx - this.leftMouse.clickX;
		int leftDy = my - this.leftMouse.clickY;

		boolean leftDiffPos = (leftDx != 0) && (leftDy != 0);

		/* the mouse is dragged if it was down the last time this method was
		 * called, and the mouse's position diff is > 0 */
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
		Drawable modalWindow = this.state.getModalWindow();
		boolean modalIsHoverOrNull = (modalWindow == null || modalWindow == this.leftMouse.hoverFrame);
		boolean modalIsLClickOrNull = (modalWindow == null || modalWindow == this.leftMouse.clickedFrame);
		boolean modalIsRClickOrNull = (modalWindow == null || modalWindow == this.rightMouse.clickedFrame);
		boolean modalIsCClickOrNull = (modalWindow == null || modalWindow == this.centerMouse.clickedFrame);

		boolean lclickIsWindow = false;
		if (this.leftMouse.clickedFrame != null) {
			lclickIsWindow = Window.class
					.isAssignableFrom(this.leftMouse.clickedFrame.getClass());
		}

		boolean focusFollowMouse = Config.isWmFocusFollowmouse();
		// apply sloppy focus
		if (focusFollowMouse && !this.leftMouse.clicked
				&& !this.leftMouse.dragged) {
			if (this.leftMouse.hoverFrame != null && modalWindow == null) {
				this.state.focusFrame(this.leftMouse.hoverFrame);
			}
		}

		// frame controls hovering
		if (this.leftMouse.hoverFrame != null && modalIsHoverOrNull) {
			this.leftMouse.hoverFrame
					.setControlCloseHover(this.leftMouse.hoverRegion
							.equals(Region.CLOSE));
			this.leftMouse.hoverFrame
					.setControlMaximizeHover(this.leftMouse.hoverRegion
							.equals(Region.MAXIMIZE));
		}

		// propagate mouseHover to the frame and its content
		if (this.leftMouse.hoverFrame != null
				&& modalIsHoverOrNull
				&& (this.leftMouse.lastHoverX != this.leftMouse.hoverX || this.leftMouse.lastHoverY != this.leftMouse.hoverY)) {
			this.leftMouse.hoverFrame.mouseHover(this.leftMouse.hoverX
					- this.leftMouse.hoverFrame.getX(), this.leftMouse.hoverY
					- this.leftMouse.hoverFrame.getY());
		}
		// propagate mouseOut to the frame
		if (this.leftMouse.lastHoverFrame != null
				&& modalIsHoverOrNull
				&& !this.leftMouse.lastHoverFrame
						.equals(this.leftMouse.hoverFrame)) {
			this.leftMouse.lastHoverFrame.mouseOut();
		}

		// LMB click
		if (this.leftMouse.clicked && modalIsLClickOrNull) {
			Frame f = this.leftMouse.clickedFrame;
			if (lclickIsWindow) {
				this.state.foregroundWindow((Window) f);
			}
			if (f != null) {
				this.leftMouse.draggedFrame = f;
				this.leftMouse.dragX = f.getX();
				this.leftMouse.dragY = f.getY();
			} else {
				this.leftMouse.draggedFrame = null;
			}
			// propagate click to frame and content
			if (this.leftMouse.clickedFrame != null) {
				this.leftMouse.clickWidth = this.leftMouse.clickedFrame
						.getWidth();
				this.leftMouse.clickHeight = this.leftMouse.clickedFrame
						.getHeight();
				this.leftMouse.clickedFrame.mouseDown(0);
			}
			// propagate click to user handler
			else if (this.handler != null) {
				this.handler.mouseEvent(0, this.leftMouse.hoverX,
						this.leftMouse.hoverY, true);
			}

			// close frame control
			if (this.leftMouse.clickedRegion.equals(Region.CLOSE)
					&& this.leftMouse.clickedFrame != null) {
				this.leftMouse.clickedFrame.setControlCloseDown(true);
			}

			// maximize frame control
			if (this.leftMouse.clickedRegion.equals(Region.MAXIMIZE)
					&& this.leftMouse.clickedFrame != null) {
				this.leftMouse.clickedFrame.setControlMaximizeDown(true);
			}

		}
		// LMB unclick
		else if (this.leftMouse.downLastPoll && !this.leftMouse.down
				&& modalIsLClickOrNull) {
			if (this.leftMouse.clickedFrame != null) {
				// propagate mouse up to frame and content
				this.leftMouse.clickedFrame.mouseUp(
						0,
						this.leftMouse.hoverX
								- this.leftMouse.clickedFrame.getX(),
						this.leftMouse.hoverY
								- this.leftMouse.clickedFrame.getY());
			}
			// propagate click to user handler
			else if (this.handler != null) {
				this.handler.mouseEvent(0, this.leftMouse.hoverX,
						this.leftMouse.hoverY, false);
			}

			// close frame control
			if (this.leftMouse.clickedRegion.equals(Region.CLOSE)) {
				if (this.leftMouse.hoverRegion.equals(Region.CLOSE)
						&& this.leftMouse.hoverFrame
								.equals(this.leftMouse.clickedFrame)) {
					if (modalWindow == this.leftMouse.clickedFrame) {
						Bifstk.setModalWindow(null);
					} else {
						if (lclickIsWindow) {
							Bifstk.removeWindow((Window) this.leftMouse.clickedFrame);
						}
					}
					this.leftMouse.clickedFrame = null;
					this.leftMouse.hoverFrame = null;
					this.leftMouse.hoverRegion = Region.OUT;
				} else {
					this.leftMouse.clickedFrame.setControlCloseDown(false);
				}
			}

			// maximize frame control
			if (this.leftMouse.clickedRegion.equals(Region.MAXIMIZE)) {
				this.leftMouse.clickedFrame.setControlMaximizeDown(false);
				this.leftMouse.clickedFrame.toggleMaximize();
				this.leftMouse.hoverFrame.setControlMaximizeHover(false);

			}

			if (this.leftMouse.dragTop) {
				this.leftMouse.dragTop = false;
			}
			if (this.leftMouse.dragLeft) {
				this.leftMouse.dragLeft = false;
				/* Window w = (Window) this.leftMouse.draggedFrame;
				 * w.toggleDocked(); this.state.removeWindow(w);
				 * this.state.addToLeftDock(w, 0); */
			}
		}

		// RMB click
		if (this.rightMouse.clicked && modalIsRClickOrNull) {
			if (this.rightMouse.clickedFrame != null) {
				this.rightMouse.clickedFrame.mouseDown(1);
			}
			// propagate click to user handler
			else if (this.handler != null) {
				this.handler.mouseEvent(1, this.leftMouse.hoverX,
						this.leftMouse.hoverY, true);
			}

		}
		// RMB unclick
		else if (!this.rightMouse.down && this.rightMouse.downLastPoll
				&& modalIsRClickOrNull) {
			if (this.rightMouse.clickedFrame != null) {
				this.rightMouse.clickedFrame.mouseUp(
						1,
						this.rightMouse.hoverX
								- this.rightMouse.clickedFrame.getX(),
						this.rightMouse.hoverY
								- this.rightMouse.clickedFrame.getY());
			}
			// propagate click to user handler
			else if (this.handler != null) {
				this.handler.mouseEvent(1, this.leftMouse.hoverX,
						this.leftMouse.hoverY, false);
			}
		}

		// CMB click
		if (this.centerMouse.clicked && modalIsCClickOrNull) {
			if (this.centerMouse.clickedFrame != null) {
				this.centerMouse.clickedFrame.mouseDown(2);
			}
			// propagate click to user handler
			else if (this.handler != null) {
				this.handler.mouseEvent(2, this.leftMouse.hoverX,
						this.leftMouse.hoverY, true);
			}
		}
		// CMB unclick
		else if (!this.centerMouse.down && this.centerMouse.downLastPoll
				&& modalIsCClickOrNull) {
			if (this.centerMouse.clickedFrame != null) {
				this.centerMouse.clickedFrame.mouseUp(
						2,
						this.centerMouse.hoverX
								- this.centerMouse.clickedFrame.getX(),
						this.centerMouse.hoverY
								- this.centerMouse.clickedFrame.getY());
			}
			// propagate click to user handler
			else if (this.handler != null) {
				this.handler.mouseEvent(2, this.leftMouse.hoverX,
						this.leftMouse.hoverY, false);
			}
		}

		// hoverFrame had its titlebar toggled, need to resend mouseHover to
		// content
		if (this.leftMouse.hoverFrame != null) {
			boolean hasNow = this.leftMouse.hoverFrame.hasTitlebar();

			if (hasNow != this.leftMouse.hoverFrameHasTitle) {
				this.leftMouse.hoverFrame.mouseHover(this.leftMouse.hoverX,
						this.leftMouse.hoverY);
			}

			this.leftMouse.hoverFrameHasTitle = hasNow;
		}

		if (this.leftMouse.dragged && modalWindow == null
				&& this.leftMouse.leftDockBorderClicked) {
			if (!this.leftMouse.draggedLastPoll) {
				Cursors.setCursor(Type.RESIZE_HOR);
			}
			this.state.setLeftDockWidth(this.leftMouse.hoverX);
		}

		// mouse drag: window move/resize or delegate to child component
		if (this.leftMouse.dragged && modalIsLClickOrNull) {
			Frame dragged = this.leftMouse.draggedFrame;
			if (dragged != null) {

				// drag action effect depends on the region of the frame
				switch (this.leftMouse.clickedRegion) {
				case TITLE: {
					if (!dragged.isMovable()) {
						break;
					}

					// cancel maximize upon drag
					if (dragged.isMaximized() && !this.leftMouse.dragTop) {
						dragged.toggleMaximize();
						leftMouse.dragX = this.leftMouse.hoverX
								- dragged.getWidth() / 2;
					}

					// initiate or cancel maximize on top drag
					if (lclickIsWindow && dragged.isResizable()
							&& Config.isWmWindowSnapTop()) {
						if (this.leftMouse.dragTop) {
							if (this.leftMouse.hoverY > 1) {
								this.leftMouse.dragTop = false;
								dragged.toggleMaximize();
							}
						} else {
							if (this.leftMouse.hoverY == 1) {
								this.leftMouse.dragTop = true;
								dragged.toggleMaximize();
							}
						}
					}

					// remove from left dock
					if (dragged.isDocked() && !this.leftMouse.dragLeft) {
						Window w = (Window) dragged;
						this.state.removeFromLeftDock(w);
						this.state.addWindow(w);
						w.toggleDocked();
					}

					if (!this.leftMouse.draggedLastPoll) {
						dragged.setDragged(true);
						Cursors.setCursor(Type.MOVE);
					}

					int nx = this.leftMouse.hoverX
							- (leftMouse.clickX - leftMouse.dragX);
					int ny = this.leftMouse.hoverY
							- (leftMouse.clickY - leftMouse.dragY);

					dragged.setPos(nx, ny);

					// drag to left dock
					if (lclickIsWindow) {
						Window w = (Window) dragged;
						if (this.leftMouse.dragLeft) {
							if (this.leftMouse.hoverX > 0) {
								this.leftMouse.dragLeft = false;
								// move out of dock
								w.toggleDocked();
								this.state.removeFromLeftDock(w);
								this.state.addWindow(w);
							} else {
								this.state.removeFromLeftDock(w);
								this.state.addToLeftDock(w);
							}
						} else if (dragged.isResizable()) {
							if (this.leftMouse.hoverX == 0) {
								this.leftMouse.dragLeft = true;
								// move in dock
								w.toggleDocked();
								this.state.removeWindow(w);
								this.state.addToLeftDock(w);
							}
						}

					}
				}
					break;
				case CONTENT:
					// TODO delegate to embedded component
					break;
				case RIGHT: {
					if (!dragged.isMovable()) {
						break;
					}

					if (!dragged.isResizable()) {
						if (!this.leftMouse.draggedLastPoll) {
							dragged.setDragged(true);
							Cursors.setCursor(Type.MOVE);
						}
						int nx = this.leftMouse.hoverX
								- (leftMouse.clickX - leftMouse.dragX);
						int ny = this.leftMouse.hoverY
								- (leftMouse.clickY - leftMouse.dragY);
						dragged.setPos(nx, ny);
					} else {
						if (!this.leftMouse.draggedLastPoll) {
							dragged.setResized(true);
							Cursors.setCursor(Type.RESIZE_HOR);
						}
						int nw = this.leftMouse.clickWidth
								+ (this.leftMouse.hoverX - this.leftMouse.clickX);
						dragged.setWidth(nw);
					}
				}
					break;
				case LEFT: {
					if (!dragged.isMovable()) {
						break;
					}

					if (!dragged.isResizable()) {
						if (!this.leftMouse.draggedLastPoll) {
							dragged.setDragged(true);
							Cursors.setCursor(Type.MOVE);
						}
						int nx = this.leftMouse.hoverX
								- (leftMouse.clickX - leftMouse.dragX);
						int ny = this.leftMouse.hoverY
								- (leftMouse.clickY - leftMouse.dragY);
						dragged.setPos(nx, ny);
					} else {
						if (!this.leftMouse.draggedLastPoll) {
							dragged.setResized(true);
							Cursors.setCursor(Type.RESIZE_HOR);
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
				}
					break;
				case BOT: {
					if (!dragged.isMovable()) {
						break;
					}

					if (!dragged.isResizable()) {
						if (!this.leftMouse.draggedLastPoll) {
							dragged.setDragged(true);
							Cursors.setCursor(Type.MOVE);
						}
						int nx = this.leftMouse.hoverX
								- (leftMouse.clickX - leftMouse.dragX);
						int ny = this.leftMouse.hoverY
								- (leftMouse.clickY - leftMouse.dragY);
						dragged.setPos(nx, ny);
					} else {
						if (!this.leftMouse.draggedLastPoll) {
							dragged.setResized(true);
							Cursors.setCursor(Type.RESIZE_VER);
						}
						int nh = this.leftMouse.clickHeight
								+ (this.leftMouse.hoverY - this.leftMouse.clickY);
						dragged.setHeight(nh);
					}
				}
					break;
				case TOP: {
					if (!dragged.isMovable()) {
						break;
					}

					if (!dragged.isResizable()) {
						if (!this.leftMouse.draggedLastPoll) {
							dragged.setDragged(true);
							Cursors.setCursor(Type.MOVE);
						}
						int nx = this.leftMouse.hoverX
								- (leftMouse.clickX - leftMouse.dragX);
						int ny = this.leftMouse.hoverY
								- (leftMouse.clickY - leftMouse.dragY);
						dragged.setPos(nx, ny);
					} else {
						if (!this.leftMouse.draggedLastPoll) {
							dragged.setResized(true);
							Cursors.setCursor(Type.RESIZE_VER);
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
				}
					break;
				case BOT_RIGHT: {
					if (!dragged.isMovable()) {
						break;
					}

					if (!dragged.isResizable()) {
						if (!this.leftMouse.draggedLastPoll) {
							dragged.setDragged(true);
							Cursors.setCursor(Type.MOVE);
						}
						int nx = this.leftMouse.hoverX
								- (leftMouse.clickX - leftMouse.dragX);
						int ny = this.leftMouse.hoverY
								- (leftMouse.clickY - leftMouse.dragY);
						dragged.setPos(nx, ny);
					} else {
						if (!this.leftMouse.draggedLastPoll) {
							dragged.setResized(true);
							Cursors.setCursor(Type.RESIZE_TOP_LEFT);
						}
						int nw = this.leftMouse.clickWidth
								+ (this.leftMouse.hoverX - this.leftMouse.clickX);
						int nh = this.leftMouse.clickHeight
								+ (this.leftMouse.hoverY - this.leftMouse.clickY);
						dragged.setBounds(nw, nh);
					}
				}
					break;
				case TOP_RIGHT: {
					if (!dragged.isMovable()) {
						break;
					}

					if (!dragged.isResizable()) {
						if (!this.leftMouse.draggedLastPoll) {
							dragged.setDragged(true);
							Cursors.setCursor(Type.MOVE);
						}
						int nx = this.leftMouse.hoverX
								- (leftMouse.clickX - leftMouse.dragX);
						int ny = this.leftMouse.hoverY
								- (leftMouse.clickY - leftMouse.dragY);
						dragged.setPos(nx, ny);
					} else {
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
				}
					break;
				case TOP_LEFT: {
					if (!dragged.isMovable()) {
						break;
					}

					if (!dragged.isResizable()) {
						if (!this.leftMouse.draggedLastPoll) {
							dragged.setDragged(true);
							Cursors.setCursor(Type.MOVE);
						}
						int nx = this.leftMouse.hoverX
								- (leftMouse.clickX - leftMouse.dragX);
						int ny = this.leftMouse.hoverY
								- (leftMouse.clickY - leftMouse.dragY);
						dragged.setPos(nx, ny);
					} else {
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
				}
					break;
				case BOT_LEFT: {
					if (!dragged.isMovable()) {
						break;
					}

					if (!dragged.isResizable()) {
						if (!this.leftMouse.draggedLastPoll) {
							dragged.setDragged(true);
							Cursors.setCursor(Type.MOVE);
						}
						int nx = this.leftMouse.hoverX
								- (leftMouse.clickX - leftMouse.dragX);
						int ny = this.leftMouse.hoverY
								- (leftMouse.clickY - leftMouse.dragY);
						dragged.setPos(nx, ny);
					} else {
						if (!this.leftMouse.draggedLastPoll) {
							dragged.setResized(true);
							Cursors.setCursor(Type.RESIZE_TOP_RIGHT);
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
				}
					break;
				case OUT: {

				}
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

		Drawable modal = this.state.getModalWindow();
		Frame f = this.leftMouse.hoverFrame;
		if (f == null || (modal != null && modal != this.leftMouse.hoverFrame)
				|| !(f.isMovable() || f.isResizable())) {

			if (this.leftMouse.leftDockBorderHover) {
				Cursors.setCursor(Type.RESIZE_HOR);
			} else {
				Cursors.setCursor(Type.POINTER);
			}
			return;
		}

		switch (this.leftMouse.hoverRegion) {
		case CONTENT:
		case TITLE:
		case OUT:
		case CLOSE:
		case MAXIMIZE:
			Cursors.setCursor(Type.POINTER);
			return;
		case TOP_LEFT:
			Cursors.setCursor(Type.RESIZE_TOP_LEFT);
			return;
		case TOP:
			Cursors.setCursor(Type.RESIZE_VER);
			return;
		case TOP_RIGHT:
			Cursors.setCursor(Type.RESIZE_TOP_RIGHT);
			return;
		case LEFT:
			Cursors.setCursor(Type.RESIZE_HOR);
			return;
		case RIGHT:
			Cursors.setCursor(Type.RESIZE_HOR);
			return;
		case BOT_LEFT:
			Cursors.setCursor(Type.RESIZE_TOP_RIGHT);
			return;
		case BOT:
			Cursors.setCursor(Type.RESIZE_VER);
			return;
		case BOT_RIGHT:
			Cursors.setCursor(Type.RESIZE_TOP_LEFT);
			return;
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
