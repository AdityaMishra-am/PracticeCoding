package SnakeLadders;

public class Player {
  String name;
  int currentPos;

  public Player(String name, int currentPos) {
    this.name = name;
    this.currentPos = currentPos;
  }

  public int getCurrentPos() {
    return currentPos;
  }

  public void setCurrentPos(int currentPos) {
    this.currentPos = currentPos;
  }
}
