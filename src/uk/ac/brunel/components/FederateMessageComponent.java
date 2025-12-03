package uk.ac.brunel.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

public class FederateMessageComponent implements Component, Poolable {
    public String sender = "";
    public String receiver = "";
    public String type = "";
    public String content = "";

    @Override
    public void reset() {
        sender = "";
        receiver = "";
        type = "";
        content = "";
    }
}
