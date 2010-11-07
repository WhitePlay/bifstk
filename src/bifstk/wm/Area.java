package bifstk.wm;

import bifstk.config.Theme;
import bifstk.gl.Color;

public class Area extends Frame {

	public Area(int x, int y) {
		this(x, y, 60, 60);
	}

	public Area(int x, int y, int w, int h) {
		super(x, y, w, h);
		super.setResizable(false);
		super.setTitlebar(false);
	}

	@Override
	public void setResizable(boolean r) {
		return;
	}

	@Override
	public boolean isResizable() {
		return false;
	}

	@Override
	public boolean isMovable() {
		return false;
	}

	@Override
	public void setTitlebar(boolean t) {
		return;
	}

	@Override
	public boolean hasTitlebar() {
		return false;
	}

	@Override
	protected int getTitleBarHeight() {
		return 0;
	}

	@Override
	protected int getBorderWidth() {
		return Theme.getAreaBorderWidth();
	}

	@Override
	protected Color getBorderFocusedColor() {
		return Theme.getAreaFocusedColor();
	}

	@Override
	protected Color getBorderUnfocusedColor() {
		return Theme.getAreaUnfocusedColor();
	}

	@Override
	protected Color getBorderOuterFocusedColor() {
		return Theme.getAreaBorderFocusedColor();
	}

	@Override
	protected Color getBorderOuterUnfocusedColor() {
		return Theme.getAreaBorderUnfocusedColor();
	}

	@Override
	protected Color getFrameTitlebarFocusedColor() {
		// there should be no titlebar
		return Color.RED;
	}

	@Override
	protected Color getFrameTitlebarUnfocusedColor() {
		// there should be no titlebar
		return Color.RED;
	}

	@Override
	protected Color getFrameFocusedColor() {
		return Theme.getAreaFocusedColor();
	}

	@Override
	protected Color getFrameUnfocusedColor() {
		return Theme.getAreaUnfocusedColor();
	}

	@Override
	protected Color getFrameShadowFocusedColor() {
		// should be none
		return Color.RED;
	}

	@Override
	protected Color getFrameShadowUnfocusedColor() {
		// should be none
		return Color.RED;
	}

	@Override
	protected float getFrameMovedAlpha() {
		return 1.0f;
	}

	@Override
	protected float getFrameResizedAlpha() {
		return 1.0f;
	}

	@Override
	protected float getFrameFocusedAlpha() {
		return Theme.getAreaFocusedAlpha();
	}

	@Override
	protected float getFrameUnfocusedAlpha() {
		return Theme.getAreaUnfocusedAlpha();
	}

}
