package net.tbnr.util;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Jake on 1/23/14.
 */
public class FileUtil {

    /**
     * Downloads the file from the parameters
     *
     * @param link URL of the file to download
     * @param file Where to save the downloaded file
     * @return     boolean of whether or not the download was successful
     */
    public static boolean downloadFile(String link, String file) {
        URL url;
        try {
            url = new URL(link);
        } catch (MalformedURLException e) {
            return false;
        }

        try {
            InputStream inStream = url.openStream();
            BufferedInputStream bufIn = new BufferedInputStream(inStream);

            File fileWrite = new File(file);
            OutputStream out = new FileOutputStream(fileWrite);
            BufferedOutputStream bufOut = new BufferedOutputStream(out);
            byte buffer[] = new byte[1024];
            while (true) {
                int nRead = bufIn.read(buffer, 0, buffer.length);
                if (nRead <= 0)
                    break;
                bufOut.write(buffer, 0, nRead);
            }

            bufOut.flush();
            out.close();
            inStream.close();
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    /**
     * Deletes the specified file
     *
     * @param dir File to delete
     * @return    boolean of Whether or not the file was deleted
     */
    public static boolean delete(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (String aChildren : children) {
                delete(new File(dir, aChildren));
            }
        }
        return dir.delete();
    }
}
