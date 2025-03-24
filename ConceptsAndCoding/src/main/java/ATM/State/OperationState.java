package ATM.State;

import ATM.ATMMachine;
import ATM.Card;
import ATM.TransactionType;

public class OperationState extends ATMState{

  @Override
  public void selectOperation(ATMMachine atm, TransactionType type) {
    switch (type) {
      case WITHDRAWAL:
        atm.setState(new WithdrawalState());
        break;
      case BAL_CHECK:
        break;
    }

  }
}
