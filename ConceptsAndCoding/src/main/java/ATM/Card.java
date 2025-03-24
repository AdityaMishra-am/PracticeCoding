package ATM;

public class Card {

  BankAcc bankAcc;
  int pin;

  public Card(int pin) {
    this.pin = pin;
  }

  public boolean authenticate(int pin) {
    return  this.pin == pin;
  }

  public BankAcc getBankAcc() {
    return bankAcc;
  }

  public void setBankAcc(BankAcc bankAcc) {
    this.bankAcc = bankAcc;
  }

}
