package Elevator;

import java.util.List;

public class InternalButtonDispatcher {
  List<ElevatorController> elevatorControllerList;

  public InternalButtonDispatcher(List<ElevatorController> elevatorControllerList) {
    this.elevatorControllerList = elevatorControllerList;
  }
  void submitInternalRequest(int id, int floor) {
    ElevatorController elevatorController = elevatorControllerList.get(id);
    elevatorController.submitRequest(floor);

  }
}
