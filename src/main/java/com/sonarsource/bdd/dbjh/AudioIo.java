// Copyright 2013 Christian d'Heureuse, Inventec Informatik AG, Zurich, Switzerland
// www.source-code.biz, www.inventec.ch/chdh
//
// This module is multi-licensed and may be used under the terms
// of any of the following licenses:
//
//  EPL, Eclipse Public License, V1.0 or later, http://www.eclipse.org/legal
//  LGPL, GNU Lesser General Public License, V2.1 or later, http://www.gnu.org/licenses/lgpl.html
//
// Please contact the author if you need another license.
// This module is provided "as is", without warranties of any kind.

package com.sonarsource.bdd.dbjh;

import java.io.InputStream;
import java.io.IOException;
import java.io.File;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;

/**
* Audio i/o utilities.
*/
public class AudioIo {

  // ------------------------------------------------------------------------------

  

  // ------------------------------------------------------------------------------

  private AudioIo() {
  }


  /**
  * Loads an audio signal from a WAV file.
  */
  public static AudioSignal loadWavFile(String fileName) throws Exception {
    AudioSignal signal = new AudioSignal();
    AudioInputStream stream = AudioSystem.getAudioInputStream(new File(fileName));
    AudioFormat format = stream.getFormat();
    signal.samplingRate = Math.round(format.getSampleRate());
    int frameSize = format.getFrameSize();
    int channels = format.getChannels();
    if (channels > 1) {
      throw new IllegalArgumentException("Unable to load a wav file with more than 1 channel");
    }
    long totalFramesLong = stream.getFrameLength();
    if (totalFramesLong > Integer.MAX_VALUE) {
      throw new Exception("Sound file too long.");
    }
    int totalFrames = (int) totalFramesLong;
    signal.data = new float[totalFrames];
    final int blockFrames = 0x4000;
    byte[] blockBuf = new byte[frameSize * blockFrames];
    int pos = 0;
    while (pos < totalFrames) {
      int reqFrames = Math.min(totalFrames - pos, blockFrames);
      int trBytes = stream.read(blockBuf, 0, reqFrames * frameSize);
      if (trBytes % frameSize != 0) {
        throw new AssertionError();
      }
      int trFrames = trBytes / frameSize;
      unpackAudioStreamBytes(format, blockBuf, 0, signal.data, pos, trFrames);
      pos += trFrames;
    }
    return signal;
  }

  /**
  * Plays an audio signal on the default system audio output device.
  */
  public static void play(AudioSignal signal) throws Exception {
    int channels = 1;
    AudioFormat format = new AudioFormat(signal.samplingRate, 16, channels, true, false);
    int frameSize = format.getFrameSize();
    SourceDataLine line = AudioSystem.getSourceDataLine(format);
    line.open(format, signal.samplingRate * frameSize); // 1 second buffer
    line.start();
    final int blockFrames = 0x4000;
    byte[] blockBuf = new byte[frameSize * blockFrames];
    int pos = 0;
    while (pos < signal.getLength()) {
      int frames = Math.min(signal.getLength() - pos, blockFrames);
      packAudioStreamBytes(format, signal.data, pos, blockBuf, 0, frames);
      int bytes = frames * frameSize;
      int trBytes = line.write(blockBuf, 0, bytes);
      if (trBytes != bytes) {
        throw new AssertionError();
      }
      pos += frames;
    }
    line.drain();
    line.stop();
    line.close();
  }

  /**
  * Plays an audio signal on the default system audio output device.
  */
  public static void play(float[] buf, int samplingRate) throws Exception {
    AudioSignal signal = new AudioSignal();
    signal.data = buf;
    signal.samplingRate = samplingRate;
    play(signal);
  }

  /**
  * A utility routine to unpack the data of a Java Sound audio stream.
  */
  public static void unpackAudioStreamBytes(AudioFormat format, byte[] inBuf, int inPos, float[] outBufs, int outPos, int frames) {
    int channels = 1;
    boolean bigEndian = format.isBigEndian();
    int sampleBits = format.getSampleSizeInBits();
    int frameSize = format.getFrameSize();
    if (format.getEncoding() != AudioFormat.Encoding.PCM_SIGNED) {
      throw new UnsupportedOperationException("Audio stream format not supported (not signed PCM).");
    }
    if (sampleBits != 16 && sampleBits != 24) {
      throw new UnsupportedOperationException("Audio stream format not supported (" + sampleBits + " bits per sample).");
    }
    int sampleSize = (sampleBits + 7) / 8;
    if (sampleSize * channels != frameSize) {
      throw new AssertionError();
    }
    float maxValue = (float) ((1 << (sampleBits - 1)) - 1);
    for (int channel = 0; channel < channels; channel++) {
      float[] outBuf = outBufs;
      int p0 = inPos + channel * sampleSize;
      for (int i = 0; i < frames; i++) {
        int v = unpackSignedInt(inBuf, p0 + i * frameSize, sampleBits, bigEndian);
        outBuf[outPos + i] = v / maxValue;
      }
    }
  }

  /**
  * A utility routine to pack the data for a Java Sound audio stream.
  */
  public static void packAudioStreamBytes(AudioFormat format, float[] inBufs, int inPos, byte[] outBuf, int outPos, int frames) {
    int channels = 1;
    boolean bigEndian = format.isBigEndian();
    int sampleBits = format.getSampleSizeInBits();
    int frameSize = format.getFrameSize();
    if (format.getEncoding() != AudioFormat.Encoding.PCM_SIGNED) {
      throw new UnsupportedOperationException("Audio stream format not supported (not signed PCM).");
    }
    if (sampleBits != 16 && sampleBits != 24) {
      throw new UnsupportedOperationException("Audio stream format not supported (" + sampleBits + " bits per sample).");
    }
    int sampleSize = (sampleBits + 7) / 8;
    if (sampleSize * channels != frameSize) {
      throw new AssertionError();
    }
    int maxValue = (1 << (sampleBits - 1)) - 1;
    for (int channel = 0; channel < channels; channel++) {
      float[] inBuf = inBufs;
      int p0 = outPos + channel * sampleSize;
      for (int i = 0; i < frames; i++) {
        float clipped = Math.max(-1, Math.min(1, inBuf[inPos + i]));
        int v = Math.round(clipped * maxValue);
        packSignedInt(v, outBuf, p0 + i * frameSize, sampleBits, bigEndian);
      }
    }
  }

  private static int unpackSignedInt(byte[] buf, int pos, int bits, boolean bigEndian) {
    switch (bits) {
      case 16:
        if (bigEndian) {
          return (buf[pos] << 8) | (buf[pos + 1] & 0xFF);
        }
        else {
          return (buf[pos + 1] << 8) | (buf[pos] & 0xFF);
        }
      case 24:
        if (bigEndian) {
          return (buf[pos] << 16) | ((buf[pos + 1] & 0xFF) << 8) | (buf[pos + 2] & 0xFF);
        }
        else {
          return (buf[pos + 2] << 16) | ((buf[pos + 1] & 0xFF) << 8) | (buf[pos] & 0xFF);
        }
      default:
        throw new AssertionError();
    }
  }

  private static void packSignedInt(int i, byte[] buf, int pos, int bits, boolean bigEndian) {
    switch (bits) {
      case 16:
        if (bigEndian) {
          buf[pos] = (byte) ((i >>> 8) & 0xFF);
          buf[pos + 1] = (byte) (i & 0xFF);
        }
        else {
          buf[pos] = (byte) (i & 0xFF);
          buf[pos + 1] = (byte) ((i >>> 8) & 0xFF);
        }
        break;
      case 24:
        if (bigEndian) {
          buf[pos] = (byte) ((i >>> 16) & 0xFF);
          buf[pos + 1] = (byte) ((i >>> 8) & 0xFF);
          buf[pos + 2] = (byte) (i & 0xFF);
        }
        else {
          buf[pos] = (byte) (i & 0xFF);
          buf[pos + 1] = (byte) ((i >>> 8) & 0xFF);
          buf[pos + 2] = (byte) ((i >>> 16) & 0xFF);
        }
        break;
      default:
        throw new AssertionError();
    }
  }

}
