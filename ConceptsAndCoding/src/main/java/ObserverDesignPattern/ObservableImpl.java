package ObserverDesignPattern;

import java.util.ArrayList;
import java.util.List;
public class ObservableImpl implements Obervable{
List<Observer> observers;
  public ObservableImpl() {
    this.observers = new ArrayList<>();
  }
  @Override
  public void add(Observer observer) {
    this.observers.add(observer);
  }

  @Override
  public void removeObserver(int i) {
    //
  }

  @Override
  public void notifyObservers() {
    for (int i = 0; i<observers.size(); i++) {
      observers.get(i).update("yoyo");
    }
  }
}
