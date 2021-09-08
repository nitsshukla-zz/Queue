package com.navi.app.model;

import lombok.Data;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.UniqueConstraint;

@Entity
@Data
public class QueuePayload {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  private QueueDAO topic;

  @OneToOne
  @JoinColumn(name = "`OFFSET`")
  private OffsetSequenceNumber offset;

  @Lob
  private String payload;

  @Column(unique = true)
  private String requestId;
}
