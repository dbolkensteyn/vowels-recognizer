package com.sonarsource.bdd.dbjh;

import com.google.common.base.Preconditions;
import org.jtransforms.fft.FloatFFT_1D;
import org.junit.Test;

public class FourierTest {

  @Test
  public void forward_fft() throws Exception {
    AudioSignal wavFile = AudioIo.loadWavFile("i_jh_clean.wav");
    System.out.println("Sampling: " + wavFile.samplingRate);

    float[] samples = wavFile.data;
    Preconditions.checkArgument(samples.length % 2 == 0, "Number of samples must be even");

    Fader fader = new Fader();
    fader.fadeIn(samples, samples.length / 3);
    fader.fadeOut(samples, samples.length / 3);

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

    double[] magnitudes = new double[samples.length / 2];
    for (int freq = 0; freq < magnitudes.length; freq++) {
      float re = samples[2 * freq];
      float im = samples[2 * freq + 1];

      double magnitude = Math.sqrt(re * re + im * im);

      magnitudes[freq] = magnitude;
    }

    PeaksExtractor e = new PeaksExtractor(150);
    System.out.println("Peaks:");
    for (int peak : e.peaks(magnitudes)) {
      if (peak >= 200 && peak <= 3000) {
        System.out.println("  - " + peak + ", val = " + magnitudes[peak]);
      }
    }
  }
}
