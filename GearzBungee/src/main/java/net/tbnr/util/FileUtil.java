package net.tbnr.util;

import net.tbnr.gearz.GearzBungee;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

/**
 * Created by Jake on 1/23/14.
 *
 * Purpose Of File:
 *
 * Latest Change:
 */
public class FileUtil {

    /**
     * Downloads the file from the parameters
     *
     * @param link URL of the file to download
     * @param file Where to save the downloaded file
     * @return boolean of whether or not the download was successful
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
     * @return boolean of Whether or not the file was deleted
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

    /**
     * Writes an internal resource that
     * is saved with in the jar to a
     * local file.
     *
     * @param resourceName name of the resource to save
     * @param file         path to save too
     * @return whether or not the file was saved
     */
    public static boolean writeEmbeddedResourceToLocalFile(final String resourceName, final File file) {
        boolean result = false;

        final URL resourceUrl = FileUtil.class.getClassLoader().getResource(resourceName);
        final Logger logger = GearzBungee.getInstance().getLogger();

        // 1Kb buffer
        byte[] buffer = new byte[1024];
        int byteCount;

        InputStream inputStream = null;
        OutputStream outputStream = null;

        try {
            assert resourceUrl != null;
            inputStream = resourceUrl.openStream();
            outputStream = new FileOutputStream(file);

            while ((byteCount = inputStream.read(buffer)) >= 0) {
                outputStream.write(buffer, 0, byteCount);
            }

            // report success
            result = true;
        } catch (final IOException e) {
            logger.warning("Failure on saving the embedded resource " + resourceName + " to the file " + file.getAbsolutePath());
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (final IOException e) {
                    logger.warning("Problem closing an input stream while reading data from the embedded resource " + resourceName);
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.flush();
                    outputStream.close();
                } catch (final IOException e) {
                    logger.warning("Problem closing the output stream while writing the file " + file.getAbsolutePath());
                }
            }
        }
        return result;
    }
}
