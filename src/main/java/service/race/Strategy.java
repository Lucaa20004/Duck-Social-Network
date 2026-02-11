package service.race;
import domain.Duck;
import domain.Culoar;
import domain.Result;


public interface Strategy {
    Result solve(Duck[] list , Culoar[] list1);
}
