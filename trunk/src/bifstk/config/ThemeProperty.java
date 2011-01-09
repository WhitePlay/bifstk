package bifstk.config;

/**
 * Properties to configure the Bifstk theme
 */
public enum ThemeProperty {

	/** STRING path to the textures map image */
	texturesImage("textures.image"),

	/** COLOR root background color */
	rootBackgroundColor("root.background.color"),
	/** COLOR color superposed to the root bg when a modal frame is shown */
	rootBackgroundModalColor("root.background.modal.color"),
	/** FLOAT opacity of the color layered when a modal is shown */
	rootBackgroundModalAlpha("root.background.modal.alpha"),

	/** INT pixel width of the border around the window */
	windowBorderWidth("window.border.width"),
	/** COLOR window border color when focused */
	windowBorderFocusedColor("window.border.focused.color"),
	/** COLOR window border color when not focused */
	windowBorderUnfocusedColor("window.border.unfocused.color"),
	/** COLOR color of the border of the window border */
	windowBorderOuterFocusedColor("window.border.outer.focused.color"),
	/** COLOR color of the border of the window border */
	windowBorderOuterUnfocusedColor("window.border.outer.unfocused.color"),
	/** COLOR window titlebar color when focused */
	windowTitlebarFocusedColor("window.titlebar.focused.color"),
	/** COLOR window titlebar color when not focused */
	windowTitlebarUnfocusedColor("window.titlebar.unfocused.color"),
	/** COLOR window titlebar font color when focused */
	windowTitlebarFocusedFontColor("window.titlebar.focused.font.color"),
	/** COLOR window titlebar font color when not focused */
	windowTitlebarUnfocusedFontColor("window.titlebar.unfocused.font.color"),
	/** FLOAT window opacity when moved */
	windowMovedAlpha("window.moved.alpha"),
	/** FLOAT window opacity when resized */
	windowResizedAlpha("window.resized.alpha"),
	/** FLOAT window opacity when not focused */
	windowUnfocusedAlpha("window.unfocused.alpha"),
	/** BOOL true to enable dropped shadows around windows */
	windowShadowEnabled("window.shadow.enabled"),
	/** FLOAT opacity of the window shadow */
	windowShadowAlpha("window.shadow.alpha"),
	/** STRING path to the 'corner' shadow image */
	windowShadowCornerImage("window.shadow.corner.image"),
	/** STRING path to the 'side' shadow image */
	windowShadowSideImage("window.shadow.side.image"),
	/** COLOR base color of a Window ui */
	windowFocusedColor("window.focused.color"),
	/** COLOR base color of a Window ui when not focused */
	windowUnfocusedColor("window.unfocused.color"),
	/** FLOAT base opacity of the window ui */
	windowFocusedAlpha("window.focused.alpha"),
	/** COLOR color of the window shadow when focused */
	windowShadowFocusedColor("window.shadow.focused.color"),
	/** COLOR color of the window shadow when not focused */
	windowShadowUnfocusedColor("window.shadow.unfocused.color"),

	/** INT pixel width of the border around the area */
	areaBorderWidth("area.border.width"),
	/** COLOR focused color of the area */
	areaFocusedColor("area.focused.color"),
	/** COLOR unfocused color of the area */
	areaUnfocusedColor("area.unfocused.color"),
	/** COLOR focused color of the outer border */
	areaBorderFocusedColor("area.border.focused.color"),
	/** COLOR unfocused color of the outer border */
	areaBorderUnfocusedColor("area.border.unfocused.color"),
	/** FLOAT opacity of the area ui */
	areaFocusedAlpha("area.focused.alpha"),
	/** FLOAT opacity if of the area when not focused */
	areaUnfocusedAlpha("area.unfocused.alpha"),

	/** List<bifstk.wm.Frame.Controls> title frame controls order */
	frameControlsOrder("frame.controls.order"),
	/** INT width of the frame controls */
	frameControlsWidth("frame.controls.width"),
	/** INT height of the frame controls */
	frameControlsHeight("frame.controls.height"),
	/** INT spacing border between frame controls */
	frameControlsBorder("frame.controls.border"),
	/** COLOR close frame control color */
	frameControlsCloseColor("frame.controls.close.color"),
	/** COLOR close frame control color when hovered */
	frameControlsCloseHoverColor("frame.controls.close.hover.color"),
	/** COLOR close frame control color when clicked */
	frameControlsCloseClickColor("frame.controls.close.click.color"),
	/** COLOR maximize frame control color */
	frameControlsMaximizeColor("frame.controls.maximize.color"),
	/** COLOR maximize frame control color when hovered */
	frameControlsMaximizeHoverColor("frame.controls.maximize.hover.color"),
	/** COLOR maximize frame control color when hovered */
	frameControlsMaximizeClickColor("frame.controls.maximize.click.color"),

	/** COLOR base font color of the UI */
	uiFontColor("ui.font.color"),
	/** COLOR color of the ui button border */
	uiButtonBorderColor("ui.button.border.color"),
	/** COLOR color of the ui button background */
	uiButtonColor("ui.button.color"),
	/** COLOR color of the ui button background when focused */
	uiButtonHoverColor("ui.button.hover.color"),
	/** COLOR color of the ui button background when clicked */
	uiButtonClickColor("ui.button.click.color");

	private String name = "";

	public String getName() {
		return this.name;
	}

	private ThemeProperty(String name) {
		this.name = name;
	}
}