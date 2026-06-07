package com.contractorcontrol.api.util;

import java.time.Instant;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class InstantLongConverter implements AttributeConverter<Instant, Long> {

  @Override
  public Long convertToDatabaseColumn(Instant attribute) {
    return attribute == null ? null : attribute.toEpochMilli();
  }

  @Override
  public Instant convertToEntityAttribute(Long dbData) {
    return dbData == null ? null : Instant.ofEpochMilli(dbData);
  }
}
