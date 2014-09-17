package com.sonarsource.bdd.dbjh;

/**
* A class for storing an audio signal in memory.
*/
public class AudioSignal {
  
  /**
  * The sampling rate in Hz
  */
  public int samplingRate;

  /**
  * The audio signal sample values, per channel separately.
  * The normal value range is -1 .. 1.
  */
  public float[] data;

  public AudioSignal() {
  }
  
  public AudioSignal(float[] data, int samplingRate) {
    this.data = data;
    this.samplingRate = samplingRate;
  }

  /**
  * Returns the signal length in samples.
  */
  public int getLength() {
    return data.length;
  }
  
}