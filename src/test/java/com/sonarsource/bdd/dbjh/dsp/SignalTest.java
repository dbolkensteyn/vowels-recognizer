package com.sonarsource.bdd.dbjh.dsp;

import com.sonarsource.bdd.dbjh.AudioIo;
import com.sonarsource.bdd.dbjh.AudioSignal;
import com.sonarsource.bdd.dbjh.AudioSignalViewer;

public class SignalTest {

  public static void main(String[] args) throws Exception {
    
    String[][] tests = new String[][] {
      new String[] {"a", "760", "1450", "2353", "3166"},
      new String[] {"e", "350", "1350", "2250", "3170"},
      new String[] {"i", "250", "2250", "2980", "3280"},
      new String[] {"o", "360", "770", "2530", "3200"},
    };
    
    for (int i = 0; i < tests.length; i++) {
    
    AudioSignal wavFile = AudioIo.loadWavFile(tests[i][0] + "_jh_clean.wav");
    AudioSignal wavFileTruncated = Signal.truncate(wavFile);

    //AudioSignalViewer.show(wavFile);

    //Signal.coupe100(wavFileTruncated);
    //Signal.fadeInOut(wavFileTruncated);

    //AudioSignalViewer.show(wavFileTruncated);
    
    AudioIo.play(wavFile);
    
    int[] formants = Signal.formants(wavFileTruncated);
    System.out.println(tests[i][0]);
    System.out.println("F1 ("+tests[i][1]+"): " + formants[0]);
    System.out.println("F2 ("+tests[i][2]+"): " + formants[1]);
    System.out.println("F3 ("+tests[i][3]+"): " + formants[2]);
    System.out.println("F4 ("+tests[i][4]+"): " + formants[3]);
    }
  }
}
