package pers.kinson.wechat.ui.controller;

import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.util.Args;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;

public class ProgressFileEntity extends FileEntity {

    private ProgressMonitor monitor;

    public ProgressFileEntity(ProgressMonitor monitor, File file, ContentType contentType) {
        super(file, contentType);
        this.monitor = monitor;
    }

    public void writeTo(OutputStream outStream) throws IOException {
        Args.notNull(outStream, "Output stream");

        try (InputStream inStream = Files.newInputStream(this.file.toPath())) {
            byte[] tmp = new byte[4096];
            int l;
            while ((l = inStream.read(tmp)) != -1) {
                monitor.updateTransferred(l);
                outStream.write(tmp, 0, l);
            }
            outStream.flush();
        }
    }

}
