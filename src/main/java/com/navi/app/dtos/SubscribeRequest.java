package com.navi.app.dtos;

import com.navi.app.model.CallbackInfo;
import com.navi.app.model.QueueDAO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data @Builder @NoArgsConstructor @AllArgsConstructor @ToString
public class SubscribeRequest {
  @NotEmpty
  private String name;
  @NotEmpty
  private String queueName;
  @Valid @NotNull
  private CallbackDetails callbackDetails;
}
