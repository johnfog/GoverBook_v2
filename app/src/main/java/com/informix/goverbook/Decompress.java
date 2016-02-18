package com.informix.goverbook;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 *
 * @author jon
 */
public class Decompress {

    public void unzip(String _zipFile, String _targetLocation) {


        // create target location folder if not exist
        dirChecker(_targetLocation);

        try {
            FileInputStream fin = new FileInputStream(_zipFile);
            ZipInputStream zin = new ZipInputStream(fin);
            ZipEntry ze;
            while ((ze = zin.getNextEntry()) != null) {

                // create dir if required while unzipping
                if (ze.isDirectory()) {
                    dirChecker(ze.getName());
                } else {
                    FileOutputStream fout = new FileOutputStream(
                            _targetLocation + "/" + ze.getName());
                    BufferedOutputStream bufout = new BufferedOutputStream(fout);
                    byte[] buffer = new byte[1024];
                    int read;
                    while ((read = zin.read(buffer)) != -1) {
                        bufout.write(buffer, 0, read);
                    }


                    zin.closeEntry();
                    bufout.close();
                    fout.close();
                }
            }
            zin.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void dirChecker(String dir) {
        File f = new File(dir);
        if (!f.isDirectory()) {
            f.mkdirs();
        }
    }
}