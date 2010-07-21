package bifstk;

import java.util.Deque;

import bifstk.type.Drawable;


public interface State {

	public Deque<Drawable> getFrames();

}
