package io.toro.pairprogramming.unit;

import io.toro.pairprogramming.integration.utils.AuthUtils;
import io.toro.pairprogramming.integration.utils.ProjectUtils;
import io.toro.pairprogramming.models.request.Content;
import io.toro.pairprogramming.models.Project;
import io.toro.pairprogramming.models.User;
import io.toro.pairprogramming.services.storages.LocalProjectStorage;
import io.toro.pairprogramming.services.storages.ProjectStorageService;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static io.toro.pairprogramming.models.request.Content.ContentType.DIRECTORY;
import static io.toro.pairprogramming.models.request.Content.ContentType.FILE;
import static io.toro.pairprogramming.services.storages.LocalProjectStorage.ROOT_DIRECTORY;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;
import static org.unitils.reflectionassert.ReflectionComparatorMode.LENIENT_ORDER;

public class LocalProjectStorageTest extends ProjectStorageTest {

    private Project project;

    private Path projectDir;

    private ProjectStorageService storageService = new LocalProjectStorage();

    @Before
    public void setUp() throws IOException {
        User user = AuthUtils.createUser();
        user.setId(1L);

        project = ProjectUtils.createProject();
        project.setId(1L);
        project.setUser(user);

        String stringPath = String.join(
                File.separator,
                ROOT_DIRECTORY,
                project.getUser().getIds().toString(),
                project.getName()
        );

        projectDir = Paths.get(stringPath);

        Files.createDirectories(projectDir);
    }

    @After
    public void tearDown() throws Exception {
        File storageDir = Paths.get(ROOT_DIRECTORY).toFile();
        FileUtils.cleanDirectory(storageDir);
    }

    @Override
    public void shouldWriteToFile() throws Exception {
        // Given
        String content = "sample file content";
        String path = "/sample.txt";

        // When
        Content fileContent = storageService.writeToFile(project, path, content);

        // Then
        Content expected = new Content();
        expected.setParent("/");
        expected.setName("sample.txt");
        expected.setType(FILE);

        Path expectedPath = projectDir.resolve("sample.txt");

        assertTrue(Files.exists(expectedPath));
        assertThat(fileContent, samePropertyValuesAs(expected));
        assertEquals(content, new String(Files.readAllBytes(expectedPath)));
    }

    @Override
    public void shouldMoveFile() throws Exception {
        // Given
        Files.createFile(projectDir.resolve("old.txt"));

        // When
        Content newFile = storageService.moveFile(project, "/old.txt", "/new.txt");

        // Then
        Content expected = new Content();
        expected.setName("new.txt");
        expected.setParent("/");
        expected.setType(FILE);

        Path expectedPath = projectDir.resolve("new.txt");

        assertTrue(Files.exists(expectedPath));
        assertThat(newFile, samePropertyValuesAs(expected));
    }

    @Override
    public void shouldCopyFile() throws Exception {
        // Given
        Path file = Files.createTempFile(projectDir, null, null);

        // When
        String oldPath = "/" + file.getFileName();
        Content content = storageService.copyFile(project, oldPath, "/copy");

        // Then
        Content expected = new Content();
        expected.setName("copy");
        expected.setParent("/");
        expected.setType(FILE);

        assertTrue(Files.exists(projectDir.resolve("copy")));
        assertThat(content, samePropertyValuesAs(expected));
    }

    @Override
    public void shouldReadFile() throws Exception {
        // Given
        String content = "sample file content";
        Files.write(projectDir.resolve("test.txt"), content.getBytes());

        // When
        String actual = storageService.readFile(project, "/test.txt");

        // Then
        assertEquals(content, actual);
    }

    @Override
    public void shouldDeleteFile() throws Exception {
        // Given
        Path file = Files.createTempFile(projectDir, null, null);

        // When
        storageService.deleteFile(project, "/" + file.getFileName());

        // Then
        assertFalse(Files.exists(file));
    }

    @Override
    public void shouldCreateDirectory() throws Exception {
        // Given
        String path = "/assets";

        // When
        Content content = storageService.createDirectory(project, path);

        // Then
        Content expected = fileToContent(projectDir.resolve("assets").toFile());

        assertTrue(Files.exists(projectDir.resolve("assets")));
        assertThat(content, samePropertyValuesAs(expected));
    }

    @Override
    public void shouldMoveDirectory() throws Exception {
        // Given
        Files.createDirectory(projectDir.resolve("assets"));

        // When
        Content content = storageService.moveDirectory(project, "/assets", "/public");

        // Then
        Content expected = new Content();
        expected.setName("public");
        expected.setParent("/");
        expected.setType(DIRECTORY);

        assertTrue(Files.exists(projectDir.resolve("public")));
        assertThat(content, samePropertyValuesAs(expected));
    }

    @Override
    public void shouldCopyDirectory() throws Exception {
        // Given
        Files.createDirectory(projectDir.resolve("assets"));

        // When
        Content content = storageService.copyDirectory(project, "/assets", "/public");

        // Then
        Content expected = new Content();
        expected.setName("public");
        expected.setParent("/");
        expected.setType(DIRECTORY);

        assertTrue(Files.exists(projectDir.resolve("public")));
        assertThat(content, samePropertyValuesAs(expected));
    }

    @Override
    public void shouldReadDirectory() throws Exception {
        // Given
        File folder = Files.createTempDirectory(projectDir, null).toFile();
        File file = Files.createTempFile(projectDir, null, null).toFile();

        // When
        List<Content> contents = storageService.readDirectory(project, "/");

        // Then
        List<Content> expected = new ArrayList<>();

        expected.add(fileToContent(folder));
        expected.add(fileToContent(file));

        assertEquals(contents.size(), 2);
        assertReflectionEquals(expected, contents, LENIENT_ORDER);
    }

    @Override
    public void shouldDeleteDirectory() throws Exception {
        // Given
        Path directory = Files.createDirectory(projectDir.resolve("assets"));
        Files.createTempFile(directory, null, null);

        // When
        storageService.deleteDirectory(project, "/assets");

        // Then
        assertFalse(Files.exists(projectDir.resolve("assets")));
    }

    private Content fileToContent(File file) {
        Content content = new Content();

        content.setParent("/");
        content.setName(file.getName());
        content.setType(file.isFile() ? FILE : DIRECTORY);

        return content;
    }
}
