package com.z.mobis.znotesconverter.FileSaver;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by Евгений on 12.12.2016.
 */

public class HtmlWriter implements FileSaver {
    String name;
    OutputStream fileOutput = null;

    public HtmlWriter(String fileName) throws IOException {
        setWritableFile(fileName);
    }

    @Override
    public void setWritableFile(String fileName) throws IOException {
        File file = new File(fileName + ".htm");
        name = file.getAbsolutePath();
        fileOutput = new FileOutputStream(file);
        String startTags = "<html>\n<head>\n<meta charset=\"utf-8\">\n";
        startTags = startTags + "<style>\n" +
                "p {\n" +
                "margin-top: 0.5em;\n" +    /* Отступ сверху */
                "margin-bottom: 0.5em;\n" +   /* Отступ снизу */
                "}\n" +
                "</style>\n";
        startTags = startTags + "</head>\n";
        fileOutput.write(startTags.getBytes());
    }

    @Override
    public void write(String name, String desc) throws IOException {
        name = "<p><strong>" + name + "</strong></p>" + "\n";
        desc = "<p>" + desc + "</p>" + "\n";

        String string = name + desc;
        fileOutput.write(string.getBytes());
    }

    @Override
    public void closeWritableFile() throws IOException {
        if (fileOutput != null) {
            fileOutput.write("</html>".getBytes());
            fileOutput.flush();
            fileOutput.close();
        }
        File file = new File(name);
        boolean b = file.exists();
        file.exists();
    }

    @Override
    public boolean isFileAvailable() {
        return fileOutput != null;
    }

    @Override
    public String getTab() {
        return "&emsp;";
    }
}
