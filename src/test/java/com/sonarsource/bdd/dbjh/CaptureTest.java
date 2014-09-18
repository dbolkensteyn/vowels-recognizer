package com.sonarsource.bdd.dbjh;

import org.junit.Test;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

import java.io.ByteArrayOutputStream;

public class CaptureTest {

  @Test
  public void test() throws Exception {

    AudioFormat format = new AudioFormat(44100, 16, 1, true, false);

    TargetDataLine line;
    DataLine.Info info = new DataLine.Info(TargetDataLine.class,
      format); // format is an AudioFormat object
    if (!AudioSystem.isLineSupported(info)) {
      // Handle the error ...
      throw new UnsupportedOperationException();
    }
    // Obtain and open the line.
    try {
      line = (TargetDataLine) AudioSystem.getLine(info);
      line.open(format);
    } catch (LineUnavailableException ex) {
      // Handle the error ...
      throw new UnsupportedOperationException();
    }

    // Assume that the TargetDataLine, line, has already
    // been obtained and opened.
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    int numBytesRead;
    byte[] data = new byte[line.getBufferSize() / 5];

    // Begin audio capture.
    line.start();

    // Here, stopped is a global boolean set by another thread.
    FormantExtractor formantExtractor = new FormantExtractor();

    for (int second = 0; second < 20; second++) {
      while (out.size() < 0.5 * format.getSampleRate() * (format.getSampleSizeInBits() / 8) * format.getChannels()) {
        // Read the next chunk of data from the TargetDataLine.
        numBytesRead = line.read(data, 0, data.length);
        // Save this chunk of data.
        out.write(data, 0, numBytesRead);
      }

      byte[] byteArray = out.toByteArray();
      float[] samples = new float[byteArray.length / 2];
      float maxValue = (1 << format.getSampleSizeInBits() - 1) - 1;
      for (int i = 0, j = 0; i < byteArray.length; i = i + 2, j++) {
        samples[j] = unpackSignedInt(byteArray, i, format.getSampleSizeInBits(), format.isBigEndian()) / maxValue;
      }

      out.reset();

      System.out.println("formant = " + formantExtractor.formant(samples));
    }
  }

  // TODO Duplicated from AudioIo
  private static int unpackSignedInt(byte[] buf, int pos, int bits, boolean bigEndian) {
    switch (bits) {
      case 16:
        if (bigEndian) {
          return buf[pos] << 8 | buf[pos + 1] & 0xFF;
        }
        else {
          return buf[pos + 1] << 8 | buf[pos] & 0xFF;
        }
      case 24:
        if (bigEndian) {
          return buf[pos] << 16 | (buf[pos + 1] & 0xFF) << 8 | buf[pos + 2] & 0xFF;
        }
        else {
          return buf[pos + 2] << 16 | (buf[pos + 1] & 0xFF) << 8 | buf[pos] & 0xFF;
        }
      default:
        throw new AssertionError();
    }
  }

}
