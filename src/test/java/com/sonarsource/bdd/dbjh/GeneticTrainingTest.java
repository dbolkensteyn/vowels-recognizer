package com.sonarsource.bdd.dbjh;

import com.google.common.base.Joiner;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import org.junit.Test;

import java.io.File;
import java.util.List;

public class GeneticTrainingTest {

  private static final int POPULATION_SIZE = 6;
  private static final int EVOLUTION_STEPS = 20;

  private final FormantExtractor formantExtractor = new FormantExtractor();

  @Test
  public void test() {
    train();
  }

  public GeneticTraining train() {
    try {
      Fitness fitness = loadFitness();
      GeneticTraining training = new GeneticTraining(new Population(POPULATION_SIZE), fitness);
      training.evolve(EVOLUTION_STEPS);

      System.out.println("Best individual scores: " + training.bestIndividualScore());

      return training;
    } catch (Exception e) {
      throw Throwables.propagate(e);
    }
  }

  private Fitness loadFitness() throws Exception {
    int[] i = loadTrainingFormants(new File("training/i"));
    int[] o = loadTrainingFormants(new File("training/o"));
    int[] a = loadTrainingFormants(new File("training/a"));

    System.out.println("i: " + Joiner.on(", ").join(toList(i)));
    System.out.println("o: " + Joiner.on(", ").join(toList(o)));
    System.out.println("a: " + Joiner.on(", ").join(toList(a)));

    return new Fitness(
      i,
      o,
      a);
  }

  private List<Integer> toList(int[] array) {
    List<Integer> result = Lists.newArrayList();
    for (Integer element : array) {
      result.add(element);
    }
    return result;
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
