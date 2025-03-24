package ATM.State;

import ATM.ATMMachine;

public class ExitState extends ATMState{

  public void exit(ATMMachine atm) {
    System.out.println("Take your card");
    atm.setState(new IdleState());
  }
}
