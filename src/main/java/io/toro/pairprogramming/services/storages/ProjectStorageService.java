package io.toro.pairprogramming.services.storages;

import io.toro.pairprogramming.models.request.Content;
import io.toro.pairprogramming.models.Project;
import org.apache.commons.io.FileExistsException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.util.List;

@Service
public interface ProjectStorageService {

    Content createFile(Project project, String path);

    Content writeToFile(Project project, String path, String content);

    Content moveFile(Project project, String oldPath, String newPath) throws FileExistsException;

    Content copyFile(Project project, String source, String destination) throws FileExistsException;

    String readFile(Project project, String path);

    void deleteFile(Project project, String path) throws FileNotFoundException;

    Content createDirectory(Project project, String path);

    Content moveDirectory(Project project, String oldPath, String newPath);

    Content copyDirectory(Project project, String source, String destination);

    List<Content> readDirectory(Project project, String path);

    void deleteDirectory(Project project, String path);

    void moveProject(Project oldProject, Project newProject);

    void deleteProject(Project project);

    boolean isFile(Project project, String path);

    Content uploadFile(MultipartFile multipartFile, Project project, String path);

    void exportFile(String directory);

    void compressDirectory();

    void decompressDirectory(String zipDirectory, String directory) throws Exception;
}
