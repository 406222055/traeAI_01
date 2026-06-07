package com.contractorcontrol.api.security;

import com.contractorcontrol.api.entity.UserEntity;
import com.contractorcontrol.api.repository.UserRepository;
import com.contractorcontrol.api.util.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private static final Set<String> PUBLIC_PATHS = new HashSet<String>(Arrays.asList("/health", "/api/auth/login", "/error"));

  private final JwtService jwtService;
  private final UserRepository userRepository;
  private final ObjectMapper objectMapper;

  public JwtAuthenticationFilter(JwtService jwtService, UserRepository userRepository, ObjectMapper objectMapper) {
    this.jwtService = jwtService;
    this.userRepository = userRepository;
    this.objectMapper = objectMapper;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    String path = request.getRequestURI();
    if ("OPTIONS".equalsIgnoreCase(request.getMethod()) || PUBLIC_PATHS.contains(path)) {
      filterChain.doFilter(request, response);
      return;
    }

    String header = request.getHeader(HttpHeaders.AUTHORIZATION);
    if (header == null || !header.startsWith("Bearer ")) {
      writeUnauthorized(response);
      return;
    }

    String token = header.substring(7);

    try {
      Claims claims = jwtService.parse(token);
      String userId = claims.getSubject();
      if (userId == null || userId.trim().isEmpty()) {
        writeUnauthorized(response);
        return;
      }

      Optional<UserEntity> userOptional = userRepository.findById(userId);
      if (!userOptional.isPresent()) {
        writeUnauthorized(response);
        return;
      }

      UserEntity user = userOptional.get();
      if (!"active".equals(user.getStatus())) {
        writeUnauthorized(response);
        return;
      }

      CurrentUser currentUser = new CurrentUser(user);
      UsernamePasswordAuthenticationToken authentication =
          new UsernamePasswordAuthenticationToken(currentUser, null, AuthorityUtils.NO_AUTHORITIES);
      SecurityContextHolder.getContext().setAuthentication(authentication);
      filterChain.doFilter(request, response);
    } catch (Exception ex) {
      writeUnauthorized(response);
    }
  }

  private void writeUnauthorized(HttpServletResponse response) throws IOException {
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setCharacterEncoding("UTF-8");
    objectMapper.writeValue(response.getWriter(), new ErrorResponse("未授权"));
  }
}
