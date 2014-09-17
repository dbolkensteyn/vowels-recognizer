package com.sonarsource.bdd.dbjh;

import com.google.common.base.Preconditions;
import com.sonarsource.bdd.dbjh.AudioIo.AudioSignal;
import org.jtransforms.fft.FloatFFT_1D;
import org.junit.Test;

public class FourierTest {

  @Test
  public void forward_fft() throws Exception {
    AudioSignal wavFile = AudioIo.loadWavFile("i_jh_bruyant.wav");
    System.out.println("Sampling: " + wavFile.samplingRate);
    System.out.println("Channels: " + wavFile.getChannels());

    float[] samples = wavFile.data[0];
    Preconditions.checkArgument(samples.length % 2 == 0, "Number of samples must be even");

    System.out.println("Length: " + samples.length);

    FloatFFT_1D fft = new FloatFFT_1D(samples.length);
    fft.realForward(samples);

    double max = Double.MIN_VALUE;
    int maxK = 0;

    for (int k = 0; k < samples.length / 2; k++) {
      float re = samples[2 * k];
      float im = samples[2 * k + 1];

      double magnitude = Math.sqrt(re * re + im * im);

      if (magnitude > max) {
        max = magnitude;
        maxK = k;
      }
    }

    System.out.println("Max = " + max + ", at k = " + maxK);
  }

}
