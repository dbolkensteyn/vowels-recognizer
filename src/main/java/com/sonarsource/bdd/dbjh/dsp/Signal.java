package com.sonarsource.bdd.dbjh.dsp;

import com.sonarsource.bdd.dbjh.AudioSignalViewer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jtransforms.fft.FloatFFT_1D;
import com.sonarsource.bdd.dbjh.AudioSignal;

public class Signal {

  /**
   * Keep 
   * @param input
   * @return
   */
  public static AudioSignal truncate(AudioSignal input) {
    int duration = 100;
    if (input.getLength() * 1000 / input.samplingRate < duration) {
      throw new IllegalStateException("Input should be at last " + duration + "  ms");
    }
    int keep = (duration * input.samplingRate / 1000) & 0xFFFFFFFE;
    float[] result = new float[keep];
    int middle = input.getLength() / 2;
    System.arraycopy(input.data, middle - (keep / 2), result, 0, keep);
    return new AudioSignal(result, input.samplingRate);
  }

  public static void fadeInOut(AudioSignal input) {
    int size = input.getLength();
    int fadeSize = size * 5 / 100;
    for (int i = 0; i < fadeSize; i++) {
      input.data[i] = input.data[i] * (((float) i + 1) / fadeSize);
      input.data[size - i - 1] = input.data[size - i - 1] * (((float) i + 1) / fadeSize);
    }
  }

  public static void coupe100(AudioSignal input) {
    int fs = input.samplingRate;
    int N = input.getLength();
    float[] Y = Arrays.copyOf(input.data, 2 * N);
    FloatFFT_1D fft = new FloatFFT_1D(N);
    fft.realForwardFull(Y);

    int threshold = 100 * 2;
    Arrays.fill(Y, 0, Math.round(threshold * (float) N / fs), 0);
    Arrays.fill(Y, Math.round(2 * N - threshold * (float) N / fs), 2 * N - 1, 0);

    fft.complexInverse(Y, true);

    for (int i = 0; i < N; i++) {
      input.data[i] = Y[2 * i];
    }
  }

  public static float[] accentue(float[] input) {
    int alpha = 2;
    float[] result = new float[input.length];
    result[0] = input[0];
    for (int i = 1; i < input.length; i++) {
      result[i] = input[i] - alpha * input[i - 1];
    }
    return result;
  }

  public static List<float[]> deconvol(float[] signal, int fs, int fenetre, int n0) {
    int nbfram = signal.length;

    int frameparfenetre = Math.round(fs * (float) fenetre / 1000);
    int recoupframe = Math.round(frameparfenetre / 2);
    float[] hamming = new float[frameparfenetre];
    for (int i = 0; i < frameparfenetre; i++) {
      hamming[i] = (float) (0.54 - 0.46 * Math.cos(2 * Math.PI * i / (frameparfenetre - 1)));
    }
    int fftsize = (int) Math.pow(2, Math.ceil(log2(frameparfenetre)));

    List<float[]> result = new ArrayList<>();

    int end = nbfram - frameparfenetre;
    for (int i = 0; i < end; i += frameparfenetre - recoupframe) {
      int to = Math.min(i + frameparfenetre - 1, end);
      float[] sfen = new float[to - i + 1];
      System.arraycopy(signal, i, sfen, 0, sfen.length);
      sfen = accentue(sfen);
      for (int j = 0; j < sfen.length; j++) {
        sfen[j] = sfen[j] * hamming[j];
      }

      FloatFFT_1D fft = new FloatFFT_1D(sfen.length);
      fft.realForward(sfen);
      for (int j = 0; j < sfen.length; j++) {
        sfen[j] = (float) Math.log(Math.abs(sfen[j]));
      }
      fft.realInverse(sfen, true);
      Arrays.fill(sfen, n0 + 1, sfen.length - 1, 0);
      fft.realForward(sfen);
      for (int j = 0; j < sfen.length; j++) {
        sfen[j] = (float) Math.abs(Math.exp(sfen[j]));
      }
      result.add(Arrays.copyOfRange(sfen, 2, Math.round(fftsize / 2) - 1));
    }

    return result;
  }

  public static double log2(double num) {
    return (Math.log(num) / Math.log(2));
  }

  public static int[] formants(AudioSignal input) {

    int[] formants = new int[4];

    float[] phon = new float[input.getLength() / 3];
    int fs = input.samplingRate / 3;

    for (int i = 0; i < phon.length; i++) {
      phon[i] = input.data[i * 3];
    }

    int fmax = fs / 2;

    int TFenetre = 23;

    int quefrence = (int) Math.floor(1000 * ((float) phon.length) / fs);

    List<float[]> deconvols = deconvol(phon, fs, TFenetre, quefrence);
    int Nf = deconvols.get(0).length;
    float[] magnitude = new float[Nf];
    for (int i = 0; i < magnitude.length; i++) {
      double sum = 0;
      for (float[] deconvol : deconvols) {
        sum += Math.abs(deconvol[i]);
      }
      magnitude[i] = (float) (sum / deconvols.size());
    }

    int[][] B = new int[][] {new int[] {235, 775}, new int[] {675, 2265}, new int[] {2145, 2995}, new int[] {3045, 3495}};

    for (int i = 0; i < 4; i++) {
      for (int j = 0; j < 2; j++) {
        B[i][j] = Math.min(Math.round(B[i][j] * Nf / fmax), Nf);
      }
    }

    int i = 0;
    while (i < 4) {
      int b1 = B[i][0];
      int b2 = B[i][1];
      float max = 0;
      int maxIdx = 0;
      for (int idx = b1; idx <= b2; idx++) {
        if (magnitude[idx] > max) {
          max = magnitude[idx];
          maxIdx = idx;
        }
      }
      formants[i] = Math.round(maxIdx * fmax / Nf);
      if (i == 1 && (formants[1] - formants[0]) <= 410) { // distance minimale entre F1 et F2
        if (formants[1] <= 763) {
          // ce sont ceux qui posent le plus problème
          formants[0] = formants[1];
        }
        B[1][0] = b1 + 1;
        i = 0;
      }
      i++;
    }

    return formants;

  }

}
