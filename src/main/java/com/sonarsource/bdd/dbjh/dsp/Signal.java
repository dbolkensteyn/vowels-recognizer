package com.sonarsource.bdd.dbjh.dsp;

import com.sonarsource.bdd.dbjh.AudioSignal;

public class Signal {

  /**
   * Keep 
   * @param input
   * @return
   */
  public static AudioSignal truncate(AudioSignal input) {
    int duration = 1000;
    if (input.getLength() * 1000 / input.samplingRate < duration) {
      throw new IllegalStateException("Input should be at last " + duration + "  ms");
    }
    int keep = (duration * input.samplingRate / 1000) >> 2;
    float[] result = new float[keep];
    int middle = input.getLength() / 2;
    System.arraycopy(input.data, middle - (keep / 2), result, 0, keep);
    return new AudioSignal(result, input.samplingRate);
  }

  public static AudioSignal coupe100(AudioSignal input) {
    return input;
  }

}
