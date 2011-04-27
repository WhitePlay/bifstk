package bifstk.config;

/**
 * Properties to configure the Bifstk theme
 */
public enum ThemeProperty {

	// STRING path to the textures map image
	texturesImage("textures.image"),

	// COLOR root background color
	rootBackgroundColor("root.background.color"),
	// COLOR color superposed to the root bg when a modal frame is shown 
	rootBackgroundModalColor("root.background.modal.color"),
	// FLOAT opacity of the color layered when a modal is shown 
	rootBackgroundModalAlpha("root.background.modal.alpha"),

	// List<bifstk.wm.Frame.Controls> title frame controls order 
	frameControlsOrder("frame.controls.order"),
	// INT width of the frame controls 
	frameControlsWidth("frame.controls.width"),
	// INT height of the frame controls 
	frameControlsHeight("frame.controls.height"),
	// INT spacing border between frame controls 
	frameControlsBorder("frame.controls.border"),
	// COLOR close frame control color 
	frameControlsCloseColor("frame.controls.close.color"),
	// COLOR close frame control color when hovered 
	frameControlsCloseHoverColor("frame.controls.close.hover.color"),
	// COLOR close frame control color when clicked 
	frameControlsCloseClickColor("frame.controls.close.click.color"),
	// COLOR close frame control color when not focused 
	frameControlsCloseUnfocusedColor("frame.controls.close.unfocused.color"),
	// COLOR maximize frame control color 
	frameControlsMaximizeColor("frame.controls.maximize.color"),
	// COLOR maximize frame control color when hovered 
	frameControlsMaximizeHoverColor("frame.controls.maximize.hover.color"),
	// COLOR maximize frame control color when hovered 
	frameControlsMaximizeClickColor("frame.controls.maximize.click.color"),
	// COLOR maximize frame control color when not focused 
	frameControlsMaximizeUnfocusedColor(
			"frame.controls.maximize.unfocused.color"),

	// INT pixel width of the border around the window 
	windowBorderWidth("window.border.width"),
	// COLOR color of the border of the window 
	windowBorderColor("window.border.color"),
	// COLOR base color of a Window ui 
	windowColor("window.color"),
	// COLOR window titlebar color when focused 
	windowTitlebarColor("window.titlebar.color"),
	// MASK color mask to add when not focused
	windowUnfocusedMask("window.unfocused.mask"),
	// FLOAT base opacity of the window ui 
	windowAlpha("window.alpha"),
	// FLOAT window opacity when moved 
	windowMovedAlpha("window.moved.alpha"),
	// FLOAT window opacity when resized 
	windowResizedAlpha("window.resized.alpha"),
	// FLOAT window opacity when not focused 
	windowUnfocusedAlpha("window.unfocused.alpha"),
	// BOOL true to enable dropped shadows around windows 
	windowShadowEnabled("window.shadow.enabled"),
	// FLOAT opacity of the window shadow 
	windowShadowAlpha("window.shadow.alpha"),
	// COLOR color of the window shadow when focused 
	windowShadowFocusedColor("window.shadow.focused.color"),
	// COLOR color of the window shadow when not focused 
	windowShadowUnfocusedColor("window.shadow.unfocused.color"),

	// INT pixel width of the border around the area 
	areaBorderWidth("area.border.width"),
	// STRING 3 strings in [-255,255] separated by spaces
	areaUnfocusedMask("area.unfocused.mask"),
	// COLOR focused color of the area 
	areaColor("area.color"),
	// COLOR focused color of the outer border 
	areaBorderColor("area.border.color"),
	// FLOAT opacity of the area ui 
	areaAlpha("area.alpha"),
	// FLOAT opacity if of the area when not focused 
	areaUnfocusedAlpha("area.unfocused.alpha"),

	// COLOR font color mask of the UI 
	uiFontMask("ui.font.mask"),
	// MASK color mask of the ui widgets border 
	uiBorderMask("ui.border.mask"),
	// MASK color mask of the ui button background 
	uiButtonMask("ui.button.mask"),
	// MASK color mask of the ui button background 
	uiButtonHoverMask("ui.button.hover.mask"),
	// MASK color mask of the ui button background when clicked 
	uiButtonClickMask("ui.button.click.mask"),
	// MASK color mask of the ui label bound to a button when hovered
	uiButtonLabelHoverMask("ui.button.label.hover.mask"),
	// MASK color of the ui entries (text,radio,..) bg 
	uiEntryMask("ui.entry.mask"),
	// COLOR fg font color mask for ui entry widgets (text) 
	uiEntryFontMask("ui.entry.font.mask"),
	// MASK color mask of the gradient on top of the focused tab 
	uiTabFocusedHighlightMask("ui.tab.focused.highlight.mask"),
	// MASK bg color mask for the whole tabs group widget
	uiTabsMask("ui.tabs.mask"),
	// MASK border color mask for tabs group widget
	uiTabsBorderMask("ui.tabs.border.mask"),
	// MASK border color mask of unfocused tabs 
	uiTabUnfocusedBorderMask("ui.tab.unfocused.border.mask"),
	// MASK font color mask of unfocused tabs 
	uiTabUnfocusedFontMask("ui.tab.unfocused.font.mask"),
	// MASK bg color mask of unfocused tabs 
	uiTabUnfocusedBackgroundMask("ui.tab.unfocused.background.mask"),
	// MASK color mask of the gradient on top of unfocused tabs 
	uiTabUnfocusedHighlightMask("ui.tab.unfocused.highlight.mask"),
	// MASK color mask of the checked checkbox image
	uiCheckMask("ui.check.mask"),
	// MASK color mask of the checked checkbox image when hovered
	uiCheckHoverMask("ui.check.hover.mask"),
	// MASK color mask of the checked checkbox image when clicked
	uiCheckClickMask("ui.check.click.mask");

	private String name = "";

	public String getName() {
		return this.name;
	}

	private ThemeProperty(String name) {
		this.name = name;
	}
}