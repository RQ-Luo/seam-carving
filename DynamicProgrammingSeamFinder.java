package seamcarving;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DynamicProgrammingSeamFinder implements SeamFinder {
    public double[][] rotate(double[][] a) {
        double[][] b = new double[a[0].length][a.length];
        for (int i = 0; i < a.length; i++) {
            int end = a[0].length - 1;
            for (int j = 0; j < a[0].length; j++, end--) {
                b[end][i] = a[i][j];
            }
        }
        return b;
    }
    @Override
    public List<Integer> findHorizontalSeam(double[][] energies) {
        double[][] energiesH = rotate(energies);
        List<Integer> result = findVerticalSeam(energiesH);
        result.replaceAll(integer -> energies[0].length - integer - 1);
        return result;
    }

    @Override
    public List<Integer> findVerticalSeam(double[][] energies) {
        Map<String, Double> energyTo = new HashMap<>();
        Map<String, String> pointTo = new HashMap<>();
        List<Integer> theWay = new ArrayList<>();
        double minEnergy = Double.POSITIVE_INFINITY;
        String theEnd = null;
        for (int i = 0; i < energies[0].length; i++) {
            for (int j = 0; j < energies.length; j++) {
                if (i == 0) {
                    energyTo.put(i + " " + j, 0.0);
                } else {
                    double minE = Double.POSITIVE_INFINITY;
                    String pre = null;
                    for (int k = 0; k < 3; k++) {
                        int x = i - 1;
                        int y = j + k - 1;
                        if (y >= 0 && y < energies.length) {
                            if (energies[y][x] + energyTo.get(x + " " + y) < minE) {
                                minE = energies[y][x] + energyTo.get(x + " " + y);
                                pre = x + " " + y;
                            }
                        }
                    }
                    energyTo.put(i + " " + j, minE);
                    pointTo.put(i + " " + j, pre);
                    if (i == energies[0].length - 1) {
                        if (minEnergy > minE + energies[j][i]) {
                            minEnergy = minE + energies[j][i];
                            theEnd = i + " " + j;
                        }
                    }

                }
            }
        }
        theWay.add(Integer.parseInt(theEnd.split(" ")[1]));
        while (pointTo.containsKey(theEnd)) {
            theEnd = pointTo.get(theEnd);
            theWay.add(Integer.parseInt(theEnd.split(" ")[1]));
        }
        Collections.reverse(theWay);
        return theWay;
    }
}
