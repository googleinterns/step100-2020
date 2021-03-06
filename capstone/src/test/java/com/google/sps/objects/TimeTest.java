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
  private static long EXPECTED_DATE = 1595894399000L;

  @Before
  public void setUp() throws IOException {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void getDueDateTest() {
    LocalDateTime now = LocalDateTime.of(2020, Month.JULY, 20, 10, 30, 40);
    when(mockedTime.getNow()).thenReturn(now);

    assertEquals(EXPECTED_DATE, mockedTime.getDueDate());
  }
}
