package com.sonarsource.bdd.dbjh;

import com.google.common.base.Preconditions;

import java.util.Random;

/**
 * An individual of the genetic algorithm capable of classifying 'i', 'o', 'a' vowels based on their first formant
 */
public class Individual {

  private static final int MIN_FORMANT_FREQ = 0;
  private static final int MAX_FORMANT_FREQ = 1000;

  private static final Random RANDOM = new Random();

  private final int i1;
  private final int i2;
  private final int o1;
  private final int o2;
  private final int a1;
  private final int a2;

  public Individual(
    int i1, int i2,
    int o1, int o2,
    int a1, int a2) {

    validateArguments(i1, i2);
    validateArguments(o1, o2);
    validateArguments(a1, a2);

    this.i1 = i1;
    this.i2 = i2;
    this.o1 = o1;
    this.o2 = o2;
    this.a1 = a1;
    this.a2 = a2;
  }

  public static Individual newRandomIndividual() {
    int i1 = randomFormantFrequency();
    int i2 = randomFormantFrequency(i1);

    int o1 = randomFormantFrequency();
    int o2 = randomFormantFrequency(o1);

    int a1 = randomFormantFrequency();
    int a2 = randomFormantFrequency(a1);

    return new Individual(i1, i2, o1, o2, a1, a2);
  }

  public static int randomFormantFrequency() {
    return random(Individual.MIN_FORMANT_FREQ, Individual.MAX_FORMANT_FREQ - 1);
  }

  public static int randomFormantFrequency(int low) {
    return random(low + 1, Individual.MAX_FORMANT_FREQ);
  }

  private static int random(int low, int high) {
    Preconditions.checkArgument(high - low > 0, "low = " + low + ", high = " + high);
    return RANDOM.nextInt(high - low) + low;
  }

  private final void validateArguments(int f1, int f2) {
    validateFrequency(f1);
    validateFrequency(f2);

    Preconditions.checkArgument(f1 < f2, "Invalid range: " + f1 + " - " + f2);
  }

  private final void validateFrequency(int f) {
    Preconditions.checkArgument(f >= MIN_FORMANT_FREQ && f < MAX_FORMANT_FREQ, "Invalid frequency: " + f);
  }

  public int i1() {
    return i1;
  }

  public int i2() {
    return i2;
  }

  public int o1() {
    return o1;
  }

  public int o2() {
    return o2;
  }

  public int a1() {
    return a1;
  }

  public int a2() {
    return a2;
  }

  private int i() {
    return mean(i1, i2);
  }

  private int o() {
    return mean(o1, o2);
  }

  private int a() {
    return mean(a1, a2);
  }

  private static int mean(int f1, int f2) {
    return (f1 + f2) / 2;
  }

  public Vowel classify(int f) {
    int i = Math.abs(f - i());
    int o = Math.abs(f - o());
    int a = Math.abs(f - a());

    if (i < o && i < a) {
      return Vowel.I;
    } else if (o < i && o < a) {
      return Vowel.O;
    } else if (a < i && a < o) {
      return Vowel.A;
    } else {
      return Vowel.OTHER;
    }
  }

}
