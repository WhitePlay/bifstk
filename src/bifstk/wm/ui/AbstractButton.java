package bifstk.wm.ui;

import bifstk.wm.geom.Rectangle;

/**
 * Most common clickable widget
 * 
 */
public abstract class AbstractButton extends Actionable {

	/** dimensions of the button */
	private Rectangle bounds = null;
	/** true when the button is being hovered by LMB */
	private boolean mouseHover = false;
	/** true when the button is being clicked by LMB */
	private boolean mouseClicked = false;

	protected AbstractButton() {
		this.bounds = new Rectangle();
	}

	/**
	 * @return true when the mouse is currently hovering this button
	 */
	public boolean isMouseHover() {
		return this.mouseHover;
	}

	/**
	 * @return true when this button is currently being clicked
	 */
	public boolean isMouseClicked() {
		return this.mouseClicked;
	}

	@Override
	public void setWidth(int w) {
		this.bounds.setWidth(w);
	}

	@Override
	public void setHeight(int h) {
		this.bounds.setHeight(h);
	}

	public void setBounds(int w, int h) {
		this.bounds.setBounds(w, h);
	}

	@Override
	public int getWidth() {
		return this.bounds.getWidth();
	}

	@Override
	public int getHeight() {
		return this.bounds.getHeight();
	}

	@Override
	public void mouseHover(int x, int y) {
		this.mouseHover = true;
	}

	@Override
	public void mouseOut() {
		this.mouseHover = false;
	}

	@Override
	public void mouseDown(int button) {
		if (button == 0) {
			this.mouseClicked = true;
		}
	}

	@Override
	public void mouseUp(int button, int x, int y) {
		if (button == 0) {
			this.mouseClicked = false;
			if (this.bounds.contains(x, y) && this.getHandler() != null) {
				this.getHandler().actionPerformed(this.getAction(), this);
			}
		}
	}
}
