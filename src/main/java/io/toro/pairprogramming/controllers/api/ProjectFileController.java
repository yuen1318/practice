package io.toro.pairprogramming.controllers.api;

import io.toro.pairprogramming.models.request.Content;
import io.toro.pairprogramming.models.request.MoveContent;
import io.toro.pairprogramming.models.Project;
import io.toro.pairprogramming.models.request.UploadContent;
import io.toro.pairprogramming.services.projects.ProjectManager;
import io.toro.pairprogramming.services.storages.ProjectStorageService;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.net.URLDecoder;
import java.util.Optional;

import static io.toro.pairprogramming.models.request.Content.ContentType.DIRECTORY;
import static io.toro.pairprogramming.models.request.Content.ContentType.FILE;
import static org.apache.commons.lang3.CharEncoding.UTF_8;

@RestController
@RequestMapping("api/v1/users")
public class ProjectFileController {

    private ProjectManager projectManager;

    private ProjectStorageService storageService;

    @Autowired
    public ProjectFileController(ProjectManager projectManager, ProjectStorageService storageService) {
        this.projectManager = projectManager;
        this.storageService = storageService;
    }

    @GetMapping("/{userId}/projects/{projectId}/files")
    public ResponseEntity getContents(@PathVariable Long projectId, HttpServletRequest request) throws Exception {
        String path = Optional.ofNullable(request.getParameter("path")).orElse("");

        if ( ! path.equals("") ) {
            path = URLDecoder.decode(path, UTF_8);
        }

        Project project = projectManager.getById(projectId);

        return ResponseEntity.ok(
                storageService.isFile(project, path)
                        ? storageService.readFile(project, path)
                        : storageService.readDirectory(project, path).stream().map(c -> {
                                if(c.getType() == DIRECTORY) {
                                    String moveContentUrl = request.getRequestURL().toString() + "/move";
                                    Link moveContentLink = new Link(moveContentUrl, "move");

                                    String copyContentUrl = request.getRequestURL().toString() + "/copy";
                                    Link copyContentLink = new Link(copyContentUrl, "copy");

                                    String uploadContentUrl = request.getRequestURL().toString() + "/upload";
                                    Link uploadContentLink = new Link(uploadContentUrl, "upload");

                                    c.add(moveContentLink);
                                    c.add(copyContentLink);
                                    c.add(uploadContentLink);
                                }
                                if(c.getType() == FILE) {
                                    String moveContentUrl = request.getRequestURL().toString() + "/move";
                                    Link moveContentLink = new Link(moveContentUrl, "move");

                                    String copyContentUrl = request.getRequestURL().toString() + "/copy";
                                    Link copyContentLink = new Link(copyContentUrl, "copy");

                                    c.add(moveContentLink);
                                    c.add(copyContentLink);
                                }
                                String selfUrl = request.getRequestURL().toString() + "?path=" + c.getParent() + c.getName();
                                Link selfLink = new Link(selfUrl, "self");

                                String editUrl = request.getRequestURL().toString() + "/files";
                                Link editLink = new Link(editUrl, "edit");
                                c.add(selfLink);
                                c.add(editLink);
                                return c;
                            }).collect(Collectors.toList())
        );
    }

    @PostMapping("/{userId}/projects/{projectId}/files")
    public ResponseEntity createContent(@PathVariable Long projectId, @RequestBody Content content, HttpServletRequest request) {
        Content created;

        Project project = projectManager.getById(projectId);

        String fullPath = String.join(
                File.separator, content.getParent(), content.getName()
        );

        if (content.getType() == DIRECTORY) {
            created = storageService.createDirectory(project, fullPath);
        }

        else if (content.getType() == FILE) {
            created = storageService.createFile(project, fullPath);
        }

        else {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body("Type should either be file or directory");
        }

        return ResponseEntity.ok(created);
    }

    @PutMapping("/{userId}/projects/{projectId}/files")
    public ResponseEntity writeToContent(@PathVariable Long projectId, @RequestBody String content, HttpServletRequest request) {
        String path = Optional.ofNullable(request.getParameter("path")).orElse("");

        Project project = projectManager.getById(projectId);

        return ResponseEntity.ok(storageService.writeToFile(project, path, content));
    }

    @DeleteMapping("/{userId}/projects/{projectId}/files")
    public ResponseEntity deleteContent(@PathVariable Long projectId, HttpServletRequest request) throws Exception {
        String path = Optional.ofNullable(request.getParameter("path")).orElse("");

        if (path.equals("") || path.equals("/")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Cannot deleteInvitation root directory");
        }

        Project project = projectManager.getById(projectId);

        if (storageService.isFile(project, path)) {
            storageService.deleteFile(project, path);
        } else {
            storageService.deleteDirectory(project, path);
        }

        return ResponseEntity.ok().build();
    }

    @PostMapping("/{userId}/projects/{projectId}/files/move")
    public ResponseEntity moveContent(@PathVariable Long projectId, @RequestBody MoveContent content) throws Exception {
        Content moved;

        Project project = projectManager.getById(projectId);

        String oldFullPath = String.join(
                File.separator, content.getOldParent(), content.getOldName()
        );

        String newFullPath = String.join(
                File.separator, content.getParent(), content.getName()
        );

        if (content.getType() == FILE) {
            moved = storageService.moveFile(project, oldFullPath, newFullPath);
        }

        else if (content.getType() == DIRECTORY) {
            moved = storageService.moveDirectory(project, oldFullPath, newFullPath);
        }

        else {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body("Type should either be file or directory");
        }

        return ResponseEntity.ok(moved);
    }

    @PostMapping("/{userId}/projects/{projectId}/files/copy")
    public ResponseEntity copyContent(@PathVariable Long projectId, @RequestBody MoveContent content) throws Exception {
        Content moved;

        Project project = projectManager.getById(projectId);

        String oldFullPath = String.join(
                File.separator, content.getOldParent(), content.getOldName()
        );

        String newFullPath = String.join(
                File.separator, content.getParent(), content.getName()
        );

        if (content.getType() == FILE) {
            moved = storageService.copyFile(project, oldFullPath, newFullPath);
        }

        else if (content.getType() == DIRECTORY) {
            moved = storageService.copyDirectory(project, oldFullPath, newFullPath);
        }

        else {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body("Type should either be file or directory");
        }

        return ResponseEntity.ok(moved);
    }

    @PostMapping("/{userId}/projects/{projectId}/files/upload")
    public ResponseEntity uploadFile(@PathVariable Long projectId, UploadContent content) throws Exception {
        Project project = projectManager.getById(projectId);

        String fullPath = String.join(
                File.separator, content.getParent(), content.getName()
        );

        Content uploaded = storageService.uploadFile(content.getFile(), project, fullPath);

        return ResponseEntity.ok(uploaded);
    }
}
