package com.z.mobis.znotesconverter.FileSaver;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by Евгений on 18.12.2016.
 */

public class DbzWriter {
    OutputStream fileOutput = null;

    public DbzWriter(String fileName) throws FileNotFoundException {
        setWritableFile(fileName);
    }

    public void setWritableFile(String fileName) throws FileNotFoundException {

    }

    public void write(String name, String desc) throws IOException {

    }

    public void closeWritableFile() throws IOException {

    }

    public boolean isFileAvailable() {
        return false;
    }

    public String getTab() {
        return null;
    }
}
