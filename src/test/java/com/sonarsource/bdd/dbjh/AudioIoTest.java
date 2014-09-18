package com.sonarsource.bdd.dbjh;

import org.junit.Test;

public class AudioIoTest {

  @Test
  public void loadWav() throws Exception {
    AudioSignal wavFile = AudioIo.loadWavFile("a_jh_bruyant.wav");

    AudioIo.play(wavFile);
  }

}
