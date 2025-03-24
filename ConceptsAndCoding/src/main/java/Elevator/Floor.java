package Elevator;

public class Floor {
  int floorNumber;
  ExternalButtonDispacter externalButtonDispacter;

  public Floor(int floorNumber) {
    this.floorNumber = floorNumber;
  }
  int getFloorNumber() {
    return this.floorNumber;
  }
  public void pressButton(Direction direction) {

    externalButtonDispacter.submitReq(floorNumber, direction);
  }
}
