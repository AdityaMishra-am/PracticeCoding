package ATM;

public class ATMController {
  public static void main(String args[]) {
    ATMMachine atmMachine = new ATMMachine(4000, 1, 2, 10);
    User user = new User("user");
    Card card = new Card(12121);
    BankAcc bankAcc = new BankAcc(4000, 133113);
    card.setBankAcc(bankAcc);
    user.setCard(card);

    atmMachine.getState().insertCard(card, atmMachine);
    atmMachine.getState().auth(atmMachine,card,12121);
    atmMachine.getState().selectOperation(atmMachine, TransactionType.WITHDRAWAL);
    atmMachine.getState().withDrawBalance(atmMachine, card, 2500);

  }
  void init(){

  }
}
