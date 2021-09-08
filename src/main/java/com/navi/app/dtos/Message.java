package com.navi.app.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data @Builder @NoArgsConstructor @AllArgsConstructor @ToString
public class Message {
  private Long id;
  @NotEmpty
  private String message;
  @NotEmpty
  private String requestId;
}
