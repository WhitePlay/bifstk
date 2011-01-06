package bifstk.wm.ui;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;

import bifstk.config.Fonts;
import bifstk.config.Theme;
import bifstk.gl.Color;
import bifstk.gl.Util;
import bifstk.wm.geom.Rectangle;

/**
 * Basic single or multiline text editor
 * 
 */
public class Text extends Actionable implements Focusable {

	/** dimensions */
	private Rectangle bounds = null;

	/** length in characters ; < 1 means expand */
	private int length;
	/** false if single line */
	private boolean multiLine = false;

	/** content of the text editor, current line if multiline */
	private StringBuffer content = null;

	/** if multiline */
	private ArrayList<StringBuffer> lines = null;
	/** current line */
	private int line = 0;

	/** caret position in Characters */
	private int pos = 0;
	/** caret position in pixels */
	private int caretPos = 0;

	/** horizontal offset when single line and content is longer than box */
	private int offset = 0;

	/** true when this text editor is focused */
	private boolean focus = false;

	/** there are 2 ctrl keys, so > 0 mean CONTROL down */
	private int ctrlDown = 0;
	/** there are 2 shift keys, so > 0 mean SHIFT down */
	private int shiftDown = 0;

	/** scroll for this editor if multiline */
	private ScrollBox scroll = null;

	/**
	 * Creates a multiline text that expands horizontally and vertically
	 */
	public Text() {
		this(0);
		this.multiLine = true;
		this.lines = new ArrayList<StringBuffer>();
		this.lines.add(this.content);
		this.scroll = new ScrollBox(this);
	}

	/**
	 * Creates a single line text
	 * 
	 * @param length number of characters to display. < 1 means the widget will
	 *            expand
	 */
	public Text(int length) {
		this.bounds = new Rectangle();
		this.length = length;
		this.multiLine = false;
		this.content = new StringBuffer();
	}

	@Override
	public void render(float alpha, Color uiBg, float uiBgAlpha) {
		int w = this.getWidth();
		int h = this.getHeight();
		float a = uiBgAlpha * alpha;

		if (w <= 0 || h <= 0) {
			return;
		}

		w = Math.max(w, Fonts.getNormal().getFontSize());
		h = Math.max(h, Fonts.getNormal().getHeight());

		Util.raster().fillQuad(0, 0, w, h, Color.WHITE, a);

		if (!this.multiLine) {
			String str = this.content.toString();
			renderLine(str, 0, alpha, true);
		} else {
			int yOff = 0;
			for (StringBuffer sb : this.lines) {
				renderLine(sb.toString(), yOff, alpha, this.content.equals(sb));
				yOff += Fonts.getNormal().getHeight();
			}
		}
		Color borderCol = Theme.getUiButtonBorderColor();
		Util.raster().drawQuad(0, 0, w, h, borderCol, a);
	}

	private void renderLine(String str, int yOff, float alpha, boolean drawCaret) {
		Fonts.getNormal().drawString(2 + this.offset, 2 + yOff, str,
				Color.BLACK, alpha);

		// caret
		if (this.focus && System.currentTimeMillis() / 500 % 2 == 0
				&& drawCaret) {
			int len = this.caretPos + this.offset + 2;
			Util.raster().drawQuad(len - 1, 2 + yOff, 2,
					Fonts.getNormal().getHeight(), Color.BLACK, alpha);
		}
	}

	@Override
	public void mouseHover(int x, int y) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseOut() {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseDown(int button) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseUp(int button, int x, int y) {
		this.getFrame().setKeyboardFocus(this);
	}

	@Override
	public void keyEvent(int key, boolean state, char character) {
		if (!state) {
			switch (key) {
			case Keyboard.KEY_LSHIFT:
			case Keyboard.KEY_RSHIFT:
				this.shiftDown--;
				break;
			case Keyboard.KEY_LCONTROL:
			case Keyboard.KEY_RCONTROL:
				this.ctrlDown--;
				break;
			}
			return;
		}

		switch (key) {
		// new line
		case Keyboard.KEY_RETURN:
			if (this.multiLine) {
				StringBuffer nc = new StringBuffer();
				this.line++;
				String cut = this.content.substring(this.pos);
				this.content.delete(this.pos, this.content.length());
				nc.append(cut);
				this.lines.add(this.line, nc);
				this.pos = 0;
				this.content = nc;
			}
			break;

		// remove previous character
		case Keyboard.KEY_BACK:
			if (pos > 0) {
				this.content.deleteCharAt(this.pos - 1);
				this.pos--;
			} else if (pos == 0 && multiLine && line > 0) {
				String cut = this.lines.remove(line).toString();
				line--;
				this.content = this.lines.get(line);
				this.pos = this.content.length();
				this.content.append(cut);
			}
			break;

		// remove next character
		case Keyboard.KEY_DELETE:
			if (pos < this.content.length()) {
				this.content.deleteCharAt(this.pos);
			} else if (pos == this.content.length() && multiLine
					&& line + 1 < lines.size()) {
				String cut = this.lines.remove(line + 1).toString();
				this.content.append(cut);
			}
			break;

		// move caret right
		case Keyboard.KEY_RIGHT:
			if (this.ctrlDown > 0) {
				this.pos = this.getNextWordPos(this.pos);
			} else if (this.pos < this.content.length()) {
				this.pos++;
			} else if (this.pos == this.content.length() && multiLine
					&& line + 1 < lines.size()) {
				this.pos = 0;
				this.line++;
				this.content = this.lines.get(line);
			}
			break;

		// move caret left
		case Keyboard.KEY_LEFT:
			if (this.ctrlDown > 0) {
				this.pos = this.getPrevWordPos(this.pos);
			} else if (this.pos > 0) {
				this.pos--;
			} else if (this.pos == 0 && multiLine && line > 0) {
				this.line--;
				this.content = this.lines.get(line);
				this.pos = content.length();
			}
			break;

		// move caret up
		case Keyboard.KEY_UP:
			if (this.multiLine && this.line > 0) {
				this.line--;
				this.content = this.lines.get(this.line);
				this.pos = Util.clampi(this.pos, 0, this.content.length());
			}
			break;

		// move caret down
		case Keyboard.KEY_DOWN:
			if (this.multiLine && this.line + 1 < this.lines.size()) {
				this.line++;
				this.content = this.lines.get(this.line);
				this.pos = Util.clampi(this.pos, 0, this.content.length());
			}
			break;

		case Keyboard.KEY_LSHIFT:
		case Keyboard.KEY_RSHIFT:
			this.shiftDown++;
			break;
		case Keyboard.KEY_LCONTROL:
		case Keyboard.KEY_RCONTROL:
			this.ctrlDown++;
			break;

		// insert character
		default:
			if (character != Keyboard.CHAR_NONE) {
				char c = character;

				if (shiftDown > 0 && Character.isLetter(c))
					c = Character.toUpperCase(c);

				if (ctrlDown > 0) {
					switch (key) {
					case Keyboard.KEY_A:
						pos = 0;
						break;
					case Keyboard.KEY_E:
						pos = this.content.length();
						break;
					}
					break;
				}

				this.content.insert(this.pos, c);
				this.pos++;
			}
			break;
		}

		String str = this.content.toString();
		this.caretPos = Fonts.getNormal().getWidth(str.substring(0, this.pos));

		// single line : 'silently' scroll right/left
		if (!this.multiLine) {
			if (caretPos + offset > getWidth() - 4) {
				offset = getWidth() - caretPos - 4;
			} else if (caretPos + offset < 2) {
				offset = -caretPos + 2;
			}
		}
		// multiline : scroll the attached ScrollBox
		else {
			int sw = (scroll.isScrollVer()) ? scroll.getWidth()
					- scroll.getScrollBarWidth() : scroll.getWidth();
			int sh = (scroll.isScrollHor()) ? scroll.getHeight()
					- scroll.getScrollBarWidth() : scroll.getHeight();

			if (caretPos + scroll.getXTranslate() > sw - 4) {
				scroll.setXTranslate(caretPos + 4 - sw);
			} else if (caretPos + scroll.getXTranslate() < 2) {
				scroll.setXTranslate(caretPos - 2);
			}

			int mh = (this.line + 1) * Fonts.getNormal().getHeight();
			if (mh - scroll.getYTranslate() > sh - 4) {
				scroll.setYTranslate(mh + 4 - sh);
			} else if (mh - scroll.getYTranslate() < Fonts.getNormal()
					.getHeight() + 2) {
				scroll.setYTranslate(mh - 2 - Fonts.getNormal().getHeight());
			}

			// force scrollBox update
			this.scroll.resize();
		}

	}

	/**
	 * @param pos the current position in the line
	 * @return the position of the next word on the right
	 */
	private int getNextWordPos(int pos) {
		char[] chars = this.content.toString().toCharArray();
		while (true) {
			pos++;

			if (pos >= chars.length)
				return chars.length;

			if (isWordSep(chars[pos])) {
				if (pos + 1 <= chars.length) {
					return pos + 1;
				} else {
					return pos;
				}
			}
		}
	}

	/**
	 * @param pos the current position in the line
	 * @return the position of the previous word on the left
	 */
	private int getPrevWordPos(int pos) {
		pos--;
		char[] chars = this.content.toString().toCharArray();
		while (true) {
			pos--;

			if (pos <= 0)
				return 0;

			if (isWordSep(chars[pos])) {
				return pos + 1;
			}
		}
	}

	/**
	 * @param c a character
	 * @return true if the provided character is a word separator
	 */
	private boolean isWordSep(char c) {
		return Character.isWhitespace(c) || c == ',' || c == ';' || c == '.';
	}

	/**
	 * Using this ScrollBox is not equivalent to creating one with this editor
	 * as content, as this ScrollBox will be bound to the content and the
	 * position of the caret as it is moved.
	 * 
	 * @return if multiline, returns a ScrollBox containing this editor
	 */
	public ScrollBox getScrollBox() {
		return this.scroll;
	}

	@Override
	public void setWidth(int w) {
		this.bounds.setWidth(w);
	}

	@Override
	public void setHeight(int h) {
		this.bounds.setHeight(h);
	}

	@Override
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
	public int getPreferredWidth(int max) {
		if (this.multiLine) {
			return Math.max(max, getMaxLineLength() + 4);
		} else {
			if (this.length < 1) {
				return max;
			} else {
				int w = Fonts.getNormal().getFontSize() * this.length;
				return Math.min(max, w + 4);
			}
		}
	}

	@Override
	public int getPreferredHeight(int max) {
		if (this.multiLine) {
			return Math.max(max, this.lines.size()
					* Fonts.getNormal().getHeight() + 4);
		} else {
			return Fonts.getNormal().getHeight() + 4;
		}
	}

	/**
	 * Only use if multiline
	 */
	private int getMaxLineLength() {
		int res = 0;
		for (StringBuffer sb : this.lines) {
			int len = Fonts.getNormal().getWidth(sb.toString());
			res = Math.max(len, res);
		}
		return res;
	}

	@Override
	public void setFocus(boolean f) {
		this.focus = f;
	}
}
