package com.sonarsource.bdd.dbjh;

import org.junit.Test;

public class FaderTest {

  @Test
  public void test() throws Exception {
    AudioSignal wavFile = AudioIo.loadWavFile("a_jh_bruyant.wav");

    Fader fader = new Fader();
    fader.fadeIn(wavFile.data, wavFile.samplingRate);
    fader.fadeOut(wavFile.data, wavFile.samplingRate);

    AudioIo.play(wavFile.data, wavFile.samplingRate);
  }

}
