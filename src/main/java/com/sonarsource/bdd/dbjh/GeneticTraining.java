package com.sonarsource.bdd.dbjh;

import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class GeneticTraining {

  private final Population population;
  private final Fitness fitness;

  public GeneticTraining(Population population, Fitness fitness) {
    this.population = population;
    this.fitness = fitness;
  }

  public Individual bestIndividual() {
    return rankIndividuals().get(0);
  }

  public double bestIndividualScore() {
    return fitness.score(bestIndividual());
  }

  public void evolve(int steps) {
    for (int i = 0; i < steps; i++) {
      evovle();
    }
  }

  public void evovle() {
    List<Individual> oldRankedIndividuals = rankIndividuals();
    List<Individual> newIndividuals = Lists.newArrayList();

    for (int i = 0; i < oldRankedIndividuals.size() - 1; i++) {
      newIndividuals.add(Population.reproduce(oldRankedIndividuals.get(i), oldRankedIndividuals.get(i + 1)));
    }
    newIndividuals.add(Individual.newRandomIndividual());

    population.newGeneration(newIndividuals);
  }

  private final List<Individual> rankIndividuals() {
    List<Individual> individuals = Lists.newArrayList(population.individuals());

    Collections.sort(individuals,
      new Comparator<Individual>() {

        @Override
        public int compare(Individual o1, Individual o2) {
          double score1 = fitness.score(o1);
          double score2 = fitness.score(o2);

          if (score1 == score2) {
            return 0;
          }

          return score1 > score2 ? -1 : 1;
        }

      });

    return individuals;
  }

}
