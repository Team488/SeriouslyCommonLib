package xbot.common.logging;

public class IntermittentConsoleLogger {

    private int i = 0;
    private int modulus = 500;

    public void logSometimes(String message) {
        if (i % modulus == 0) {
            System.out.println(message);
        }
        i++;
    }

    public void setModulus(int modulus) {
        this.modulus = modulus;
    }
}