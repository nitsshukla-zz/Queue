package com.navi.app.model;

import com.vladmihalcea.hibernate.type.json.JsonType;
import lombok.Data;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.springframework.http.HttpMethod;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.HashMap;
import java.util.Map;

@Entity
@Data
@TypeDef(name = "json", typeClass = JsonType.class)
public class CallbackInfo {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;
  @Enumerated(EnumType.STRING)
  private HttpMethod method;
  private String endpoint;
  @Type(type = "json")
  @Column(columnDefinition = "json")
  private Map<String, String> headers = new HashMap<>();
}
