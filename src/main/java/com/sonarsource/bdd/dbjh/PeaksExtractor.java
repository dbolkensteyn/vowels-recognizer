package com.sonarsource.bdd.dbjh;

import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

/**
 * Naive algorithm to extract the peaks of a signal
 */
public class PeaksExtractor {

  private final int minimalDistanceBetweenPeaks;

  public PeaksExtractor(int minimalDistanceBetweenPeaks) {
    this.minimalDistanceBetweenPeaks = minimalDistanceBetweenPeaks;
  }

  public int[] peaks(final double[] values) {
    LinkedList<Integer> peaksBuilder = Lists.newLinkedList();

    int i = 0;
    double max = values[i];

    while (i < values.length - 1) {
      // Climb up hill
      while (i < values.length - 1 && values[i + 1] >= max) {
        i++;
        max = values[i];
      }

      // Found a new peak

      if (!peaksBuilder.isEmpty() && i - peaksBuilder.peek() < minimalDistanceBetweenPeaks) {
        // Peak is too close from another already found one
        if (values[peaksBuilder.peek()] > values[i]) {
          // Already found one was bigger
        } else {
          // New peak is bigger
          peaksBuilder.pop();
          peaksBuilder.push(i);
        }
      } else {
        // New peak
        peaksBuilder.push(i);
      }

      // Climb down hill
      while (i < values.length - 1 && values[i + 1] <= max) {
        i++;
        max = values[i];
      }
    }

    Collections.sort(peaksBuilder, new Comparator<Integer>() {

      @Override
      public int compare(Integer o1, Integer o2) {
        if (values[o1] == values[o2]) {
          return 0;
        }

        return values[o1] > values[o2] ? -1 : 1;
      }

    });

    int[] peaks = new int[peaksBuilder.size()];
    i = 0;
    for (Integer peak : peaksBuilder) {
      peaks[i] = peak;
      i++;
    }

    return peaks;
  }

}
