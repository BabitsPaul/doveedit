package dove;

import dove.component.img.Image;
import dove.document.DocumentContext;

public class Main {
    public static void main(String[] args) {
        GlobalFlags.processLauncherArgs(args);

        DocumentContext context = Setup.setup();

        context.frame.add(new Image("C:\\MyData\\Infos\\wailord_used_bodyslam.jpg", context));
    }
}