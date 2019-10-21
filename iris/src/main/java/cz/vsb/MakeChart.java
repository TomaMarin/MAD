package cz.vsb;

import org.knowm.xchart.XYChart;
import org.knowm.xchart.internal.chartpart.Chart;

import java.awt.*;
import java.util.List;

public class MakeChart  {
    private List<List<String>> records;

    public MakeChart() {
    }

    public List<List<String>> getRecords() {
        return records;
    }

    public void setRecords(List<List<String>> records) {
        this.records = records;
    }

    public MakeChart(List<List<String>> records) {
        this.records = records;
    }


}
