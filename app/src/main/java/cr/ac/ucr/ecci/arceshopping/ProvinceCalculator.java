package cr.ac.ucr.ecci.arceshopping;

import android.location.Location;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ProvinceCalculator {
    private final double sanJoseCoordinates[] = new double[]{9.9333300, -84.0833300};
    private final double alajuelaCoordinates[] = new double[]{10.0162500, -84.2116300};
    private final double cartagoCoordinates[] = new double[]{9.8644400, -83.9194400};
    private final double herediaCoordinates[] = new double[]{10.0023600, -84.1165100};
    private final double guanacasteCoordinates[] = new double[]{10.5406400, -85.7332900};
    private final double puntarenasCoordinates[] = new double[]{9.9762500, -84.8383600};
    private final double limonCoordinates[] = new double[]{9.9907400, -83.0359600};

    public ProvinceCalculator() {
        ;
    }

    /**
     * Calculates in which province the user is located approximately
     * @param lat user's latitude
     * @param log user's longitude
     * @return The province where the user is
     */
    public int calculateProvince(double lat, double log) {
        float results[] = new float[1];
        if (lat == 0 && log == 0)
            return 0;
        //Obtiene la distancia entre un punto y otro
        Map<Integer, Float> maps = new HashMap<Integer, Float>(7);
        Location.distanceBetween(lat, log, sanJoseCoordinates[0], sanJoseCoordinates[1], results);
        maps.put(0, results[0]);
        Location.distanceBetween(lat, log, alajuelaCoordinates[0], alajuelaCoordinates[1], results);
        maps.put(1, results[0]);
        Location.distanceBetween(lat, log, cartagoCoordinates[0], cartagoCoordinates[1], results);
        maps.put(2, results[0]);
        Location.distanceBetween(lat, log, herediaCoordinates[0], herediaCoordinates[1], results);
        maps.put(3, results[0]);
        Location.distanceBetween(lat, log, guanacasteCoordinates[0], guanacasteCoordinates[1], results);
        maps.put(4, results[0]);
        Location.distanceBetween(lat, log, puntarenasCoordinates[0], puntarenasCoordinates[1], results);
        maps.put(5, results[0]);
        Location.distanceBetween(lat, log, limonCoordinates[0], limonCoordinates[1], results);
        maps.put(6, results[0]);

        return Collections.min(maps.entrySet(), Map.Entry.comparingByValue()).getKey();
    }
}
