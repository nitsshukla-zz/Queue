package com.navi.app.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
@Data
public class Subscriber {
  @Id
  private long id;
  private String name;
  @Column(name = "`OFFSET`")
  private Long offset;
  @ManyToOne
  private QueueDAO topic;
}
