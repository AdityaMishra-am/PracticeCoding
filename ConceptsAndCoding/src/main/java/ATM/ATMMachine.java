package ATM;

import ATM.State.ATMState;
import ATM.State.IdleState;

public class ATMMachine {
  ATMState state;
  int totalMoney;
  int twoThousandNotes;
  int fiveHundredNotes;
  int hundredNotes;

  public ATMMachine(int totalMoney, int twoThousandNotes, int fiveHundredNotes, int hundredNotes) {
    this.totalMoney = totalMoney;
    this.twoThousandNotes = twoThousandNotes;
    this.fiveHundredNotes = fiveHundredNotes;
    this.hundredNotes = hundredNotes;
    this.setState(new IdleState());
  }

  public int getTotalMoney() {
    return totalMoney;
  }

  public void setTotalMoney(int withDrawAmt) {
    this.totalMoney = this.totalMoney - withDrawAmt;
  }

  public int getTwoThousandNotes() {
    return twoThousandNotes;
  }

  public void setTwoThousandNotes(int twoThousandNotes) {
    this.twoThousandNotes = twoThousandNotes;
  }

  public int getFiveHundredNotes() {
    return fiveHundredNotes;
  }

  public void setFiveHundredNotes(int fiveHundredNotes) {
    this.fiveHundredNotes = fiveHundredNotes;
  }

  public int getHundredNotes() {
    return hundredNotes;
  }

  public void setHundredNotes(int hundredNotes) {
    this.hundredNotes = hundredNotes;
  }

  public ATMState getState() {
    return state;
  }

  public void setState(ATMState state) {
    this.state = state;
  }
}
