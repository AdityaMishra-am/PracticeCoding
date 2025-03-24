package Elevator;

public class ElevatorCar {
 Display display;
 InternalButton internalButtons;
 Status status;
 Direction direction;
 Doors doors;
 Floor currfloor;
 int id;

  public ElevatorCar(int id, Display display, InternalButton internalButtons, Doors doors, Floor currfloor) {
    this.id = id;
    this.display = display;
    this.internalButtons = internalButtons;
    this.status = Status.IDLE;
    this.direction = null;
    this.doors = doors;
    this.currfloor = currfloor;
  }

  public InternalButton getInternalButtons() {
    return internalButtons;
  }

  public void setInternalButtons(InternalButton internalButtons) {
    this.internalButtons = internalButtons;
  }

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public Direction getDirection() {
    return direction;
  }

  public void setDirection(Direction direction) {
    this.direction = direction;
  }

  public Doors getDoors() {
    return doors;
  }

  public void setDoors(Doors doors) {
    this.doors = doors;
  }

  public Floor getCurrfloor() {
    return currfloor;
  }

  public void setCurrfloor(Floor currfloor) {
    this.currfloor = currfloor;
  }

  public  void move(int destFloor) {
    this.status = Status.MOVING;
    if (destFloor > currfloor.getFloorNumber()) {
      this.direction = Direction.UP;

    } else if (destFloor < currfloor.getFloorNumber()) {
      this.direction = Direction.DOWN;
    } else {
      this.doors.openDoor();
    }
  }
}
