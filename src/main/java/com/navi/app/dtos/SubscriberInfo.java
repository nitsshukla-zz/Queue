package com.navi.app.dtos;

import com.navi.app.model.CallbackInfo;
import com.navi.app.model.QueueDAO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotEmpty;

@Data @Builder @NoArgsConstructor @AllArgsConstructor @ToString
public class SubscriberInfo {
  private Long id;
  private String name;
  private Long offset;
  private String queueName;
  private CallbackDetails callbackDetails;
}
