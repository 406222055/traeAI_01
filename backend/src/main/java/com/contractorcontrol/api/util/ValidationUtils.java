package com.contractorcontrol.api.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;
import java.util.Collection;

public final class ValidationUtils {

  private ValidationUtils() {
  }

  public static String assertString(Object value, String fieldName) {
    if (!(value instanceof String) || ((String) value).trim().isEmpty()) {
      throw new IllegalArgumentException(fieldName + " is required");
    }
    return ((String) value).trim();
  }

  public static String assertOptionalString(Object value) {
    if (value == null || "".equals(value)) {
      return null;
    }
    if (!(value instanceof String)) {
      throw new IllegalArgumentException("Invalid string value");
    }
    return ((String) value).trim();
  }

  public static String assertEnum(Object value, Collection<String> values, String fieldName) {
    if (!(value instanceof String) || !values.contains(value)) {
      throw new IllegalArgumentException("Invalid " + fieldName);
    }
    return (String) value;
  }

  public static Instant assertDate(Object value, String fieldName) {
    if (!(value instanceof String) || ((String) value).trim().isEmpty()) {
      throw new IllegalArgumentException(fieldName + " is required");
    }

    String raw = ((String) value).trim();

    try {
      if (raw.contains("T")) {
        try {
          return Instant.parse(raw);
        } catch (DateTimeParseException ignored) {
          return OffsetDateTime.parse(raw).toInstant();
        }
      }
      return LocalDate.parse(raw).atStartOfDay().toInstant(ZoneOffset.UTC);
    } catch (DateTimeParseException ex) {
      throw new IllegalArgumentException("Invalid " + fieldName);
    }
  }
}
