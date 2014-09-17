package com.sonarsource.bdd.dbjh.dsp;

import com.sonarsource.bdd.dbjh.AudioSignalViewer;

import com.sonarsource.bdd.dbjh.swing.SignalPlot;
import com.sonarsource.bdd.dbjh.AudioIo;
import com.sonarsource.bdd.dbjh.AudioSignal;

public class SignalTest {

  public static void main(String[] args) throws Exception {
    AudioSignal wavFile = AudioIo.loadWavFile("a_jh_bruyant.wav");

    AudioSignal wavFileTruncated = Signal.truncate(wavFile);

    AudioSignalViewer.show(wavFileTruncated);
    AudioSignalViewer.show(wavFile);

    AudioIo.play(wavFileTruncated);
  }
}
