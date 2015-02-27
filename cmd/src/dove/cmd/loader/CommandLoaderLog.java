package dove.cmd.loader;

import java.util.ArrayList;
import java.util.List;

public class CommandLoaderLog {
    private List<CommandLoaderLogElement> log = new ArrayList<>();

    private boolean generalSuccess = true;

    private Exception failedCause = null;

    public void add(CommandLoaderLogElement e) {
        log.add(e);
    }

    public List<CommandLoaderLogElement> getLog() {
        return log;
    }

    public boolean generalSuccess() {
        return generalSuccess;
    }

    public void setGeneralSuccess(boolean generalSuccess) {
        this.generalSuccess = generalSuccess;
    }

    public Exception getFailedCause() {
        return failedCause;
    }

    public void setFailedCause(Exception failedCause) {
        this.failedCause = failedCause;
    }
}