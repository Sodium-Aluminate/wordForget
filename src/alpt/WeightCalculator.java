package alpt;

public interface WeightCalculator {

    /**
     * @return
     *          0.000...1 ------------------- 1 ------------------- 2 ------------------- 0x1000000000000L (super big)
     *       need recheck now    new words(no need to check)     not yet                  skipped(no need to check)
     */
    double calculate(int pass, int total, long time);
}
