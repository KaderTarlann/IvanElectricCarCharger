package com.oocode;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import java.io.IOException;
import java.io.StringReader;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.Integer.parseInt;
import static java.time.LocalDateTime.parse;
import static java.time.format.DateTimeFormatter.RFC_1123_DATE_TIME;
import static java.util.Comparator.comparingInt;

public class ChargeTimes {
    private final String url;

    public static void main(String[] args) throws Exception {
        new ChargeTimes().printReport();
    }

    public ChargeTimes() {
        this("https://api.nationalgrideso.com/dataset/91c0c70e-0ef5-4116-b6fa-7ad084b5e0e8/resource/db6c038f-98af-4570-ab60-24d71ebd0ae5/download/embedded-forecast.csv");
    }

    public ChargeTimes(String url) {
        this.url = url;
    }

    void printReport() throws IOException, CsvException {
    /*
"DATE_GMT","TIME_GMT","SETTLEMENT_DATE","SETTLEMENT_PERIOD","EMBEDDED_WIND_FORECAST","EMBEDDED_WIND_CAPACITY","EMBEDDED_SOLAR_FORECAST","EMBEDDED_SOLAR_CAPACITY"
"2023-12-11T00:00:00","11:30","2023-12-11T00:00:00",23,1333,6488,2417,15595
     */
        String input = new SimpleHttpClient().readUrl(url);
        System.out.println(reportWind(input));
    }

    static String reportWind(String input) throws IOException, CsvException {
        List<String[]> forecastRows;
        try (CSVReader csvReader = new CSVReader(new StringReader(input))) {
            forecastRows = csvReader.readAll();
        }
        return "Best times to plug in:\n" +
                forecastRows.stream().skip(1)
                        .sorted(comparingInt(row -> -parseInt(row[4])))
                        .limit(3)
                        .map(row -> ZonedDateTime.of(parse(row[0])
                                        .withHour(parseInt(row[1].split(":")[0]))
                                        .withMinute(parseInt(row[1].split(":")[1])),
                                ZoneId.of("GMT")))
                        .sorted()
                        .map((zonedDateTime) -> zonedDateTime.format(RFC_1123_DATE_TIME))
                        .collect(Collectors.joining("\n"));
    }


    static String reportSolarAndWind(String input) throws IOException, CsvException {
        List<String[]> forecastRows;
        try (CSVReader csvReader = new CSVReader(new StringReader(input))) {
            forecastRows = csvReader.readAll();
        }
        return "Best times to plug in:\n" +
                forecastRows.stream().skip(1)
                        .sorted(comparingInt(row -> -parseInt(row[4] + row[6])))
                        .limit(3)
                        .map(row -> ZonedDateTime.of(parse(row[0])
                                        .withHour(parseInt(row[1].split(":")[0]))
                                        .withMinute(parseInt(row[1].split(":")[1])),
                                ZoneId.of("GMT")))
                        .sorted()
                        .map((zonedDateTime) -> zonedDateTime.format(RFC_1123_DATE_TIME))
                        .collect(Collectors.joining("\n"));
    }
}
