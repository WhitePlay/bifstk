package bifstk.wm;

import bifstk.config.Theme;
import bifstk.gl.Color;

public class Window extends Frame {

	private static final int titlebarHeight = 20;

	public Window(int x, int y) {
		super(x, y);
	}

	public Window(int x, int y, int w, int h) {
		super(x, y, w, h);
	}

	@Override
	protected int getTitleBarHeight() {
		if (this.hasTitlebar()) {
			return titlebarHeight;
		} else {
			return 0;
		}
	}

	@Override
	protected int getBorderWidth() {
		if (this.isMaximized() || this.isDocked()) {
			return 0;
		} else {
			return Theme.getWindowBorderWidth();
		}
	}

	@Override
	protected Color getBorderFocusedColor() {
		return Theme.getWindowBorderFocusedColor();
	}

	@Override
	protected Color getBorderUnfocusedColor() {
		return Theme.getWindowBorderUnfocusedColor();
	}

	@Override
	protected Color getBorderOuterFocusedColor() {
		return Theme.getWindowBorderOuterFocusedColor();
	}

	@Override
	protected Color getBorderOuterUnfocusedColor() {
		return Theme.getWindowBorderOuterUnfocusedColor();
	}

	@Override
	protected boolean isFrameBorderRounded() {
		return Theme.isWindowBorderRounded();
	}

	@Override
	protected Color getFrameTitlebarFocusedColor() {
		return Theme.getWindowTitlebarFocusedColor();
	}

	@Override
	protected Color getFrameTitlebarUnfocusedColor() {
		return Theme.getWindowTitlebarUnfocusedColor();
	}

}
