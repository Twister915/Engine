/*
 * Copyright (c) 2014.
 * CogzMC LLC USA
 * All Right reserved
 *
 * This software is the confidential and proprietary information of Cogz Development, LLC.
 * ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with Cogz LLC.
 */

package net.tbnr.util.io;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.plugin.Plugin;
import net.tbnr.gearz.GearzBungee;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
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
     * Downloads the {@link File} from the parameters
     *
     * @param link {@link java.net.URL} of the {@link File} to download in the form of a {@link String}
     * @param file Where to save the downloaded {@link File}
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
     * Deletes the specified {@link File}
     *
     * @param dir {@link File} to delete
     * @return boolean of Whether or not the {@link File} was deleted
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
     * local {@link File}.
     *
     * @param resourceName name of the resource to save
     * @param file         path to save too
     * @return whether or not the {@link File} was saved
     */
    public static boolean writeEmbeddedResourceToLocalFile(final String resourceName, final File file, Class clazz) {
        boolean result = false;

        final URL resourceUrl = clazz.getClassLoader().getResource(resourceName);
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

    public static List<String> getData(String file, Plugin plugin) {
        File f = new File(plugin.getDataFolder(), file);
        if (!(f.canRead() && f.exists())) try {
            boolean newFile = f.createNewFile();
            if (!newFile) return null;
            getData(file, plugin);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        BufferedReader stream;
        try {
            stream = new BufferedReader(new InputStreamReader(new BufferedInputStream(new FileInputStream(f))));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        List<String> lines = new ArrayList<>();
        String line;
        try {
            while ((line = stream.readLine()) != null) {
                lines.add(ChatColor.translateAlternateColorCodes('&', line));
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return lines;
    }
}
