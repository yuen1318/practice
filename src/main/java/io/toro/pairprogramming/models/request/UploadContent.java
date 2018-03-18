package io.toro.pairprogramming.models.request;

import io.toro.pairprogramming.models.request.Content;
import org.springframework.web.multipart.MultipartFile;

public class UploadContent extends Content {

    private MultipartFile file;

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }
}
