package io.toro.pairprogramming.services.storages;

import io.toro.pairprogramming.models.request.Content;
import io.toro.pairprogramming.models.Project;
import io.toro.pairprogramming.models.User;
import org.apache.commons.io.FileUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static io.toro.pairprogramming.models.request.Content.ContentType.DIRECTORY;
import static io.toro.pairprogramming.models.request.Content.ContentType.FILE;
import static java.nio.charset.StandardCharsets.UTF_8;

public class LocalProjectStorage implements ProjectStorageService {

    public static final String ROOT_DIRECTORY = System.getProperty("user.dir") + File.separator + "storage";

    private Path getUserDirectory(User user) {
        String path = String.join(
                File.separator,
                ROOT_DIRECTORY,
                user.getIds().toString()
        );

        return Paths.get(path);
    }

    public Path getProjectDirectory(Project project) {
        String userDir = getUserDirectory(project.getUser()).toString();

        String projectName = project.getName();

        String path = String.join(
                File.separator,
                userDir,
                projectName
        );

        return Paths.get(path);
    }

    private Path appendPathToProjectDirectory(Project project, String path) {
        String projectDir = getProjectDirectory(project).toString();

        String fullPath = String.join(
                File.separator,
                projectDir,
                path
        );

        return Paths.get(fullPath);
    }

    private String relativizePathToProjectDirectory(Project project, String path) {
        String projectDir = getProjectDirectory(project).toString();

        // 1.) remove actual filesystem project directory
        // 2.) remove redundant slashes
        return path.replace(projectDir, "/")
                    .replaceAll("^[/|\\\\]+", "/");
    }

    @Override
    public Content createFile(Project project, String path) {
        Content content = new Content();

        try {
            Path filePath = appendPathToProjectDirectory(project, path);

            Files.createFile(filePath);

            content = prepareContent(project, filePath, FILE);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return content;
    }

    @Override
    public Content moveFile(Project project, String oldPath, String newPath) {
        Content content = new Content();

        try {
            Path source = appendPathToProjectDirectory(project, oldPath);

            Path destination = appendPathToProjectDirectory(project, newPath);

            Files.move(source, destination);

            content = prepareContent(project, destination, FILE);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return content;
    }

    @Override
    public Content createDirectory(Project project, String path) {
        Content content = new Content();

        Path filePath = appendPathToProjectDirectory(project, path);

        try {
            Files.createDirectories(filePath);

            content = prepareContent(project, filePath, DIRECTORY);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return content;
    }

    @Override
    public Content moveDirectory(Project project, String oldPath, String newPath) {
        Content content = new Content();

        try {
            Path source = appendPathToProjectDirectory(project, oldPath);

            Path destination = appendPathToProjectDirectory(project, newPath);

            Files.move(source, destination);

            content = prepareContent(project, destination, DIRECTORY);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return content;
    }

    @Override
    public void deleteFile(Project project, String path) {
        try {
            Files.delete(
                    appendPathToProjectDirectory(project, path)
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteDirectory(Project project, String path) {
        try {
            FileUtils.deleteDirectory(
                    appendPathToProjectDirectory(project, path).toFile()
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void moveProject(Project oldProject, Project newProject) {

    }

    @Override
    public void deleteProject(Project project) {
        deleteDirectory(project, "/");
    }

    @Override
    public Content copyFile(Project project, String source, String destination) {
        Content content = new Content();

        try {
            Path sourcePath = appendPathToProjectDirectory(project, source);

            Path destinationPath = appendPathToProjectDirectory(project, destination);

            Files.copy(sourcePath, destinationPath);

            content = prepareContent(project, destinationPath, FILE);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return content;
    }

    @Override
    public Content copyDirectory(Project project, String source, String destination) {
        Content content = new Content();

        try {
            Path sourcePath = appendPathToProjectDirectory(project, source);

            Path destinationPath = appendPathToProjectDirectory(project, destination);

            Files.copy(sourcePath, destinationPath);

            content = prepareContent(project, destinationPath, DIRECTORY);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return content;
    }

    @Override
    public String readFile(Project project, String path) {
        try {
            Path filePath = appendPathToProjectDirectory(project, path);

            return new String(Files.readAllBytes(filePath), UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    @Override
    public List<Content> readDirectory(Project project, String path) {
        List<Content> contents = new ArrayList<>();

        Path filePath = appendPathToProjectDirectory(project, path);

        try {
            Files.list(filePath).forEach(file -> {
                Content content = prepareContent(
                        project,
                        file,
                        Files.isRegularFile(file) ? FILE : DIRECTORY
                );

                contents.add(content);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        return contents;
    }

    @Override
    public Content writeToFile(Project project, String path, String content) {
        Content contentObj = new Content();

        try {
            Path filePath = appendPathToProjectDirectory(project, path);

            Files.write(
                    filePath,
                    content.getBytes()
            );

            contentObj = prepareContent(project, filePath, FILE);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return contentObj;
    }

    @Override
    public Content uploadFile(MultipartFile multipartFile, Project project, String path) {
        Content content = new Content();

        try {
            Path filePath = appendPathToProjectDirectory(project, path);

            multipartFile.transferTo(filePath.toFile());

            content = prepareContent(project, filePath, FILE);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return content;
    }

    @Override
    public void exportFile(String directory) {

    }

    @Override
    public void compressDirectory() {

    }

    @Override
    public void decompressDirectory(String zipDirectory, String directory) throws IOException {
        byte[] buffer = new byte[1024];
        ZipInputStream zis = new ZipInputStream(new FileInputStream(zipDirectory));
        ZipEntry zipEntry = zis.getNextEntry();
        while(zipEntry != null){
            System.out.println(directory + zipEntry.getName());
            File file = new File(directory + zipEntry.getName());
            if(zipEntry.isDirectory()) {
                file.mkdir();
                zipEntry = zis.getNextEntry();
                continue;
            }
            try (FileOutputStream fileOuputStream = new FileOutputStream(file)) {
                fileOuputStream.write(zis.readAllBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
            zipEntry = zis.getNextEntry();
        }
        zis.closeEntry();
        zis.close();
    }

    @Override
    public boolean isFile(Project project, String path) {
        return Files.isRegularFile(
                appendPathToProjectDirectory(project, path)
        );
    }

    private Content prepareContent(Project project, Path path, Content.ContentType type) {
        Content content = new Content();

        content.setType(type);

        content.setName(path.getFileName().toString());

        content.setParent(
                relativizePathToProjectDirectory(project, path.getParent().toString())
        );

        return content;
    }
}
