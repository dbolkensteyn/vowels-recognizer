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
    int i = mean(individual1.i(), individual2.i());
    int o = mean(individual1.o(), individual2.o());
    int a = mean(individual1.a(), individual2.a());

    // Mutation
    if (random.nextInt(100) <= MUTATION_RATE) {
      i = Individual.randomFormantFrequency();
    }
    if (random.nextInt(100) <= MUTATION_RATE) {
      o = Individual.randomFormantFrequency();
    }
    if (random.nextInt(100) <= MUTATION_RATE) {
      a = Individual.randomFormantFrequency();
    }

    return new Individual(i, o, a);
  }

  private static int mean(int f1, int f2) {
    return (f1 + f2) / 2;
  }

}
