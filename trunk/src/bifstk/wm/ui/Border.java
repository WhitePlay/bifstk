package bifstk.wm.ui;

/**
 * A Border is a Container with a capacity of 1 that draws a border around its
 * content
 * 
 */
public abstract class Border extends Container {

	/** content */
	private Widget content = null;

	/**
	 * Default constructor
	 * 
	 * @param content content of the Border
	 */
	public Border(Widget content) {
		this.setContent(content);
	}

	/**
	 * @return the content of the border
	 */
	protected Widget getContent() {
		return this.content;
	}

	/**
	 * @param w the Widget contained by this border
	 */
	public void setContent(Widget w) {
		super.add(w);
		if (this.content != null) {
			this.content.setParent(null);
		}
		this.content = w;
	}

	@Override
	public void removeChild(Widget w) {
		if (this.content != null && this.content.equals(w)) {
			this.content.setParent(null);
			this.content = null;
		}
	}

	@Override
	public boolean hasChildren() {
		return this.content != null;
	}

	@Override
	public void clearChildren() {
		this.content.setParent(null);
		this.content = null;
	}

	@Override
	protected void update() {
		// has no real meaning here
	}

}
