package bifstk.wm.ui;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

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

	/** content of the text editor */
	private StringBuffer content = null;

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

	/**
	 * Creates a multiline text that expands horizontally and vertically
	 */
	public Text() {
		this(0);
		this.multiLine = true;
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

		Color borderCol = Theme.getUiButtonBorderColor();
		float[] c1 = borderCol.toArray(8, a);
		float[] c2 = Color.WHITE.toArray(4, a);
		int[] v = new int[] {
				0, 0, //
				w, 0, //
				w, h, //
				0, h
		};

		Util.draw2D(v, c2, GL11.GL_QUADS);

		Color col = Color.BLACK;
		String str = this.content.toString();
		Fonts.getNormal().drawString(2 + this.offset, 2, str, col, alpha);

		// caret
		if (this.focus && System.currentTimeMillis() / 500 % 2 == 0) {
			int len = this.caretPos + this.offset + 2;
			int[] cv = {
					len - 1, 2, //
					len + 1, 2, //
					len + 1, h - 2, //
					len - 1, h - 2
			};
			Util.draw2DLineLoop(cv, Color.BLACK.toArray(8, a));
		}

		Util.draw2DLineLoop(v, c1);
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
				this.content.insert(this.pos, '\n');
				this.pos++;
			}
			break;
			
		// remove character
		case Keyboard.KEY_BACK:
			if (pos > 0) {
				this.content.deleteCharAt(this.pos - 1);
				this.pos--;
			}
			break;
		case Keyboard.KEY_DELETE:
			if (pos < this.content.length()) {
				this.content.deleteCharAt(this.pos);
			}
			break;
			
		// move caret right
		case Keyboard.KEY_RIGHT:
			if (this.ctrlDown > 0) {
				this.pos = this.getNextWordPos(this.pos);
			} else if (this.pos < this.content.length())
				this.pos++;
			break;
			
		// move caret left
		case Keyboard.KEY_LEFT:
			if (this.ctrlDown > 0) {
				this.pos = this.getPrevWordPos(this.pos);
			} else if (this.pos > 0)
				this.pos--;
			break;
			
		// move caret up
		case Keyboard.KEY_UP:
			
			// move caret down
		case Keyboard.KEY_DOWN:
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

		if (!this.multiLine) {
			if (caretPos + offset > getWidth() - 4) {
				offset = getWidth() - caretPos - 4;
			}
			if (caretPos + offset < 2) {
				offset = -caretPos + 2;
			}
		}
	}

	/**
	 * @param pos the current position in the word
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

	private int getPrevWordPos(int pos) {
		pos--;
		char[] chars = this.content.toString().toCharArray();
		while (true) {
			pos--;

			if (pos <= 0)
				return 0;

			if (isWordSep(chars[pos])) {
				return pos +1;
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
		if (this.length < 1) {
			return max;
		} else {
			int w = Fonts.getNormal().getFontSize() * this.length;
			return Math.min(max, w + 4);
		}
	}

	@Override
	public int getPreferredHeight(int max) {
		if (this.multiLine) {
			return max;
		} else {
			return Math.min(max, Fonts.getNormal().getHeight() + 4);
		}
	}

	@Override
	public void setFocus(boolean f) {
		this.focus = f;
	}
}
