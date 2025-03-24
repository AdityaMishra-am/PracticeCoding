package ATM;

public class BankAcc {
  int money;
  int accNumber;

  public BankAcc(int initalMoney, int accNumber) {
    this.money = initalMoney;
    this.accNumber = accNumber;
  }
  public void withdraw(int amt) {
    this.money = money - amt;
  }
  public int checkBalance(){
    return this.money;
  }
}
