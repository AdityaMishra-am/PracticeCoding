package ATM.State;

import ATM.ATMMachine;
import ATM.Card;
import ATM.TransactionType;

public abstract class ATMState {

  public void insertCard(Card card, ATMMachine atmMachine) {
   System.out.println("Error");
  }
 public void auth(ATMMachine atm, Card card, int pin) {
   System.out.println("Error");
 }
 public void selectOperation(ATMMachine atm,TransactionType type) {
   System.out.println("Error");
 }
 public  void withDrawBalance(ATMMachine atm, Card card, int money) {
   System.out.println("Error");
 }
 public void checkBalance(Card card) {
   System.out.println("Bal");
 }
 public void exit(ATMMachine atm) {
   System.out.println("Error");
 }

}
