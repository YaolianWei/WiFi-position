package com.capstone.wifiposition.algorithm;

import com.capstone.wifiposition.model.AccessPoint;
import com.capstone.wifiposition.model.Distance;
import com.capstone.wifiposition.model.Location;
import com.capstone.wifiposition.model.Places;
import com.capstone.wifiposition.model.ReferencePoint;
import com.capstone.wifiposition.model.WifiInfo;
import com.capstone.wifiposition.utils.AppConfig;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.realm.RealmList;

public class Algorithms {
    final static String K = "4";

    /**
     *
     * @param latestScanList
     *            the current scan list of APs
     * @param places
     *            the project details from db for current area
     *
     * @param algorithm_choice
     *            choice of several algorithms
     *
     * @return the location of user
     */
    public static Location processingAlgorithms(List<WifiInfo> latestScanList, Places places, int algorithm_choice) {

        int i, j;
        RealmList<AccessPoint> aps = places.getAps();
        ArrayList<Float> observedRSSValues = new ArrayList<Float>();
        WifiInfo temp_LR;
        int notFoundCounter = 0;
        // Read parameter of algorithm
//		String NaNValue = readParameter(RM, 0);

        // Check which mac addresses of radio map, we are currently listening.
        for (i = 0; i < aps.size(); ++i) {
            for (j = 0; j < latestScanList.size(); ++j) {
                temp_LR = latestScanList.get(j);
                // MAC Address Matched
                if (aps.get(i).getMac_address().compareTo(temp_LR.getBssid()) == 0) {
                    observedRSSValues.add(Float.valueOf(temp_LR.getLevel()).floatValue());
                    break;
                }
            }
            // A MAC Address is missing so we place a small value, NaN value
            if (j == latestScanList.size()) {
                observedRSSValues.add(AppConfig.NaN);
                ++notFoundCounter;
            }
        }

        if (notFoundCounter == aps.size())
            return null;

        // Read parameter of algorithm
        String parameter = readParameter(algorithm_choice);

        if (parameter == null)
            return null;

        switch (algorithm_choice) {

            case 1:
                return KNN_WKNN_Algorithm(places, observedRSSValues, parameter, false);
            case 2:
                return KNN_WKNN_Algorithm(places, observedRSSValues, parameter, true);
        }
        return null;

    }

    /**
     * Calculates user location based on Weighted/Not Weighted K Nearest
     * Neighbor (KNN) Algorithm
     *
     * @param place
     *            the project details from db for current area
     *
     * @param observedRSSValues
     *            RSS values currently observed
     * @param parameter
     *
     * @param isWeighted
     *            To be weighted or not
     *
     * @return The estimated user location
     */
    private static Location KNN_WKNN_Algorithm(Places place, ArrayList<Float> observedRSSValues,
                                                               String parameter, boolean isWeighted) {

        RealmList<AccessPoint> rssValues;
        float curResult = 0;
        ArrayList<Distance> locDistanceResultsList = new ArrayList<Distance>();
        String myLocation = null;
        int K;

        try {
            K = Integer.parseInt(parameter);
        } catch (Exception e) {
            return null;
        }

        // Construct a list with locations-distances pairs for currently
        // observed RSS values
        for (ReferencePoint referencePoint : place.getRps()) {
            rssValues = referencePoint.getScanAps();
            curResult = calculateEuclideanDistance(rssValues, observedRSSValues);

            if (curResult == Float.NEGATIVE_INFINITY)
                return null;

            locDistanceResultsList.add(0, new Distance(curResult, referencePoint.getLocId(), referencePoint.getName()));
        }

        // Sort locations-distances pairs based on minimum distances
        Collections.sort(locDistanceResultsList, new Comparator<Distance>() {
            public int compare(Distance gd1, Distance gd2) {
                return (gd1.getDistance() > gd2.getDistance() ? 1 : (gd1.getDistance() == gd2.getDistance() ? 0 : -1));
            }
        });

        if (!isWeighted) {
            myLocation = calculateAverageKDistanceLocations(locDistanceResultsList, K);
        } else {
            myLocation = calculateWeightedAverageKDistanceLocations(locDistanceResultsList, K);
        }
        Location places = new Location(myLocation, locDistanceResultsList);
        return places;

    }

    /**
     * Calculates the Euclidean distance between the currently observed RSS
     * values and the RSS values for a specific location.
     *
     * @param l1
     *            RSS values of a location in stored in AP obj of locations
     * @param l2
     *            RSS values currently observed
     *
     * @return The Euclidean distance, or MIN_VALUE for error
     */
    private static float calculateEuclideanDistance(RealmList<AccessPoint> l1, ArrayList<Float> l2) {

        float finalResult = 0;
        float v1;
        float v2;
        float temp;

        for (int i = 0; i < l1.size(); ++i) {

            try {
                l1.get(i).getMeanRss();
                v1 = (float) l1.get(i).getMeanRss();
                v2 = l2.get(i);
            } catch (Exception e) {
                return Float.NEGATIVE_INFINITY;
            }

            // do the procedure
            temp = v1 - v2;
            temp *= temp;

            // do the procedure
            finalResult += temp;
        }
        return ((float) Math.sqrt(finalResult));
    }

    /**
     * Calculates the Probability of the user being in the currently observed
     * RSS values and the RSS values for a specific location.
     *
     * @param l1
     *            RSS values of a location in stored in AP obj of locations
     * @param l2
     *            RSS values currently observed
     *
     * @return The Probability for this location, or MIN_VALUE for error
     */
    private static double calculateProbability(RealmList<AccessPoint> l1, ArrayList<Float> l2, float sGreek) {
        double finalResult = 1;
        float v1;
        float v2;
        double temp;

        for (int i = 0; i < l1.size(); ++i) {

            try {
                v1 = (float) l1.get(i).getMeanRss();
                v2 = l2.get(i);
            } catch (Exception e) {
                return Double.NEGATIVE_INFINITY;
            }

            temp = v1 - v2;

            temp *= temp;

            temp = -temp;

            temp /= (double) (sGreek * sGreek);
            temp = Math.exp(temp);

            //Do not allow zero instead stop on small possibility
            if (finalResult * temp != 0)
                finalResult = finalResult * temp;
        }
        return finalResult;
    }

    /**
     * Calculates the Average of the K locations that have the shortest
     * distances D
     *
     * @param LocDistance_Results_List
     *            Locations-Distances pairs sorted by distance
     * @param K
     *            The number of locations used
     * @return The estimated user location, or null for error
     */
    private static String calculateAverageKDistanceLocations(ArrayList<Distance> LocDistance_Results_List, int K) {
        float sumX = 0.0f;
        float sumY = 0.0f;

        String[] LocationArray = new String[2];
        float x, y;

        int K_Min = K < LocDistance_Results_List.size() ? K : LocDistance_Results_List.size();

        // Calculate the sum of X and Y
        for (int i = 0; i < K_Min; ++i) {
            LocationArray = LocDistance_Results_List.get(i).getLocation().split(" ");

            try {
                x = Float.valueOf(LocationArray[0].trim()).floatValue();
                y = Float.valueOf(LocationArray[1].trim()).floatValue();
            } catch (Exception e) {
                return null;
            }

            sumX += x;
            sumY += y;
        }

        // Calculate the average
        sumX /= K_Min;
        sumY /= K_Min;

        return sumX + " " + sumY;
    }

    /**
     * Calculates the Weighted Average of the K locations that have the shortest
     * distances D
     *
     * @param LocDistance_Results_List
     *            Locations-Distances pairs sorted by distance
     * @param K
     *            The number of locations used
     * @return The estimated user location, or null for error
     */
    private static String calculateWeightedAverageKDistanceLocations(ArrayList<Distance> LocDistance_Results_List, int K) {
        double LocationWeight = 0.0f;
        double sumWeights = 0.0f;
        double WeightedSumX = 0.0f;
        double WeightedSumY = 0.0f;

        String[] LocationArray = new String[2];
        float x, y;

        int K_Min = K < LocDistance_Results_List.size() ? K : LocDistance_Results_List.size();

        // Calculate the weighted sum of X and Y
        for (int i = 0; i < K_Min; ++i) {
            if (LocDistance_Results_List.get(i).getDistance() != 0.0) {
                LocationWeight = 1 / LocDistance_Results_List.get(i).getDistance();
            } else {
                LocationWeight = 100;
            }
            LocationArray = LocDistance_Results_List.get(i).getLocation().split(" ");

            try {
                x = Float.valueOf(LocationArray[0].trim()).floatValue();
                y = Float.valueOf(LocationArray[1].trim()).floatValue();
            } catch (Exception e) {
                return null;
            }

            sumWeights += LocationWeight;
            WeightedSumX += LocationWeight * x;
            WeightedSumY += LocationWeight * y;

        }

        WeightedSumX /= sumWeights;
        WeightedSumY /= sumWeights;

        return WeightedSumX + " " + WeightedSumY;
    }

    /**
     * Calculates the Weighted Average over ALL locations where the weights are
     * the Normalized Probabilities
     *
     * @param LocDistance_Results_List
     *            Locations-Probability pairs
     *
     * @return The estimated user location, or null for error
     */
    private static String calculateWeightedAverageProbabilityLocations(ArrayList<Distance> LocDistance_Results_List) {
        double sumProbabilities = 0.0f;
        double WeightedSumX = 0.0f;
        double WeightedSumY = 0.0f;
        double NP;
        float x, y;
        String[] LocationArray = new String[2];

        // Calculate the sum of all probabilities
        for (int i = 0; i < LocDistance_Results_List.size(); ++i)
            sumProbabilities += LocDistance_Results_List.get(i).getDistance();

        // Calculate the weighted (Normalized Probabilities) sum of X and Y
        for (int i = 0; i < LocDistance_Results_List.size(); ++i) {
            LocationArray = LocDistance_Results_List.get(i).getLocation().split(" ");

            try {
                x = Float.valueOf(LocationArray[0].trim()).floatValue();
                y = Float.valueOf(LocationArray[1].trim()).floatValue();
            } catch (Exception e) {
                return null;
            }

            NP = LocDistance_Results_List.get(i).getDistance() / sumProbabilities;

            WeightedSumX += (x * NP);
            WeightedSumY += (y * NP);

        }

        return WeightedSumX + " " + WeightedSumY;

    }

    /**
     * Reads the parameters from the file
     *
     * @param file
     *            the file of radiomap, to read parameters
     *
     * @param algorithm_choice
     *            choice of several algorithms
     *
     * @return The parameter for the algorithm
     *
     */
    private static String readParameter(File file, int algorithm_choice) {
        String line;
        BufferedReader reader = null;

        String parameter = null;

        try {
            FileReader fr = new FileReader(file.getAbsolutePath().replace(".txt", "-parameters2.txt"));

            reader = new BufferedReader(fr);

            while ((line = reader.readLine()) != null) {

                /* Ignore the labels */
                if (line.startsWith("#") || line.trim().equals("")) {
                    continue;
                }

                /* Split fields */
                String[] temp = line.split(":");

                /* The file may be corrupted so ignore reading it */
                if (temp.length != 2) {
                    return null;
                }

                if (algorithm_choice == 0 && temp[0].equals("NaN")) {
                    parameter = temp[1];
                    break;
                } else if (algorithm_choice == 1 && temp[0].equals("KNN")) {
                    parameter = temp[1];
                    break;
                } else if (algorithm_choice == 2 && temp[0].equals("WKNN")) {
                    parameter = temp[1];
                    break;
                }

            }

        } catch (Exception e) {
            return null;
        } finally {
            if (reader != null)
                try {
                    reader.close();
                } catch (IOException e) {
                }
        }

        return parameter;
    }

    private static String readParameter(int algorithm_choice) {
        String parameter = null;

        if (algorithm_choice == 1) {
            // && ("KNN")
            parameter = K;
        } else if (algorithm_choice == 2) {
            // && ("WKNN")
            parameter = K;
        }
        return parameter;
    }
}
