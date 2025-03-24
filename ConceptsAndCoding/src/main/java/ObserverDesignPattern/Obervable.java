package ObserverDesignPattern;

public interface Obervable {
public void add(Observer observer);
public void removeObserver(int i);
public void notifyObservers();
}
