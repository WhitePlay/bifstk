package bifstk.wm.geom;

/**
 * Regions that compose a frame: borders, title and content
 * <p>
 * 
 * <pre>
 *  1_________2_________3
 *  |_________4_________|
 *  |                   |
 *  5         6         7    11
 *  |                   |
 *  8_________9________10
 * </pre>
 * 
 * <ul>
 * <li>1: top left border
 * <li>2: top border
 * <li>3: top right border
 * <li>4: title
 * <li>5: left border
 * <li>6: content
 * <li>7: right border
 * <li>8: bottom left border
 * <li>9: bottom border
 * <li>10: bottom right border
 * <li>11: outside of the frame
 * </ul>
 */
public enum Region {

	TITLE, CONTENT, LEFT, TOP, RIGHT, BOT, TOP_LEFT, TOP_RIGHT, BOT_LEFT, BOT_RIGHT, OUT;

}
