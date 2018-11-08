package com.littlepay;

import com.google.common.collect.Collections2;
import com.littlepay.models.Stop;
import com.littlepay.models.Tap;
import com.littlepay.models.TapType;
import com.littlepay.services.TapsDAO;
import com.sun.istack.internal.Nullable;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import static com.littlepay.models.Stop.*;

public class Main {

    private static int journeyOneCost = 325;
    private static int journeyTwoCost = 550;
    private static int journeyThreeCost = 730;
    private static int maxJourneyCost = journeyThreeCost;

    private static int getJourneyCost(Stop tapOnStop, Stop tapOffStop) {
        if (tapOnStop == Stop1) {
            switch (tapOffStop) {
                case Stop1:
                    return 0;
                case Stop2:
                    return journeyOneCost;
                case Stop3:
                    return journeyThreeCost;
            }
        } else if (tapOnStop == Stop2) {
            switch (tapOffStop) {
                case Stop1:
                    return journeyOneCost;
                case Stop2:
                    return 0;
                case Stop3:
                    return journeyTwoCost;
            }
        } else if (tapOnStop == Stop3) {
            switch (tapOffStop) {
                case Stop1:
                    return journeyThreeCost;
                case Stop2:
                    return journeyTwoCost;
                case Stop3:
                    return 0;
            }
        }

        throw new RuntimeException("Unrecognised StopId '" + tapOnStop.name() + "'");
    }

    @Nullable
    private static Tap getCorrespondingTapOff(Tap tapOn, Collection<Tap> tapsOff) {
        for (Tap tapOff : tapsOff) {
            ZonedDateTime tapOffWindowEnd = tapOn.getTime().plusHours(4);
            if (tapOff.getTime().isAfter(tapOn.getTime())
                    && tapOn.getPan().equals(tapOff.getPan())) {
                if (tapOff.getTime().isAfter(tapOffWindowEnd)) {
                    System.out.println("No corresponding TapOFF found inside tapOff window for TapON with id " + tapOn.getId());
                    return null;
                }
                return tapOff;

            }
        }
        System.out.println("No corresponding TapOFF found for TapON with id " + tapOn.getId());
        return null;
    }

    private static void writeCSV(ArrayList<ArrayList<String>> data) throws IOException {
        File file = new File("output.csv");
        FileWriter fw = new FileWriter(file);
        BufferedWriter bw = new BufferedWriter(fw);

        bw.write("Started,Finished,DurationSecs,FromStopId,ToStopId,ChargeAmount,CompanyId,BusID,PAN,Status");
        bw.newLine();
        for (int i = 0; i < data.size(); i++) {
            ArrayList<String> line = data.get(i);
            if (line != null) {
                bw.write(String.join(",", line));
                bw.newLine();
            }
        }

        bw.close();
        fw.close();
    }

    public static void main(String[] args) {
        TapsDAO tapsDAO = new TapsDAO(args[0]);
        tapsDAO.init();

        ArrayList<Tap> tapsOn = new ArrayList<Tap>(Collections2.filter(tapsDAO.getDao(), tap -> tap.getTapType() == TapType.ON));
        ArrayList<Tap> tapsOff = new ArrayList<Tap>(Collections2.filter(tapsDAO.getDao(), tap -> tap.getTapType() == TapType.OFF));

        Collections.sort(tapsOn);
        Collections.sort(tapsOff);

        ArrayList<ArrayList<String>> journeys = new ArrayList<>();

        for (Tap tapOn : tapsOn) {
            Tap tapOff = getCorrespondingTapOff(tapOn, tapsOff);
            ArrayList<String> journey = new ArrayList<>();
            journey.add(tapOn.formatTimeAsString()); // Started
            journey.add(tapOff != null ? tapOff.formatTimeAsString() : null); // Finished
            journey.add(tapOff != null ? Long.toString(ChronoUnit.SECONDS.between(tapOn.getTime(), tapOff.getTime())) : null); // DurationSecs
            journey.add(tapOn.getStopId().name()); // FromStopId
            journey.add(tapOff != null ? tapOff.getStopId().name() : null); // ToStopId
            journey.add(Integer.toString(tapOff != null ? getJourneyCost(tapOn.getStopId(), tapOff.getStopId()) : maxJourneyCost)); // ChargeAmount
            journey.add(tapOn.getCompanyId()); // CompanyId
            journey.add(tapOn.getBusId()); // BusId
            journey.add(tapOn.getPan()); // PAN
            journeys.add(null); // Status (not yet implemented)

            journeys.add(journey);
        }

        try {
            writeCSV(journeys);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
