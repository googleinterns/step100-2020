package com.google.sps.objects;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.Month;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import com.google.sps.Objects.Time;

public class TimeTest {

  @Spy private Time mockedTime;

  @Before
  public void setUp() throws IOException {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void getDueDateTest() {
    LocalDateTime now = LocalDateTime.of(2020, Month.JULY, 10, 10, 30, 40);
    when(mockedTime.getNow()).thenReturn(now);
    long actualDueDate = 1595044799000L;

    assertEquals(actualDueDate, mockedTime.getDueDate());
  }
}
