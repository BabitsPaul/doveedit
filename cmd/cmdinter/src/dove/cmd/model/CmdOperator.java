package dove.cmd.model;

/**
 * Interface for all operators used by the commandline
 * <p>
 * this include aswell +,-,etc. as for and while-loops
 * and if-statements
 */
public abstract class CmdOperator {
    public abstract void evaluate(String in);
}
