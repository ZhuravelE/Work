package com.z.mobis.znotesconverter.FileSaver;

import java.io.IOException;

/**
 * Created by Евгений on 12.12.2016.
 */

public interface FileSaver {
    void setWritableFile(String fileName) throws IOException;
    void write(String name, String desc) throws IOException;
    void closeWritableFile() throws IOException;
    boolean isFileAvailable();
    String getTab();
}
