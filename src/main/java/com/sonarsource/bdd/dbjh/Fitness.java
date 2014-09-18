package com.sonarsource.bdd.dbjh;

/**
 * Rank individuals based on their performance
 */
public class Fitness {

  private final int[] i;
  private final int[] o;
  private final int[] a;

  /**
   *
   * @param i Array of first formant frequencies for 'i'
   * @param o Array of first formant frequencies for 'o'
   * @param a Array of first formant frequencies for 'a'
   */
  public Fitness(int[] i, int[] o, int[] a) {
    this.i = i;
    this.o = o;
    this.a = a;
  }

  public double score(Individual individual) {
    int successes = 0;
    int total = a.length + i.length + o.length;

    successes += successes(individual, i, Vowel.I);
    successes += successes(individual, o, Vowel.O);
    successes += successes(individual, a, Vowel.A);

    return successes / (double) total;
  }

  private int successes(Individual individual, int[] formantFrequencies, Vowel expectedVowel) {
    int successes = 0;
    for (int formantFrequency : formantFrequencies) {
      if (individual.classify(formantFrequency) == expectedVowel) {
        successes++;
      }
    }
    return successes;
  }

}
