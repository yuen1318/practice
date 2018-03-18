package io.toro.pairprogramming.models.request;

import lombok.Data;

@Data
public class FileRequest {
    private String type;
    private String path;
    private String fileName;
    private Long userId;
    private String oldFileName;
    private String newFileName;
    private String sourceFilePath;
    private String destinationFilePath;
    private String content;
}
