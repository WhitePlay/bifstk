package bifstk.config;

public enum Property {

	cursorsPath("cursors.path",
			"Path to the directory containing the mouse cursor bitmaps");

	private String property = null;

	private String description = null;

	private Property(String property, String desc) {
		this.property = property;
		this.description = desc;
	}

	public String getProperty() {
		return this.property;
	}

	public String getDescription() {
		return this.description;
	}
}
