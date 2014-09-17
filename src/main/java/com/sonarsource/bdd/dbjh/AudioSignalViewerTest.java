package com.sonarsource.bdd.dbjh;


public class AudioSignalViewerTest {
  
  public static void main(String[] args) throws Exception {
    AudioSignalViewer.show(AudioIo.loadWavFile("a_jh_bruyant.wav"));
  }

}
