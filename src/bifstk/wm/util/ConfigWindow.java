package bifstk.wm.util;

import bifstk.Bifstk;
import bifstk.Handler;
import bifstk.config.Config;
import bifstk.config.ConfigProperty;
import bifstk.wm.Window;
import bifstk.wm.ui.Actionable;
import bifstk.wm.ui.Button;
import bifstk.wm.ui.Checkbox;
import bifstk.wm.ui.CustomBorder;
import bifstk.wm.ui.FlowBox;
import bifstk.wm.ui.FlowBox.Orientation;
import bifstk.wm.ui.Label;
import bifstk.wm.ui.ScrollBox;

/**
 * Stock graphical utility edit all {@link ConfigProperty} values
 * 
 * 
 */
public final class ConfigWindow extends Window implements Handler {

	private static ConfigWindow instance = null;

	/* Display */
	private Checkbox fullScreen;
	private Checkbox capFps;
	private Checkbox vsync;

	/* WM */
	private Checkbox focusFollowMouse;
	private Checkbox debugLayout;
	private Checkbox windowSnapTop;
	private Checkbox windowDockLeft;
	private Checkbox windowDockRight;
	private Checkbox frameSnap;
	private Checkbox frameAnims;

	private ConfigWindow(int x, int y) {
		super(x, y);
		this.build();
		this.reset();
	}

	public static ConfigWindow getInstance() {
		if (ConfigWindow.instance == null) {
			ConfigWindow.instance = new ConfigWindow(50, 20);
		}
		return ConfigWindow.instance;
	}

	private void build() {
		setTitle("Configuration");

		// Display properties 
		FlowBox displayBox = new FlowBox(FlowBox.Orientation.VERTICAL);

		FlowBox fsBox = new FlowBox(FlowBox.Orientation.HORIZONTAL);
		fullScreen = new Checkbox();
		fsBox.addBegin(new Label("Fullscreen"));
		fsBox.addEnd(fullScreen);
		fsBox.bindButton(fullScreen);
		displayBox.addBegin(fsBox);

		FlowBox capBox = new FlowBox(FlowBox.Orientation.HORIZONTAL);
		capFps = new Checkbox();
		capBox.addBegin(new Label("Cap FPS"));
		capBox.addEnd(capFps);
		capBox.bindButton(capFps);
		displayBox.addBegin(capBox);

		FlowBox vsyncBox = new FlowBox(FlowBox.Orientation.HORIZONTAL);
		vsync = new Checkbox();
		vsyncBox.addBegin(new Label("VSync"));
		vsyncBox.addEnd(vsync);
		vsyncBox.bindButton(vsync);
		displayBox.addBegin(vsyncBox);

		// WM properties
		FlowBox wmBox = new FlowBox(FlowBox.Orientation.VERTICAL);

		FlowBox focusBox = new FlowBox(FlowBox.Orientation.HORIZONTAL);
		focusFollowMouse = new Checkbox();
		focusBox.addBegin(new Label("Focus follow mouse"));
		focusBox.addEnd(focusFollowMouse);
		focusBox.bindButton(focusFollowMouse);
		wmBox.addBegin(focusBox);

		FlowBox debugLayoutBox = new FlowBox(FlowBox.Orientation.HORIZONTAL);
		debugLayout = new Checkbox();
		debugLayoutBox.addBegin(new Label("Debug layout"));
		debugLayoutBox.addEnd(debugLayout);
		debugLayoutBox.bindButton(debugLayout);
		wmBox.addBegin(debugLayoutBox);

		FlowBox snapTopBox = new FlowBox(FlowBox.Orientation.HORIZONTAL);
		windowSnapTop = new Checkbox();
		snapTopBox.addBegin(new Label("Snap Windows top"));
		snapTopBox.addEnd(windowSnapTop);
		snapTopBox.bindButton(windowSnapTop);
		wmBox.addBegin(snapTopBox);

		FlowBox dockLeftBox = new FlowBox(FlowBox.Orientation.HORIZONTAL);
		windowDockLeft = new Checkbox();
		dockLeftBox.addBegin(new Label("Left dock"));
		dockLeftBox.addEnd(windowDockLeft);
		dockLeftBox.bindButton(windowDockLeft);
		wmBox.addBegin(dockLeftBox);

		FlowBox dockRightBox = new FlowBox(FlowBox.Orientation.HORIZONTAL);
		windowDockRight = new Checkbox();
		dockRightBox.addBegin(new Label("Right dock"));
		dockRightBox.addEnd(windowDockRight);
		dockRightBox.bindButton(windowDockRight);
		wmBox.addBegin(dockRightBox);

		FlowBox frameSnapBox = new FlowBox(FlowBox.Orientation.HORIZONTAL);
		frameSnap = new Checkbox();
		frameSnapBox.addBegin(new Label("Magnetic windows"));
		frameSnapBox.addEnd(frameSnap);
		frameSnapBox.bindButton(frameSnap);
		wmBox.addBegin(frameSnapBox);

		FlowBox frameAnimBox = new FlowBox(FlowBox.Orientation.HORIZONTAL);
		frameAnims = new Checkbox();
		frameAnimBox.addBegin(new Label("Animations"));
		frameAnimBox.addEnd(frameAnims);
		frameAnimBox.bindButton(frameAnims);
		wmBox.addBegin(frameAnimBox);

		// Buttons
		FlowBox butBox = new FlowBox(FlowBox.Orientation.HORIZONTAL);
		Button applyButton = new Button("Apply");
		applyButton.setAction("apply");
		applyButton.setHandler(this);
		Button cancelButton = new Button("Cancel");
		cancelButton.setAction("cancel");
		cancelButton.setHandler(this);
		butBox.addEnd(applyButton);
		butBox.addEnd(cancelButton);

		FlowBox vbox = new FlowBox(FlowBox.Orientation.VERTICAL);
		vbox.addBegin(new CustomBorder(displayBox, "Display"));
		vbox.addBegin(new CustomBorder(wmBox, "Window manager"));

		ScrollBox scroll = new ScrollBox(vbox);

		FlowBox contentBox = new FlowBox(Orientation.VERTICAL);
		contentBox.setExpand(scroll);
		contentBox.addEnd(butBox);

		setContent(contentBox);

		pack();
	}

	private void reset() {
		Config c = Config.get();

		this.fullScreen.setChecked(c.isDisplayFullscreen());
		this.capFps.setChecked(c.isDisplayFpsCap());
		this.vsync.setChecked(c.isDisplayVsync());

		this.focusFollowMouse.setChecked(c.isWmFocusFollowmouse());
		this.debugLayout.setChecked(c.isWmDebugLayout());
		this.windowSnapTop.setChecked(c.isWmWindowSnapTop());
		this.windowDockLeft.setChecked(c.isWmWindowDockLeft());
		this.windowDockRight.setChecked(c.isWmWindowDockRight());
		this.frameSnap.setChecked(c.isWmFrameSnap());
		this.frameAnims.setChecked(c.isWmAnimations());
	}

	@Override
	public void actionPerformed(String action, Actionable source) {
		if (action.equals("apply")) {
			Config c = Config.get();

			c.setDisplayFullScreen(this.fullScreen.isChecked());
			c.setDisplayFpsCap(this.capFps.isChecked());
			c.setDisplayVsync(this.vsync.isChecked());

			c.setWmFocusFollowmouse(this.focusFollowMouse.isChecked());
			c.setWmDebugLayout(this.debugLayout.isChecked());
			c.setWmWindowSnapTop(this.windowSnapTop.isChecked());
			c.setWmWindowDockLeft(this.windowDockLeft.isChecked());
			c.setWmWindowDockRight(this.windowDockRight.isChecked());
			c.setWmFrameSnap(this.frameSnap.isChecked());
			c.setWmAnimations(this.frameAnims.isChecked());
		} else if (action.equals("cancel")) {
			Bifstk.removeWindow(this);
		}
	}

	@Override
	public void keyEvent(int key, boolean state, char character) {
	}

	@Override
	public void mouseEvent(int button, int x, int y, boolean state) {
	}

}
