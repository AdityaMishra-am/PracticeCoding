package ATM.State;

import ATM.ATMMachine;
import ATM.Card;

public class IdleState extends ATMState{
  @Override
  public void insertCard(Card card, ATMMachine atmMachine) {
    atmMachine.setState(new HasCard());
  }
}
