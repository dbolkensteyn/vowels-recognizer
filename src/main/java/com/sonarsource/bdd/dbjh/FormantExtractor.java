package com.sonarsource.bdd.dbjh;

import com.google.common.base.Preconditions;
import org.jtransforms.fft.FloatFFT_1D;

public class FormantExtractor {

  private final Fader fader = new Fader();
  private final PeaksExtractor peaksExtractor = new PeaksExtractor(150);

  /**
   * Returns the first formant
   */
  public int formant(float[] originalSamples) {
    Preconditions.checkArgument(originalSamples.length > 0 && originalSamples.length % 2 == 0);

    float[] samples = new float[originalSamples.length];
    System.arraycopy(originalSamples, 0, samples, 0, originalSamples.length);

    fader.fadeIn(samples, samples.length / 4);
    fader.fadeOut(samples, samples.length / 4);

    double[] magnitudes = magnitudes(samples);
    int[] peaks = peaksExtractor.peaks(magnitudes);

    return peaks.length > 0 ? (int) (peaks[0] / (originalSamples.length / 44100.0)) : -1;
  }

  private static double[] magnitudes(float[] samples) {
    FloatFFT_1D fft = new FloatFFT_1D(samples.length);
    fft.realForward(samples);

    double[] magnitudes = new double[samples.length / 2];
    for (int freq = 0; freq < magnitudes.length; freq++) {
      float re = samples[2 * freq];
      float im = samples[2 * freq + 1];

      double magnitude = Math.sqrt(re * re + im * im);

      magnitudes[freq] = magnitude;
    }

    return magnitudes;
  }

}
