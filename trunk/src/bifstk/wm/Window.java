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
		return Theme.get().windowBorderColor;
	}

	@Override
	protected Color getBorderUnfocusedColor() {
		return Theme.get().windowBorderColor
				.add(Theme.get().windowUnfocusedMask);
	}

	@Override
	protected Color getFrameTitlebarFocusedColor() {
		return Theme.get().windowTitlebarColor;
	}

	@Override
	protected Color getFrameTitlebarUnfocusedColor() {
		return Theme.get().windowTitlebarColor
				.add(Theme.get().windowUnfocusedMask);
	}

	@Override
	protected Color getFrameFocusedColor() {
		return Theme.get().windowColor;
	}

	@Override
	protected Color getFrameUnfocusedColor() {
		return Theme.get().windowColor
				.add(Theme.get().windowUnfocusedMask);
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
		return Theme.get().windowAlpha;
	}

	@Override
	protected float getFrameUnfocusedAlpha() {
		return Theme.get().windowUnfocusedAlpha;
	}

}
