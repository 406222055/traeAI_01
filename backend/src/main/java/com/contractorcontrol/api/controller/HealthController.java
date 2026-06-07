package com.contractorcontrol.api.controller;

import com.contractorcontrol.api.util.ApiSerializers;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

  @GetMapping("/health")
  public Map<String, Object> health() {
    Map<String, Object> data = new LinkedHashMap<String, Object>();
    data.put("status", "ok");
    data.put("workspace", ApiSerializers.WORKSPACE_NAME);
    return data;
  }
}
