package com.contractorcontrol.api.controller;

import com.contractorcontrol.api.entity.ProjectEntity;
import com.contractorcontrol.api.repository.ProjectRepository;
import com.contractorcontrol.api.util.ApiConstants;
import com.contractorcontrol.api.util.ApiSerializers;
import com.contractorcontrol.api.util.ValidationUtils;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

  private final ProjectRepository projectRepository;

  public ProjectController(ProjectRepository projectRepository) {
    this.projectRepository = projectRepository;
  }

  @GetMapping
  public List<Map<String, Object>> list(
      @RequestParam(required = false) String keyword,
      @RequestParam(required = false) String status) {
    Specification<ProjectEntity> specification = Specification.where(null);
    if (status != null && !status.isEmpty()) {
      specification = specification.and((root, query, cb) -> cb.equal(root.get("status"), status));
    }
    if (keyword != null && !keyword.trim().isEmpty()) {
      String like = "%" + keyword.trim() + "%";
      specification = specification.and((root, query, cb) -> cb.or(
          cb.like(root.get("code"), like),
          cb.like(root.get("name"), like),
          cb.like(root.get("region"), like)));
    }
    return projectRepository.findAll(specification, Sort.by(Sort.Direction.DESC, "createdAt"))
        .stream()
        .map(ApiSerializers::serializeProject)
        .collect(Collectors.toList());
  }

  @GetMapping("/{id}")
  public Map<String, Object> detail(@PathVariable String id) {
    ProjectEntity project = projectRepository.findById(id).orElseThrow(() -> new NoSuchElementException("项目不存在"));
    return ApiSerializers.serializeProject(project);
  }

  @PostMapping
  @org.springframework.web.bind.annotation.ResponseStatus(HttpStatus.CREATED)
  public Map<String, Object> create(@RequestBody(required = false) Map<String, Object> body) {
    return ApiSerializers.serializeProject(saveProject(new ProjectEntity(), body, true));
  }

  @PutMapping("/{id}")
  public Map<String, Object> update(@PathVariable String id, @RequestBody(required = false) Map<String, Object> body) {
    ProjectEntity project = projectRepository.findById(id).orElseThrow(() -> new NoSuchElementException("项目不存在"));
    return ApiSerializers.serializeProject(saveProject(project, body, false));
  }

  private ProjectEntity saveProject(ProjectEntity project, Map<String, Object> body, boolean create) {
    Map<String, Object> payload = body == null ? java.util.Collections.<String, Object>emptyMap() : body;
    if (create) {
      project.setId(UUID.randomUUID().toString().replace("-", ""));
      project.setCreatedAt(Instant.now());
    }
    project.setCode(ValidationUtils.assertString(payload.get("code"), "code"));
    project.setName(ValidationUtils.assertString(payload.get("name"), "name"));
    project.setRegion(ValidationUtils.assertString(payload.get("region"), "region"));
    project.setManagerName(ValidationUtils.assertString(payload.get("managerName"), "managerName"));
    project.setStatus(ValidationUtils.assertEnum(payload.get("status"), ApiConstants.PROJECT_STATUSES, "status"));
    return projectRepository.save(project);
  }
}
