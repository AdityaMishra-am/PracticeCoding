package ATM.State;

import ATM.ATMMachine;
import ATM.BankAcc;
import ATM.Card;
import ATM.MoneyProcessor.FiveHundredProcessor;
import ATM.MoneyProcessor.HundredProcessor;
import ATM.MoneyProcessor.NotesProcessor;
import ATM.MoneyProcessor.TwoThousandProcessor;

public class WithdrawalState extends  ATMState{
  public  void withDrawBalance(ATMMachine atm, Card card, int money) {
    BankAcc bankAcc = card.getBankAcc();
    if (bankAcc.checkBalance() < money) {
      System.out.println("Gareeb ho tum");
      atm.setState(new ExitState());
    }
    else {
      NotesProcessor notesProcessor = new TwoThousandProcessor(new FiveHundredProcessor(
           new HundredProcessor(null)));
      card.getBankAcc().withdraw(money);
      notesProcessor.setNextNoteProcessor(money, atm);
      System.out.println("Money taken out");
      int currBal = card.getBankAcc().checkBalance();
      System.out.println("Curr Bal "+ currBal);
      atm.setState(new ExitState());
    }
  }
}
