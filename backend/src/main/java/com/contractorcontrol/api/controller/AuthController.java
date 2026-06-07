package com.contractorcontrol.api.controller;

import com.contractorcontrol.api.entity.UserEntity;
import com.contractorcontrol.api.repository.UserRepository;
import com.contractorcontrol.api.security.CurrentUser;
import com.contractorcontrol.api.security.JwtService;
import com.contractorcontrol.api.security.SecurityUtils;
import com.contractorcontrol.api.util.ApiSerializers;
import com.contractorcontrol.api.util.ErrorResponse;
import com.contractorcontrol.api.util.ValidationUtils;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final UserRepository userRepository;
  private final SecurityUtils securityUtils;
  private final JwtService jwtService;

  public AuthController(UserRepository userRepository, SecurityUtils securityUtils, JwtService jwtService) {
    this.userRepository = userRepository;
    this.securityUtils = securityUtils;
    this.jwtService = jwtService;
  }

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody(required = false) Map<String, Object> body) {
    try {
      Map<String, Object> payload = body == null ? new LinkedHashMap<String, Object>() : body;
      String username = ValidationUtils.assertString(payload.get("username"), "username");
      String password = ValidationUtils.assertString(payload.get("password"), "password");

      Optional<UserEntity> userOptional = userRepository.findByUsername(username);
      if (!userOptional.isPresent()) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("用户名或密码错误"));
      }

      UserEntity user = userOptional.get();
      if (!"active".equals(user.getStatus()) || !securityUtils.verifyPassword(password, user.getPasswordHash())) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("用户名或密码错误"));
      }

      Map<String, Object> response = new LinkedHashMap<String, Object>();
      response.put("token", jwtService.createToken(user));
      response.put("user", ApiSerializers.serializeUser(user));
      return ResponseEntity.ok(response);
    } catch (IllegalArgumentException ex) {
      return ResponseEntity.badRequest().body(new ErrorResponse(ex.getMessage()));
    }
  }

  @GetMapping("/me")
  public Map<String, Object> me(Authentication authentication) {
    CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();
    Map<String, Object> response = new LinkedHashMap<String, Object>();
    response.put("user", ApiSerializers.serializeUser(currentUser.getUser()));
    return response;
  }
}
