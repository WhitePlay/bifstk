package bifstk.wm.ui;

import bifstk.wm.Drawable;

public interface Widget extends Drawable {

	public void setWidth(int w);

	public void setHeight(int h);

	public void setBounds(int w, int h);

	public int getWidth();

	public int getHeight();

}
