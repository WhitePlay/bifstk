package bifstk.wm.ui;

import bifstk.Handler;

/**
 * A Widget that can notify a Handler that an action has been performed
 * <p>
 * See {@link Handler#actionPerformed(String, Actionable)}, which uses
 * {@link #getAction()} as command and this as source.
 * 
 */
public abstract class Actionable extends Widget {

	/** client-side event handler */
	private Handler handler = null;
	/** action command */
	private String action = "";

	/**
	 * @param h the client-side event handler to notify upon action
	 */
	public void setHandler(Handler h) {
		this.handler = h;
	}

	/**
	 * @param action the action command to pass upon event
	 */
	public void setAction(String action) {
		this.action = action;
	}

	/**
	 * @return the client-side event handler to notify upon action
	 */
	public Handler getHandler() {
		return this.handler;
	}

	/**
	 * @return the action command to pass upon event
	 */
	public String getAction() {
		return this.action;
	}

}
