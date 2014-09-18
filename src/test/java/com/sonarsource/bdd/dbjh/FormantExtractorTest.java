package com.sonarsource.bdd.dbjh;

import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

public class FormantExtractorTest {

  private final FormantExtractor extractor = new FormantExtractor();

  @Test
  public void i() throws Exception {
    AudioSignal wavFile = AudioIo.loadWavFile("i_jh_clean.wav");
    assertThat(extractor.formant(wavFile.data)).isEqualTo(209);
  }

  @Test
  public void o() throws Exception {
    AudioSignal wavFile = AudioIo.loadWavFile("o_jh_clean.wav");
    assertThat(extractor.formant(wavFile.data)).isEqualTo(364);
  }

  @Test
  public void a() throws Exception {
    AudioSignal wavFile = AudioIo.loadWavFile("a_jh_clean.wav");
    assertThat(extractor.formant(wavFile.data)).isEqualTo(718);

    wavFile = AudioIo.loadWavFile("a_jh_bruyant.wav");
    assertThat(extractor.formant(wavFile.data)).isEqualTo(744);
  }

  @Test
  public void la_440hz() throws Exception {
    AudioSignal wavFile = AudioIo.loadWavFile("la_440hz.wav");
    assertThat(extractor.formant(wavFile.data)).isEqualTo(440);
  }

}
