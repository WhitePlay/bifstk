package bifstk.wm.ui;

import org.lwjgl.opengl.GL11;

import bifstk.config.Fonts;
import bifstk.config.Theme;
import bifstk.gl.Color;
import bifstk.gl.Util;
import bifstk.wm.Logic;
import bifstk.wm.geom.Rectangle;

/**
 * A ScrollBox allows scrolling when the preferred size of the widget contained
 * is smaller than the size of the box.
 * <p>
 * 
 */
public class ScrollBox extends Border {

	private Rectangle bounds = null;

	/** true when the horizontal scrollbar is visible */
	private boolean scrollHor = false;
	/** true when the vertical scrollbar is visible */
	private boolean scrollVer = false;

	/** horizontal scroll position : 0.0 for left/top; 1.0 for right/bot */
	private float horScrollPos = 0.0f;
	/** vertical scroll position : 0.0 for left/top; 1.0 for right/bot */
	private float verScrollPos = 0.0f;

	/** length of the vertical scroll button in pixels */
	private int verLen;
	/** distance from the top/left of the vertical scroll button in pixels */
	private int verPos;

	/** length of the horizontal scroll button in pixels */
	private int horLen;
	/** distance from the top/left of the horizontal scroll button in pixels */
	private int horPos;

	/**
	 * Regions of a ScrollBox for Mouse event handling
	 * 
	 * <pre>
	 *  ________________________
	 * |                     |  |
	 * |                     |5 |
	 * |                     |__|
	 * |          1          |6 |      0
	 * |                     |__|
	 * |                     |7 |
	 * |_____________________|__|
	 * |   2   |  3  |   4   |0 |
	 * |_______|_____|_______|__|
	 * </pre>
	 * <ul>
	 * <li>0 outside
	 * <li>1 content
	 * <li>2 horLeft
	 * <li>3 horButton
	 * <li>4 horRight
	 * <li>5 verTop
	 * <li>6 verButton
	 * <li>7 verBot
	 * </ul>
	 */
	private enum Region {
		/** vertical scrollbar : region above the button */
		verTop,
		/** vertical scrollbar : button */
		verButton,
		/** vertical scrollbar : region below the button */
		verBot,
		/** horizontal scrollbar : region left to the button */
		horLeft,
		/** horizontal scrollbar : button */
		horButton,
		/** horizontal scrollbar : region right to the button */
		horRight,
		/** not in the ScrollBox */
		outside,
		/** content of the ScrollBox */
		content;
	}

	/** region currently hovered by the mouse cursor */
	private Region hoverRegion = Region.outside;

	/** true when the horizontal scrollbar is dragged */
	private boolean horDrag = false;
	/** true when the vertical scrollbar is dragged */
	private boolean verDrag = false;

	/** original position of the scroll bar when dragging was initiated */
	private int orPos;
	/** mouse coordinates when dragging was initiated */
	private int downX, downY;

	/**
	 * Default constructor
	 * 
	 * @param content contained widget
	 */
	public ScrollBox(Widget content) {
		super(content);
		this.bounds = new Rectangle();
	}

	@Override
	public void render(float alpha, Color uiBg, float uiBgAlpha) {
		int scrollWidth = getScrollBarWidth();
		int w = this.bounds.getWidth();
		int h = this.bounds.getHeight();
		int realWidth = getContent().getWidth();
		int realHeight = getContent().getHeight();
		int viewWidth = ((this.scrollVer) ? w - scrollWidth : w);
		int viewHeight = ((this.scrollHor) ? h - scrollWidth : h);
		float a = alpha * uiBgAlpha;

		/*
		 * update the position of the scrollbars
		 * it really sucks to update the logic in the render method,
		 * but this stuff is absolutely trivial and it avoid calling another
		 * method everytime the mouse is moved
		 */
		if (this.verDrag) {
			int my = Logic.getMouseY();
			int dy = my - this.downY;
			verPos = Util.clampi(orPos + dy, 0, viewHeight - verLen);
			verScrollPos = (float) verPos / (float) (viewHeight - verLen);
		} else if (this.horDrag) {
			int mx = Logic.getMouseX();
			int dx = mx - this.downX;
			horPos = Util.clampi(orPos + dx, 0 , viewWidth - horLen);
			horScrollPos = (float) horPos / (float) (viewWidth - horLen);
		}

		int dh = (int) (verScrollPos * Math.max(0, realHeight - viewHeight));
		int dw = (int) (horScrollPos * Math.max(0, realWidth - viewWidth));

		// draw content
		GL11.glPushMatrix();
		GL11.glTranslatef(-dw, -dh, 0.0f);

		Util.pushScissor(0, 0, viewWidth, viewHeight, false);
		this.getContent().render(alpha, uiBg, uiBgAlpha);
		Util.popScissor();

		GL11.glPopMatrix();

		Color borderCol = Theme.getUiButtonBorderColor();
		Color fillCol = Theme.getUiButtonColor();

		//  scrollbars
		if (scrollVer || scrollHor) {
			float[] c1 = new float[4 * 4 * 3];
			uiBg.fillArray(c1, 0, 16, a);
			fillCol.fillArray(c1, 16, 32, a);
			uiBg.fillArray(c1, 32, 48, a);

			if (scrollVer) {
				int[] v1 = {
						// top gap fill
						viewWidth + scrollWidth, 0, //
						viewWidth, 0, //
						viewWidth, verPos, //
						viewWidth + scrollWidth, verPos, //

						// scrollbar fill
						viewWidth + scrollWidth, verPos, //
						viewWidth, verPos, //
						viewWidth, verPos + verLen, //
						viewWidth + scrollWidth, verPos + verLen, //

						// bot gap gill
						viewWidth + scrollWidth, verPos + verLen, //
						viewWidth, verPos + verLen, //
						viewWidth, viewHeight, //
						viewWidth + scrollWidth, viewHeight, //
				};
				Util.draw2D(v1, c1, GL11.GL_QUADS);

				float[] c2 = borderCol.toArray(8, a);
				int[] v2 = {
						viewWidth, verPos, //
						viewWidth + scrollWidth, verPos, //
						viewWidth + scrollWidth, verPos + verLen, //
						viewWidth, verPos + verLen
				};
				Util.draw2DLineLoop(v2, c2);
			}
			if (scrollHor) {
				int[] v1 = {
						// left gap fill
						0, viewHeight, //
						horPos, viewHeight, //
						horPos, viewHeight + scrollWidth, //
						0, viewHeight + scrollWidth, //

						// scrollbar fill
						horPos, viewHeight, //
						horPos + horLen, viewHeight, //
						horPos + horLen, viewHeight + scrollWidth, //
						horPos, viewHeight + scrollWidth, //

						// right gap gill
						horPos + horLen, viewHeight, //
						viewWidth, viewHeight, //
						viewWidth, viewHeight + scrollWidth, //
						horPos + horLen, viewHeight + scrollWidth, //
				};
				Util.draw2D(v1, c1, GL11.GL_QUADS);

				float[] c2 = borderCol.toArray(8, a);
				int[] v2 = {
						horPos, viewHeight, //
						horPos + horLen, viewHeight, //
						horPos + horLen, viewHeight + scrollWidth, //
						horPos, viewHeight + scrollWidth
				};
				Util.draw2DLineLoop(v2, c2);
			}
		}
	}

	@Override
	public void mouseHover(int x, int y) {
		int hor = -1;
		int ver = -1;
		int scrollWidth = getScrollBarWidth();
		int w = this.bounds.getWidth();
		int h = this.bounds.getHeight();

		// region detection: very verbose; very fast
		if (x < 0) {
			hor = 0;
		} else if (x < horPos) {
			hor = 1;
		} else if (x < horPos + horLen) {
			hor = 2;
		} else if (x < w - scrollWidth) {
			hor = 3;
		} else if (x < w) {
			hor = 4;
		} else {
			hor = 5;
		}
		if (y < 0) {
			ver = 0;
		} else if (y < verPos) {
			ver = 1;
		} else if (y < verPos + verLen) {
			ver = 2;
		} else if (y < h - scrollWidth) {
			ver = 3;
		} else if (y < h) {
			ver = 4;
		} else {
			ver = 5;
		}

		switch (hor) {
		case 0:
			hoverRegion = Region.outside;
			break;
		case 1:
			switch (ver) {
			case 0:
				hoverRegion = Region.outside;
				break;
			case 1:
				hoverRegion = Region.content;
				break;
			case 2:
				hoverRegion = Region.content;
				break;
			case 3:
				hoverRegion = Region.content;
				break;
			case 4:
				hoverRegion = Region.horLeft;
				break;
			case 5:
				hoverRegion = Region.outside;
				break;
			}
			break;
		case 2:
			switch (ver) {
			case 0:
				hoverRegion = Region.outside;
				break;
			case 1:
				hoverRegion = Region.content;
				break;
			case 2:
				hoverRegion = Region.content;
				break;
			case 3:
				hoverRegion = Region.content;
				break;
			case 4:
				hoverRegion = Region.horButton;
				break;
			case 5:
				hoverRegion = Region.outside;
				break;
			}
			break;
		case 3:
			switch (ver) {
			case 0:
				hoverRegion = Region.outside;
				break;
			case 1:
				hoverRegion = Region.content;
				break;
			case 2:
				hoverRegion = Region.content;
				break;
			case 3:
				hoverRegion = Region.content;
				break;
			case 4:
				hoverRegion = Region.horRight;
				break;
			case 5:
				hoverRegion = Region.outside;
				break;
			}
			break;
		case 4:
			switch (ver) {
			case 0:
				hoverRegion = Region.outside;
				break;
			case 1:
				hoverRegion = Region.verTop;
				break;
			case 2:
				hoverRegion = Region.verButton;
				break;
			case 3:
				hoverRegion = Region.verBot;
				break;
			case 4:
				hoverRegion = Region.outside;
				break;
			case 5:
				hoverRegion = Region.outside;
				break;
			}
			break;
		case 5:
			hoverRegion = Region.outside;
			break;
		}
	}

	@Override
	public void mouseOut() {

	}

	@Override
	public void mouseDown(int button) {
		if (button == 0) {
			if (this.hoverRegion.equals(Region.verButton)) {
				this.downX = Logic.getMouseX();
				this.downY = Logic.getMouseY();
				this.verDrag = true;
				this.orPos = this.verPos;
			} else if (this.hoverRegion.equals(Region.horButton)) {
				this.downX = Logic.getMouseX();
				this.downY = Logic.getMouseY();
				this.horDrag = true;
				this.orPos = this.horPos;
			}
		}
	}

	@Override
	public void mouseUp(int button, int x, int y) {
		if (this.verDrag) {
			this.verDrag = false;
		} else if (this.horDrag) {
			this.horDrag = false;
		}
	}

	/**
	 * Called when the ScrollBox geometry changes
	 */
	private void resize() {
		Widget content = this.getContent();
		if (content == null) {
			return;
		}
		int w = this.bounds.getWidth();
		int h = this.bounds.getHeight();
		int prefWidth = content.getPreferredWidth(w);
		int prefHeight = content.getPreferredHeight(h);
		int scrollWidth = getScrollBarWidth();
		int minPrefWidth = content.getPreferredWidth(0);
		int minPrefHeight = content.getPreferredHeight(0);

		scrollHor = false;
		scrollVer = false;

		for (int i = 0; i < 2; i++) {
			if (!scrollHor && (prefWidth > w)) {
				scrollHor = true;
				prefHeight -= scrollWidth;
				prefHeight = Math.max(minPrefHeight, prefHeight);
				h -= scrollWidth;
			} else if (!scrollVer && prefHeight > h) {
				scrollVer = true;
				prefWidth -= scrollWidth;
				prefWidth = Math.max(minPrefWidth, prefWidth);
				w -= scrollWidth;
			}
		}

		verLen = (int) (h * ((float) h / (float) prefHeight));
		verPos = (int) (verScrollPos * (h - verLen));

		horLen = (int) (w * ((float) w / (float) prefWidth));
		horPos = (int) (horScrollPos * (w - horLen));

		content.setBounds(prefWidth, prefHeight);
	}

	private int getScrollBarWidth() {
		return Fonts.getNormal().getHeight();
	}

	@Override
	public void setWidth(int w) {
		if (this.bounds.getWidth() != w) {
			this.bounds.setWidth(w);
			resize();
		}
	}

	@Override
	public void setHeight(int h) {
		if (this.bounds.getHeight() != h) {
			this.bounds.setHeight(h);
			resize();
		}
	}

	@Override
	public void setBounds(int w, int h) {
		if (this.bounds.getWidth() != w || this.bounds.getHeight() != h) {
			this.bounds.setBounds(w, h);
			resize();
		}
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
	public int getPreferredWidth(int max) {
		if (this.getContent() != null) {
			return this.getContent().getPreferredWidth(max);
		} else {
			return 0;
		}
	}

	@Override
	public int getPreferredHeight(int max) {
		if (this.getContent() != null) {
			return this.getContent().getPreferredHeight(max);
		} else {
			return 0;
		}
	}

}
