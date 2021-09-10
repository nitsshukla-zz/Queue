package com.navi.app.model;

import lombok.Data;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Data
@Table(
    indexes = {
      @Index(name = "offset_lookup_index", columnList = "`OFFSET`")
    })
public class QueuePayload {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  private QueueDAO queue;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "`OFFSET`")
  private OffsetSequenceNumber offset = new OffsetSequenceNumber();

  @Lob
  private String payload;

  @Column(unique = true)
  private String requestId;
}
