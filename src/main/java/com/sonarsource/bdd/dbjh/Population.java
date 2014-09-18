package com.sonarsource.bdd.dbjh;

import com.google.common.base.Preconditions;

import java.util.List;
import java.util.Random;

public class Population {

  private static final Random random = new Random();
  private static final int MUTATION_RATE = 2;

  private final Individual[] individuals;

  public Population(int n) {
    Preconditions.checkArgument(n > 2, "population too small");

    individuals = new Individual[n];
    for (int i = 0; i < n; i++) {
      individuals[i] = Individual.newRandomIndividual();
    }
  }

  public Individual[] individuals() {
    return individuals;
  }

  public void newGeneration(List<Individual> newIndividuals) {
    Preconditions.checkArgument(individuals.length == newIndividuals.size());

    for (int i = 0; i < individuals.length; i++) {
      individuals[i] = newIndividuals.get(i);
    }
  }

  public static Individual reproduce(Individual individual1, Individual individual2) {
    // Cross-over
    int i1 = mean(individual1.i1(), individual2.i1());
    int i2 = mean(individual1.i2(), individual2.i2());

    int o1 = mean(individual1.o1(), individual2.o1());
    int o2 = mean(individual1.o2(), individual2.o2());

    int a1 = mean(individual1.a1(), individual2.a1());
    int a2 = mean(individual1.a2(), individual2.a2());

    // Mutation
    if (random.nextInt(100) <= MUTATION_RATE) {
      i1 = Individual.randomFormantFrequency();
      if (i2 <= i1) {
        i2 = Individual.randomFormantFrequency(i1);
      }
    }
    if (random.nextInt(100) <= MUTATION_RATE) {
      i2 = Individual.randomFormantFrequency(i1);
    }

    if (random.nextInt(100) <= MUTATION_RATE) {
      o1 = Individual.randomFormantFrequency();
      if (o2 <= o1) {
        o2 = Individual.randomFormantFrequency(o1);
      }
    }
    if (random.nextInt(100) <= MUTATION_RATE) {
      o2 = Individual.randomFormantFrequency(o1);
    }

    if (random.nextInt(100) <= MUTATION_RATE) {
      a1 = Individual.randomFormantFrequency();
      if (a2 <= a1) {
        a2 = Individual.randomFormantFrequency(a1);
      }
    }
    if (random.nextInt(100) <= MUTATION_RATE) {
      a2 = Individual.randomFormantFrequency(a1);
    }

    return new Individual(i1, i2, o1, o2, a1, a2);
  }

  private static int mean(int f1, int f2) {
    return (f1 + f2) / 2;
  }

}
