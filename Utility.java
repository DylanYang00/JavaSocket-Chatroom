package version3.util;

import javax.swing.filechooser.FileSystemView;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;

public class Utility {
    public static void close(Closeable...targets){
        for (Closeable target:targets) {
            try {
                if (null!=target){
                    target.close();
                }
            } catch (IOException e) {
                System.err.println("Utility close()");
                e.printStackTrace();
            }
        }
    }
    public static File getDesktopAddress(){
        File homeDirectory = FileSystemView.getFileSystemView().getHomeDirectory();
        String path = homeDirectory.getAbsolutePath();
        File file = new File(path + File.separator + "clientMessage" + File.separator + "clients.txt");
        return file;
    }
}
