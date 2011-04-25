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
			return Theme.get().windowBorderWidth;
		}
	}

	@Override
	protected Color getBorderFocusedColor() {
		return Theme.get().windowBorderFocusedColor;
	}

	@Override
	protected Color getBorderUnfocusedColor() {
		return Theme.get().windowBorderUnfocusedColor;
	}

	@Override
	protected Color getBorderOuterFocusedColor() {
		return Theme.get().windowBorderOuterFocusedColor;
	}

	@Override
	protected Color getBorderOuterUnfocusedColor() {
		return Theme.get().windowBorderOuterUnfocusedColor;
	}

	@Override
	protected Color getFrameTitlebarFocusedColor() {
		return Theme.get().windowTitlebarFocusedColor;
	}

	@Override
	protected Color getFrameTitlebarUnfocusedColor() {
		return Theme.get().windowTitlebarUnfocusedColor;
	}

	@Override
	protected Color getFrameFocusedColor() {
		return Theme.get().windowFocusedColor;
	}

	@Override
	protected Color getFrameUnfocusedColor() {
		return Theme.get().windowUnfocusedColor;
	}

	@Override
	protected Color getFrameShadowFocusedColor() {
		return Theme.get().windowShadowFocusedColor;
	}

	@Override
	protected Color getFrameShadowUnfocusedColor() {
		return Theme.get().windowShadowUnfocusedColor;
	}

	@Override
	protected float getFrameMovedAlpha() {
		return Theme.get().windowMovedAlpha;
	}

	@Override
	protected float getFrameResizedAlpha() {
		return Theme.get().windowResizedAlpha;
	}

	@Override
	protected float getFrameFocusedAlpha() {
		return Theme.get().windowFocusedAlpha;
	}

	@Override
	protected float getFrameUnfocusedAlpha() {
		return Theme.get().windowUnfocusedAlpha;
	}

}
