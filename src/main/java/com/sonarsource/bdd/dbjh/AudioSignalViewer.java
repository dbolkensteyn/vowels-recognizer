package com.sonarsource.bdd.dbjh;


import com.sonarsource.bdd.dbjh.AudioIo.AudioSignal;

import com.sonarsource.bdd.dbjh.swing.SignalPlot;

import java.awt.EventQueue;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

public class AudioSignalViewer extends JFrame {

  private AudioSignalViewer(AudioSignal audioSignal) {
    setLocationByPlatform(true);
    setSize(new Dimension(1200, 300));
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    SignalPlot signalPlot = new SignalPlot(audioSignal.data[0], -1, 1);
    signalPlot.setZoomModeHorizontal(true);
    setContentPane(signalPlot);
  }

  /**
   * Open a swing panel that display the signal.
   */
  public static void show(AudioSignal audioSignal) throws Exception {
    startGuiThread(audioSignal);
  }

  private static void startGuiThread(final AudioSignal audioSignal) {
    EventQueue.invokeLater(new Runnable() {
      @Override
      public void run() {
        guiThreadMain(audioSignal);
      }
    });
  }

  private static void guiThreadMain(AudioSignal audioSignal) {
    try {
      guiThreadInit(audioSignal);
    } catch (Throwable e) {
      System.err.print("Error: ");
      e.printStackTrace(System.err);
      JOptionPane.showMessageDialog(null, "Error: " + e, "Error", JOptionPane.ERROR_MESSAGE);
      System.exit(9);
    }
  }

  private static void guiThreadInit(final AudioSignal audioSignal) throws Exception {
    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    new AudioSignalViewer(audioSignal).setVisible(true);
  }

}
