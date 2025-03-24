package ATM.State;

import ATM.ATMMachine;
import ATM.Card;

public class HasCard extends ATMState{
  @Override
  public void auth(ATMMachine atm, Card card, int pin) {
    if (card.authenticate(pin)) {
      atm.setState(new OperationState());
  }
    else {
      atm.setState(new ExitState());
    }
  }
}
