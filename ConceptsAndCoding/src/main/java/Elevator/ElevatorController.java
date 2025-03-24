package Elevator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

public class ElevatorController {
  ElevatorCar elevatorCar;
  PriorityQueue<Integer> minPQ = new PriorityQueue<>();
  PriorityQueue<Integer> maxPQ = new PriorityQueue<>(Collections.reverseOrder());
  List<Integer> pendingCalls = new ArrayList<>();

  void submitRequest(int floor){

  }
}
