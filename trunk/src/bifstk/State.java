package bifstk;

import java.util.Deque;

import bifstk.wm.Drawable;


public interface State {

	public Deque<Drawable> getFrames();

}
