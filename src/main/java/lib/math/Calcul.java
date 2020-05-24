package lib.math;

public abstract class Calcul {

    /**
     *
     * @param n une collection d'entiers
     * @return la somme des éléments de n
     */
    public static int sum(int... n) {
        int res = 0;
        for(int i : n) {
            res += i;
        }
        return res;
    }

}
