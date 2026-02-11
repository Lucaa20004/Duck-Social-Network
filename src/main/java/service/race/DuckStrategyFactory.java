package service.race;

public class DuckStrategyFactory implements StrategyFactory {

    public DuckStrategyFactory() {}

    @Override
    public Strategy createStrategy(Strategy_type strategy) {
        if (strategy == Strategy_type.BINARY_S) {
            return new BinaryStrategy();
        }
        else if (strategy == Strategy_type.BRUTE_S) {
            return new BinaryStrategy();
        }
        return null;
    }
}
