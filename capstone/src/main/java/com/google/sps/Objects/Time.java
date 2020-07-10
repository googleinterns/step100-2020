package com.google.sps.Objects;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class Time {

  public long getDueDate() {
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime dueDate = now.plusDays(7).withHour(23).withMinute(59).withSecond(59).withNano(0);
    return Timestamp.valueOf(dueDate).getTime();
  }
}
