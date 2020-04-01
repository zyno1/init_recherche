package lib.math;

public abstract class Calcul {

    public static int sum(int... n) {
        int res = 0;
        for(int i : n) {
            res += i;
        }
        return res;
    }

}
