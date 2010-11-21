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
import bifstk.wm.ui.Label;

/**
 * Stock graphical utility edit all {@link ConfigProperty} values
 * 
 * 
 */
public final class ConfigWindow extends Window implements Handler {

	private static ConfigWindow instance = null;

	private Checkbox focusFollowMouse;
	private Checkbox debugLayout;
	private Checkbox windowSnapTop;
	private Checkbox windowDockLeft;
	private Checkbox windowDockRight;
	private Checkbox frameSnap;
	private Checkbox frameAnims;

	private ConfigWindow() {
		super(300, 300);
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

		FlowBox vbox = new FlowBox(FlowBox.Orientation.VERTICAL);

		FlowBox focusBox = new FlowBox(FlowBox.Orientation.HORIZONTAL);
		focusFollowMouse = new Checkbox();
		focusBox.addBefore(new Label("Focus follow mouse"));
		focusBox.addAfter(focusFollowMouse);
		vbox.addBefore(focusBox);

		FlowBox debugLayoutBox = new FlowBox(FlowBox.Orientation.HORIZONTAL);
		debugLayout = new Checkbox();
		debugLayoutBox.addBefore(new Label("Debug layout"));
		debugLayoutBox.addAfter(debugLayout);
		vbox.addBefore(debugLayoutBox);

		FlowBox snapTopBox = new FlowBox(FlowBox.Orientation.HORIZONTAL);
		windowSnapTop = new Checkbox();
		snapTopBox.addBefore(new Label("Snap Windows top"));
		snapTopBox.addAfter(windowSnapTop);
		vbox.addBefore(snapTopBox);

		FlowBox dockLeftBox = new FlowBox(FlowBox.Orientation.HORIZONTAL);
		windowDockLeft = new Checkbox();
		dockLeftBox.addBefore(new Label("Left dock"));
		dockLeftBox.addAfter(windowDockLeft);
		vbox.addBefore(dockLeftBox);

		FlowBox dockRightBox = new FlowBox(FlowBox.Orientation.HORIZONTAL);
		windowDockRight = new Checkbox();
		dockRightBox.addBefore(new Label("Right dock"));
		dockRightBox.addAfter(windowDockRight);
		vbox.addBefore(dockRightBox);

		FlowBox frameSnapBox = new FlowBox(FlowBox.Orientation.HORIZONTAL);
		frameSnap = new Checkbox();
		frameSnapBox.addBefore(new Label("Magnetic windows"));
		frameSnapBox.addAfter(frameSnap);
		vbox.addBefore(frameSnapBox);

		FlowBox frameAnimBox = new FlowBox(FlowBox.Orientation.HORIZONTAL);
		frameAnims = new Checkbox();
		frameAnimBox.addBefore(new Label("Animations	"));
		frameAnimBox.addAfter(frameAnims);
		vbox.addBefore(frameAnimBox);

		FlowBox butBox = new FlowBox(FlowBox.Orientation.HORIZONTAL);
		Button applyButton = new Button("Apply");
		applyButton.setAction("apply");
		applyButton.setHandler(this);
		Button cancelButton = new Button("Cancel");
		cancelButton.setAction("cancel");
		cancelButton.setHandler(this);
		butBox.addAfter(applyButton);
		butBox.addAfter(cancelButton);

		vbox.addAfter(butBox);

		setContent(vbox);

		pack();
	}

	private void reset() {
		Config c = Config.get();

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
