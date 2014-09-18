package com.sonarsource.bdd.dbjh;

import com.google.common.base.Preconditions;

public class Fader {

  public Fader() {
  }

  public void fadeIn(float[] samples, int n) {
    Preconditions.checkArgument(n < samples.length);

    for (int i = 0; i < n; i++) {
      float gain = i / (float) n;
      samples[i] = gain * samples[i];
    }
  }

  public void fadeOut(float[] samples, int n) {
    Preconditions.checkArgument(n < samples.length);

    for (int i = samples.length - n; i < samples.length; i++) {
      float gain = (samples.length - i) / (float) n;
      samples[i] = gain * samples[i];
    }
  }

}
