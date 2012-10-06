package be.humanoids.ma;

import java.util.EventObject;
/**
 *
 * @author Martin
 */
public interface TransformedEventListener {
    public void handleTransformEvent(EventObject e,Tone[] freq);
}
