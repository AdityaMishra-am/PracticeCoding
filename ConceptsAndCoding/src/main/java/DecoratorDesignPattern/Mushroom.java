package DecoratorDesignPattern;

public class Mushroom extends PizzaDecorator{

  BasePizza basePizza;
  int extraCost;

  public Mushroom(BasePizza basePizza, int extraCost) {
    this.basePizza = basePizza;
    this.extraCost = extraCost;
  }

  @Override
  int cost() {
    return basePizza.cost() + extraCost;
  }
}
