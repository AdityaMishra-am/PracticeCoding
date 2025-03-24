package DecoratorDesignPattern;

public class Margarita extends BasePizza{
  int cost;

  @Override
  int cost() {
    return this.cost;
  }

  public Margarita(int cost) {
    this.cost = cost;
  }

}
