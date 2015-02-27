package dove.util.manifest;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.IOException;

public class JarSystemFileView
        extends FileSystemView
{
    @Override
    public boolean isRoot(File f) {
        return super.isRoot(f);
    }

    @Override
    public Boolean isTraversable(File f) {
        return super.isTraversable(f);
    }

    @Override
    public File createNewFolder(File containingDir) throws IOException {
        return null;
    }
}
