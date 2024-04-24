package com.oocode;

import com.opencsv.exceptions.CsvException;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class ChargeTimesTests {
    ChargeTimes unitUnderTest = new ChargeTimes();
    String url = "src/test/resources/embedded-forecast.csv";
    String sampleContent = """
        "DATE_GMT","TIME_GMT","SETTLEMENT_DATE","SETTLEMENT_PERIOD","EMBEDDED_WIND_FORECAST","EMBEDDED_WIND_CAPACITY","EMBEDDED_SOLAR_FORECAST","EMBEDDED_SOLAR_CAPACITY"
        "2024-04-24T00:00:00","09:30","2024-04-24T00:00:00",21,1525,6562,4776,15905
        "2024-04-24T00:00:00","10:00","2024-04-24T00:00:00",22,1497,6562,5286,15905
        "2024-04-24T00:00:00","10:30","2024-04-24T00:00:00",23,1464,6562,5694,15905
        "2024-04-24T00:00:00","11:00","2024-04-24T00:00:00",24,1431,6562,6019,15905
        """.replaceAll("([\\r\\n])", "");

    String reportResultWind = "Best times to plug in:\n" +
            "Wed, 24 Apr 2024 09:30:00 GMT\n" +
            "Wed, 24 Apr 2024 10:00:00 GMT\n" +
            "Wed, 24 Apr 2024 10:30:00 GMT";

    String reportResultSolarAndWind = "Best times to plug in:\n" +
            "Wed, 24 Apr 2024 09:30:00 GMT\n" +
            "Wed, 24 Apr 2024 10:00:00 GMT\n" +
            "Wed, 24 Apr 2024 11:00:00 GMT";

    @Test
    public void testUrlContent() throws IOException {
        String urlString = new File(url).toURI().toString();
        String contents = new SimpleHttpClient().readUrl(urlString).replaceAll("([\\r\\n])", "");

        assertThat(contents, equalTo(sampleContent));
    }

    @Test
    public void testFormattedString() throws IOException, CsvException {
        String urlString = new File(url).toURI().toString();
        String contents = new SimpleHttpClient().readUrl(urlString);
        String forecastRows = ChargeTimes.reportWind(contents);

        assertThat(forecastRows, equalTo(reportResultWind));
    }

    @Test
    public void testSolarAndWindForecast() throws IOException, CsvException {
        String urlString = new File(url).toURI().toString();
        String contents = new SimpleHttpClient().readUrl(urlString);
        String forecastSolarAndWind = ChargeTimes.reportSolarAndWind(contents);

        assertThat(forecastSolarAndWind, equalTo(reportResultSolarAndWind));
    }

}
