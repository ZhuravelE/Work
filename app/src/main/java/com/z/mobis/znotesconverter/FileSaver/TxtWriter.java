package com.z.mobis.znotesconverter.FileSaver;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by Евгений on 12.12.2016.
 */

public class TxtWriter implements FileSaver {
    OutputStream fileOutput = null;

    public TxtWriter(String fileName) throws FileNotFoundException {
        setWritableFile(fileName);
    }

    @Override
    public void setWritableFile(String fileName) throws FileNotFoundException {
        File file = new File(fileName + ".txt");
        fileOutput = new FileOutputStream(file);
    }

    @Override
    public void write(String name, String desc) throws IOException {
        fileOutput.write((name + "\n" + desc + "\n").getBytes());
    }

    @Override
    public void closeWritableFile() throws IOException {
        if (fileOutput != null) {
            fileOutput.flush();
            fileOutput.close();
        }
    }

    @Override
    public boolean isFileAvailable() {
        return fileOutput != null;
    }

    @Override
    public String getTab() {
        return "\t";
    }
}
