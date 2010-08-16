package bifstk.wm.ui;

/**
 * A Border is a Container with a capacity of 1 that draws a border around its
 * content
 * 
 */
public abstract class Border implements Container {

	/** content */
	private Widget content = null;

	/** Container containing this Border */
	private Container parent = null;

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

	@Override
	public void addChild(Widget w) {
		this.setContent(w);
	}

	/**
	 * @param w the Widget contained by this border
	 */
	public void setContent(Widget w) {
		Container parent = w.getParent();
		if (parent != null) {
			parent.removeChild(w);
		}
		w.setParent(this);

		this.content = w;
	}

	@Override
	public void removeChild(Widget w) {
		if (this.content != null && this.content.equals(w)) {
			this.content = null;
		}
	}

	@Override
	public boolean hasChildren() {
		return this.content != null;
	}

	@Override
	public void clearChildren() {
		this.content = null;
	}

	@Override
	public void setParent(Container c) {
		this.parent = c;
	}

	@Override
	public Container getParent() {
		return this.parent;
	}

}
