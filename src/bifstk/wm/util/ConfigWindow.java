package bifstk.wm.util;

import bifstk.Bifstk;
import bifstk.Handler;
import bifstk.config.Config;
import bifstk.config.ConfigProperty;
import bifstk.wm.Window;
import bifstk.wm.ui.Actionable;
import bifstk.wm.ui.Button;
import bifstk.wm.ui.Checkbox;
import bifstk.wm.ui.FlowBox;
import bifstk.wm.ui.FlowBox.Orientation;
import bifstk.wm.ui.Label;
import bifstk.wm.ui.ScrollBox;
import bifstk.wm.ui.Tabs;
import bifstk.wm.ui.Text;
import bifstk.wm.ui.Text.Filter;

/**
 * Stock graphical utility edit all {@link ConfigProperty} values
 * 
 * 
 */
public final class ConfigWindow extends Window implements Handler {

	private static ConfigWindow instance = null;

	/* Display */
	private Text width;
	private Text height;
	private Checkbox fullScreen;
	private Text title;
	private Text fps;
	private Checkbox capFps;
	private Checkbox vsync;
	private Text samples;

	/* WM */
	private Checkbox focusFollowMouse;
	private Checkbox debugLayout;
	private Checkbox windowSnapTop;
	private Checkbox windowDockLeft;
	private Checkbox windowDockRight;
	private Checkbox frameSnap;
	private Checkbox frameAnims;
	private Text frameAnimLen;
	private Text frameMinSize;
	private Text frameSnapRadius;

	private ConfigWindow() {
		super(50, 50, 300, 200);
		this.build();
		this.reset();
	}

	public static ConfigWindow getInstance() {
		if (ConfigWindow.instance == null) {
			ConfigWindow.instance = new ConfigWindow();
		}
		return ConfigWindow.instance;
	}

	private void build() {
		setTitle("Configuration");

		// Display properties
		FlowBox displayBox = new FlowBox(FlowBox.Orientation.VERTICAL);

		FlowBox widthBox = new FlowBox(FlowBox.Orientation.HORIZONTAL);
		width = new Text(4, Filter.NUM);
		widthBox.addBegin(new Label("Width"));
		widthBox.addEnd(width);
		displayBox.addBegin(widthBox);

		FlowBox heightBox = new FlowBox(FlowBox.Orientation.HORIZONTAL);
		height = new Text(4, Filter.NUM);
		heightBox.addBegin(new Label("Height"));
		heightBox.addEnd(height);
		displayBox.addBegin(heightBox);

		FlowBox fsBox = new FlowBox(FlowBox.Orientation.HORIZONTAL);
		fullScreen = new Checkbox();
		fsBox.addBegin(fullScreen);
		fsBox.addBegin(new Label("Fullscreen"));
		fsBox.bindButton(fullScreen);
		displayBox.addBegin(fsBox);

		FlowBox titleBox = new FlowBox(FlowBox.Orientation.HORIZONTAL);
		title = new Text(4, Filter.ALNUM);
		titleBox.addBegin(new Label("Title"));
		titleBox.addEnd(title);
		displayBox.addBegin(titleBox);

		FlowBox fpsBox = new FlowBox(FlowBox.Orientation.HORIZONTAL);
		fps = new Text(2, Filter.NUM);
		fpsBox.addBegin(new Label("FPS"));
		fpsBox.addEnd(fps);
		displayBox.addBegin(fpsBox);

		FlowBox capBox = new FlowBox(FlowBox.Orientation.HORIZONTAL);
		capFps = new Checkbox();
		capBox.addBegin(capFps);
		capBox.addBegin(new Label("Cap FPS"));
		capBox.bindButton(capFps);
		displayBox.addBegin(capBox);

		FlowBox vsyncBox = new FlowBox(FlowBox.Orientation.HORIZONTAL);
		vsync = new Checkbox();
		vsyncBox.addBegin(vsync);
		vsyncBox.addBegin(new Label("VSync"));
		vsyncBox.bindButton(vsync);
		displayBox.addBegin(vsyncBox);

		FlowBox samplesBox = new FlowBox(FlowBox.Orientation.HORIZONTAL);
		samples = new Text(2, Filter.NUM);
		samplesBox.addBegin(new Label("AA samples"));
		samplesBox.addEnd(samples);
		displayBox.addBegin(samplesBox);

		// WM properties
		FlowBox wmBox = new FlowBox(FlowBox.Orientation.VERTICAL);

		FlowBox focusBox = new FlowBox(FlowBox.Orientation.HORIZONTAL);
		focusFollowMouse = new Checkbox();
		focusBox.addBegin(focusFollowMouse);
		focusBox.addBegin(new Label("Focus follow mouse"));
		focusBox.bindButton(focusFollowMouse);
		wmBox.addBegin(focusBox);

		FlowBox debugLayoutBox = new FlowBox(FlowBox.Orientation.HORIZONTAL);
		debugLayout = new Checkbox();
		debugLayoutBox.addBegin(debugLayout);
		debugLayoutBox.addBegin(new Label("Debug layout"));
		debugLayoutBox.bindButton(debugLayout);
		wmBox.addBegin(debugLayoutBox);

		FlowBox frameAnimBox = new FlowBox(FlowBox.Orientation.HORIZONTAL);
		frameAnims = new Checkbox();
		frameAnimBox.addBegin(frameAnims);
		frameAnimBox.addBegin(new Label("Animations"));
		frameAnimBox.bindButton(frameAnims);
		wmBox.addBegin(frameAnimBox);

		FlowBox frameAnimLenBox = new FlowBox(FlowBox.Orientation.HORIZONTAL);
		frameAnimLen = new Text(4, Filter.NUM);
		frameAnimLenBox.addBegin(new Label("Animation duration"));
		frameAnimLenBox.addEnd(frameAnimLen);
		wmBox.addBegin(frameAnimLenBox);

		FlowBox snapTopBox = new FlowBox(FlowBox.Orientation.HORIZONTAL);
		windowSnapTop = new Checkbox();
		snapTopBox.addBegin(windowSnapTop);
		snapTopBox.addBegin(new Label("Snap Windows top"));
		snapTopBox.bindButton(windowSnapTop);
		wmBox.addBegin(snapTopBox);

		FlowBox frameSizeBox = new FlowBox(FlowBox.Orientation.HORIZONTAL);
		frameMinSize = new Text(4, Filter.NUM);
		frameSizeBox.addBegin(new Label("Minimum window size"));
		frameSizeBox.addEnd(frameMinSize);
		wmBox.addBegin(frameSizeBox);

		FlowBox dockLeftBox = new FlowBox(FlowBox.Orientation.HORIZONTAL);
		windowDockLeft = new Checkbox();
		dockLeftBox.addBegin(windowDockLeft);
		dockLeftBox.addBegin(new Label("Left dock"));
		dockLeftBox.bindButton(windowDockLeft);
		wmBox.addBegin(dockLeftBox);

		FlowBox dockRightBox = new FlowBox(FlowBox.Orientation.HORIZONTAL);
		windowDockRight = new Checkbox();
		dockRightBox.addBegin(windowDockRight);
		dockRightBox.addBegin(new Label("Right dock"));
		dockRightBox.bindButton(windowDockRight);
		wmBox.addBegin(dockRightBox);

		FlowBox frameSnapBox = new FlowBox(FlowBox.Orientation.HORIZONTAL);
		frameSnap = new Checkbox();
		frameSnapBox.addBegin(frameSnap);
		frameSnapBox.addBegin(new Label("Magnetic windows"));
		frameSnapBox.bindButton(frameSnap);
		wmBox.addBegin(frameSnapBox);

		FlowBox frameSnapRadBox = new FlowBox(FlowBox.Orientation.HORIZONTAL);
		frameSnapRadius = new Text(3, Filter.NUM);
		frameSnapRadBox.addBegin(new Label("Attraction threshold"));
		frameSnapRadBox.addEnd(frameSnapRadius);
		wmBox.addBegin(frameSnapRadBox);

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

		Tabs tabs = new Tabs();
		tabs.addTab(new ScrollBox(displayBox), "Display");
		tabs.addTab(new ScrollBox(wmBox), "Windows");
		tabs.addTab(new Label("Sample text"), "Tab");

		FlowBox contentBox = new FlowBox(Orientation.VERTICAL);
		contentBox.setExpand(tabs);
		contentBox.addEnd(butBox);

		setContent(contentBox);

		pack();
	}

	private void reset() {
		Config c = Config.get();

		this.width.setText("" + c.getDisplayWidth());
		this.height.setText("" + c.getDisplayHeight());
		this.fullScreen.setChecked(c.isDisplayFullscreen());
		this.title.setText(c.getDisplayTitle());
		this.fps.setText("" + c.getDisplayFps());
		this.capFps.setChecked(c.isDisplayFpsCap());
		this.vsync.setChecked(c.isDisplayVsync());
		this.samples.setText("" + c.getDisplayAntialiasSamples());

		this.focusFollowMouse.setChecked(c.isWmFocusFollowmouse());
		this.debugLayout.setChecked(c.isWmDebugLayout());
		this.windowSnapTop.setChecked(c.isWmWindowSnapTop());
		this.windowDockLeft.setChecked(c.isWmWindowDockLeft());
		this.windowDockRight.setChecked(c.isWmWindowDockRight());
		this.frameSnap.setChecked(c.isWmFrameSnap());
		this.frameAnims.setChecked(c.isWmAnimations());
		this.frameAnimLen.setText("" + c.getWmAnimationsLength());
		this.frameMinSize.setText("" + c.getWmFrameSizeMin());
		this.frameSnapRadius.setText("" + c.getWmFrameSnapRadius());
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
