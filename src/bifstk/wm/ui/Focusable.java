package bifstk.wm.ui;

import org.lwjgl.input.Keyboard;

/**
 * A focusable Widget can receive focus within a Frame and then be forwarded
 * keyboard events
 * 
 * 
 */
public interface Focusable {

	/**
	 * A keyboard event was sent from the WM to this widget
	 * 
	 * @param key code of the key, see {@link Keyboard#getEventKey()}
	 * @param state state of the key. up or down, see
	 *            {@link Keyboard#getEventKeyState()}
	 * @param character actual character of the key if human-readable, see
	 *            {@link Keyboard#getEventCharacter()}
	 */
	public abstract void keyEvent(int key, boolean state, char character);

	/**
	 * @param f true to bring focus to this Widget, false to lose it
	 */
	public void setFocus(boolean f);

}
