package service.race;

public interface StrategyFactory {
    Strategy createStrategy(Strategy_type strategy);
}
