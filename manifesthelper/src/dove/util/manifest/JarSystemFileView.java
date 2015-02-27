package dove.util.manifest;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.IOException;

/**
 * Created by Babits on 27/02/2015.
 */
public class JarSystemFileView
        extends FileSystemView {


    @Override
    public File createNewFolder(File containingDir) throws IOException {
        return null;
    }
}
