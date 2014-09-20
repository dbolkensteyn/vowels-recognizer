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

  private final int i;
  private final int o;
  private final int a;

  public Individual(int i, int o, int a) {
    validateFrequency(i);
    validateFrequency(o);
    validateFrequency(a);

    this.i = i;
    this.o = o;
    this.a = a;
  }

  public static Individual newRandomIndividual() {
    int i = randomFormantFrequency();
    int o = randomFormantFrequency();
    int a = randomFormantFrequency();

    return new Individual(i, o, a);
  }

  public static int randomFormantFrequency() {
    return random(Individual.MIN_FORMANT_FREQ, Individual.MAX_FORMANT_FREQ);
  }

  private static int random(int low, int high) {
    Preconditions.checkArgument(high - low > 0, "low = " + low + ", high = " + high);
    return RANDOM.nextInt(high - low) + low;
  }

  private final void validateFrequency(int f) {
    Preconditions.checkArgument(f >= MIN_FORMANT_FREQ && f < MAX_FORMANT_FREQ, "Invalid frequency: " + f);
  }

  public int i() {
    return i;
  }

  public int o() {
    return o;
  }

  public int a() {
    return a;
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
