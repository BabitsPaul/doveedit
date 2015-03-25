package dove.config;

import java.util.EventObject;

public class ConfigChangedEvent
        extends EventObject {
    private String key;

    public ConfigChangedEvent(Configuration src, String key) {
        super(src);

        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public Configuration getSource() {
        return (Configuration) super.getSource();
    }
}
