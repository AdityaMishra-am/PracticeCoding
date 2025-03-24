package SnakeLadders;

import java.util.concurrent.ThreadLocalRandom;

public class Dice {
int numberOfDice;

  public Dice(int numberOfDice) {
    this.numberOfDice = numberOfDice;
  }
  int roll() {
    return ThreadLocalRandom.current().nextInt(1,this.numberOfDice*6);
  }
}
