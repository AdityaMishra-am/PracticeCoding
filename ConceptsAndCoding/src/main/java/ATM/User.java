package ATM;

public class User {
  String name;
  Card card;
  public User(String name) {
    this.name = name;
  }
  public Card getCard() {
    return card;
  }

  public void setCard(Card card) {
    this.card = card;
  }


}
