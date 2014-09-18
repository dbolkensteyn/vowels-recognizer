package com.sonarsource.bdd.dbjh;

import org.junit.Test;

import java.io.File;

public class GeneticTrainingTest {

  private static final int POPULATION_SIZE = 3;
  private static final int EVOLUTION_STEPS = 0;

  private final FormantExtractor formantExtractor = new FormantExtractor();

  @Test
  public void test() throws Exception {
    Fitness fitness = loadFitness();
    GeneticTraining training = new GeneticTraining(new Population(POPULATION_SIZE), fitness);
    training.evolve(EVOLUTION_STEPS);

    System.out.println("Best individual scores: " + training.bestIndividualScore());
  }

  private Fitness loadFitness() throws Exception {
    return new Fitness(
      loadTrainingFormants(new File("training/i")),
      loadTrainingFormants(new File("training/o")),
      loadTrainingFormants(new File("training/a")));
  }

  private int[] loadTrainingFormants(File folder) throws Exception {
    File[] files = folder.listFiles();
    int[] result = new int[files.length];
    int i = 0;
    for (File file : files) {
      AudioSignal wavFile = AudioIo.loadWavFile(file.getAbsolutePath());
      result[i] = formantExtractor.formant(wavFile.data);
      i++;
    }
    return result;
  }

}
