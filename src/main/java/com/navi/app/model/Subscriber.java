package com.navi.app.model;

import lombok.Data;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

@Entity
@Data @ToString
public class Subscriber {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(nullable = false, unique = true)
  private String name;
  @Column(name = "`OFFSET`", columnDefinition = "bigint default 0")
  private Long offset;
  @ManyToOne
  private QueueDAO queue;
  @OneToOne(cascade = CascadeType.ALL)
  private CallbackInfo callbackInfo;
}
