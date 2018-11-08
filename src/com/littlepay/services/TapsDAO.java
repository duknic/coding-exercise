package com.littlepay.services;

import com.littlepay.models.Stop;
import com.littlepay.models.Tap;
import com.littlepay.models.TapType;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class TapsDAO {
    private String filePath;
    private ArrayList<Tap> dao;

    public TapsDAO(String filePath) {
        this.filePath = filePath;
    }

    public ArrayList<Tap> getDao() {
        return dao;
    }

    public Tap readTapFromStringArr(String[] tapData) {
        return new Tap(
                tapData[0],
                LocalDateTime.parse(tapData[1], Tap.formatter).atZone(ZoneId.systemDefault()),
                TapType.valueOf(tapData[2]),
                Stop.valueOf(tapData[3]),
                tapData[4],
                tapData[5],
                tapData[6]
        );
    }


    public void init() {
        if (filePath != null) {
            try {
                this.dao = new BufferedReader(new FileReader(filePath))
                        .lines()
                        .skip(1) // ignore csv column headers
                        .map(line -> {
                            String[] tap = line.split(",");
                            return readTapFromStringArr(tap);
                        })
                        .collect(Collectors.toCollection(ArrayList::new));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
