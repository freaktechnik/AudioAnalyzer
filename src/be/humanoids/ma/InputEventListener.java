package be.humanoids.ma;

import java.util.EventObject;
/**
 *
 * @author Martin
 */
public interface InputEventListener {
    public void handleInputEvent(EventObject e,float[] by);
}
