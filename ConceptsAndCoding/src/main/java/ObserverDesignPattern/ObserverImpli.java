package ObserverDesignPattern;

public class ObserverImpli implements  Observer{

  Obervable obervable;

  public ObserverImpli(Obervable obervable) {
    this.obervable = obervable;
  }
  public void update(String messege) {
    System.out.println("show "+ messege);
  }
}
