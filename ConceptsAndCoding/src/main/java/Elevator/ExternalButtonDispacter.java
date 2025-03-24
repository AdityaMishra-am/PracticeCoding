package Elevator;

import java.util.List;

public class ExternalButtonDispacter {
  List<ElevatorController> elevatorControllerList;
  GetElevatorStrategy getElevatorStrategy;
  public ExternalButtonDispacter(List<ElevatorController> elevatorControllerList, GetElevatorStrategy getElevatorStrategy) {
    this.elevatorControllerList = elevatorControllerList;
    this.getElevatorStrategy = getElevatorStrategy;
  }

  public ElevatorController getElevatorController(int currFloor, Direction direction) {
    return  elevatorControllerList.get(0);
  }
  public void submitReq( int currFloor, Direction direction) {
    ElevatorController elevatorController = getElevatorController(currFloor,direction);
    elevatorController.submitRequest(currFloor);
  }

}
