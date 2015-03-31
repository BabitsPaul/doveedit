package dove.cmd.api;

public class CommandError {
    public String error;

    public CommandError() {
        this("");
    }

    public CommandError(String error) {
        this.error = error;
    }
}
