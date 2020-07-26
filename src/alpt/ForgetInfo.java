package alpt;


public class ForgetInfo {
    public boolean isSkipped() {
        return skipped;
    }
    public boolean isNew(){return totalTimes==0&&(!skipped);}

    public double weight(WeightCalculator w) {
        if(skipped)return 0x1000000000000L;
        if(totalTimes==0)return 1;
        return w.calculate(passedTimes, totalTimes, lastCheck);
    }

    private boolean skipped;
    private int passedTimes;
    private int totalTimes;
    private long lastCheck;

    @Override
    public String toString() {
        return (skipped ? "S" : "K") + passedTimes + "/" + totalTimes + "/" + lastCheck;
    }

    public ForgetInfo(String s) {
        if (s.startsWith("S")) {
            skipped = true;
        } else if (s.startsWith("K")) {
            skipped = false;
        } else throw new IllegalArgumentException(s + " is not start with \"S\" or \"K\"");
        String[] ints = s.substring(1).split("/");
        passedTimes = Integer.parseInt(ints[0]);
        totalTimes = Integer.parseInt(ints[1]);
        lastCheck = Long.parseLong(ints[2]);
        check();
    }

    public ForgetInfo() {
        this.skipped = false;
        this.passedTimes = 0;
        this.totalTimes = 0;
        lastCheck = 0;
    }

    public void skip() {
        skipped = true;
    }

    public boolean uSkip() {
        skipped = !skipped;
        return skipped;
    }

    public void passed() {
        passedTimes++;
        totalTimes++;
        check();
        lastCheck = System.currentTimeMillis();
    }

    public void failed() {
        totalTimes++;
        check();
        lastCheck = System.currentTimeMillis();
    }

    private void check() {
        if (passedTimes > 99) passedTimes = 99;
        if (totalTimes > 99) totalTimes = 99;
        if (passedTimes < 0) passedTimes = 0;
        if (totalTimes < 0) totalTimes = 0;
    }


    public String score() {

        return (passedTimes < 10 ? "0" : "") + passedTimes + "/" + (totalTimes < 10 ? "0" : "") + totalTimes;
    }
}
