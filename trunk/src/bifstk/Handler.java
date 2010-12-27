package bifstk;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import bifstk.wm.ui.Actionable;

/**
 * Client-side event handler
 * <p>
 * Processes events sent by the Bifstk runtime from the user side, then
 * interacts back with the runtime using the API
 * 
 * @see Mouse
 * @see Keyboard
 */
public interface Handler {

	/**
	 * Called once after Display creation, and before rendering the first frame
	 */
	public void init();

	/**
	 * Called when a keyboard event was sent from the runtime to the client
	 * <p>
	 * Events sent to this listener are those that were not processed nor
	 * meaningful to the Bifstk Window Manager, and forwarded to the user
	 * 
	 * @param key code of the key, see {@link Keyboard#getEventKey()}
	 * @param state state of the key; up or down, see
	 *            {@link Keyboard#getEventKeyState()}
	 * @param character actual character of the key if human-readable, see
	 *            {@link Keyboard#getEventCharacter()}
	 */
	public void keyEvent(int key, boolean state, char character);

	/**
	 * Called when a Mouse event was sent from the runtime to the client
	 * <p>
	 * Events sent to this listener are those that were not processed nor
	 * meaningful to the Bifstk Window Manager, and forwarded to the user
	 * 
	 * @param button mouse button, see {@link Mouse#getEventButton()}
	 * @param x abscissa position of the mouse in OpenGL coordinates, see
	 *            {@link Mouse#getEventX()}
	 * @param y ordinate position of the mouse in OpenGL coordinates, see
	 *            {@link Mouse#getEventY()}
	 * @param state mouse button up or down, see
	 *            {@link Mouse#getEventButtonState()}
	 */
	public void mouseEvent(int button, int x, int y, boolean state);

	/**
	 * Called by an Actionable Widget when an action has been performed
	 * <p>
	 * Sends a command to distinguish itself as well as a reference to itself
	 * 
	 * @param action action command as specified by
	 *            {@link Actionable#setAction(String)}
	 * @param source source of the action
	 */
	public void actionPerformed(String action, Actionable source);

}
