package dove.util.manifest;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.IOException;

public class JarSystemFileView
        extends FileSystemView {


    @Override
    public File createNewFolder(File containingDir) throws IOException {
        return null;
    }
}
