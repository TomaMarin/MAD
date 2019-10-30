package cz.vsb;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.*;
import org.knowm.xchart.*;
import org.knowm.xchart.style.Styler;
import org.knowm.xchart.style.markers.SeriesMarkers;

import java.awt.*;
import java.io.*;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

//public class Main implements ExampleChart<XYChart> {
public class Main {


    public static void main(String[] args) throws IOException {

        List<List<String>> records = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("iris.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(";");
                records.add(Arrays.asList(values));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


//        for (List<String> r : records) {
//            for (String rA : r) {
//                System.out.print(rA + " ");
//            }
//            System.out.println();
//        }

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("iris graphs and values ");

        records.remove(0);
//        int i = 0;
//        for (List<String> r : records) {
//            int k = 0;
//            Row row = sheet.createRow(i);
//            for (String rA : r) {
//                Cell cell = row.createCell(k);
//                cell.setCellValue(rA);
//                k++;
//            }
//            i++;
//        }

        List<Double> xSepalData = new LinkedList<Double>();
        List<Double> ySepalData = new LinkedList<Double>();

        List<Double> xPetalData = new LinkedList<Double>();
        List<Double> yPetalData = new LinkedList<Double>();


        int i = 0;
        for (List<String> r : records) {
            int k = 0;
            for (String rA : r) {
                if (k == 0) {
                    xSepalData.add(Double.parseDouble(rA.trim().replace(",", ".")));

                } else if (k == 1) {
                    ySepalData.add(Double.parseDouble(rA.trim().replace(",", ".")));
                } else if (k == 2) {
                    xPetalData.add(Double.parseDouble(rA.trim().replace(",", ".")));
                } else if (k == 3) {
                    yPetalData.add(Double.parseDouble(rA.trim().replace(",", ".")));
                }
                k++;
            }
            i++;
        }

        Point sepalAvgPoint = calculateAveragePoint(records, 0, 1);
        // Create Chart


        XYChart chart = new XYChartBuilder().width(600).height(500).title("Sepal width and length").xAxisTitle("X - width").yAxisTitle("Y - length").build();

        XYSeries series = chart.addSeries("Sepal Average", Collections.singletonList(sepalAvgPoint.getxPoint()), Collections.singletonList(sepalAvgPoint.getyPoint()));
        series.setMarker(SeriesMarkers.DIAMOND);

        chart.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Scatter);
        chart.getStyler().setChartTitleVisible(false);
        chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNW);
        chart.getStyler().setMarkerSize(16);


        List<Point> sepalPointsWithEuclidanNorm = new ArrayList<>();

        for (List<String> listOfRecords : records) {
            Double xPoint = Double.parseDouble(listOfRecords.get(0).replace(",", "."));
            Double yPoint = Double.parseDouble(listOfRecords.get(1).replace(",", "."));
            Point newSepalPoint = new Point(xPoint, yPoint, calculateEuclidanNorm(listOfRecords.get(0), listOfRecords.get(1)));
            sepalPointsWithEuclidanNorm.add(newSepalPoint);
//            System.out.println(newSepalPoint.toString());
        }

        sepalAvgPoint.setEuclidanNorm(calculateEuclidanNorm(sepalAvgPoint.getxPoint().toString(), sepalAvgPoint.getyPoint().toString()));
//        System.out.println("Sepal avg point value: " + sepalAvgPoint.toString());
//        System.out.println("Sepal avg point euclidan norm value: " + sepalAvgPoint.getEuclidanNorm());
//        System.out.println("Sepal euclidan distance between avg point and 2nd point in list: " + calculateEuclidanDistance(sepalPointsWithEuclidanNorm.get(1).getxPoint(), sepalAvgPoint.getxPoint(), sepalPointsWithEuclidanNorm.get(1).getyPoint(), sepalAvgPoint.getyPoint()));
//        System.out.println("Sepal cosine similarity between avg point and 2nd point in list: " + calculateCosineSimilarity(sepalAvgPoint, sepalPointsWithEuclidanNorm.get(0)));
        Point bestMatchOfSepalForEuclidanDistance = findBestMatchOfEuclidanDistanceForPoints(sepalPointsWithEuclidanNorm, sepalAvgPoint);
        Point bestMatchOfSepalForCosineSimilarity = findBestMatchOfCosineSimilarityForPoints(sepalPointsWithEuclidanNorm, sepalAvgPoint);

//        System.out.println("best match of euclidan distance: " + bestMatchOfSepalForEuclidanDistance);
//        System.out.println("best match of cosine similarity: " + bestMatchOfSepalForCosineSimilarity);

        chart.addSeries("Sepal width and length", xSepalData, ySepalData);
        XYSeries bestMatchOfSepalSeries = chart.addSeries("Best Match of euclidan distance", Collections.singletonList(bestMatchOfSepalForEuclidanDistance.getxPoint()), Collections.singletonList(bestMatchOfSepalForEuclidanDistance.getyPoint()));
        bestMatchOfSepalSeries.setMarker(SeriesMarkers.CROSS);


        XYSeries bestMatchOfSepalSeriesForCosineSimilarity = chart.addSeries("Best Match of cosine similarity", Collections.singletonList(bestMatchOfSepalForCosineSimilarity.getxPoint()), Collections.singletonList(bestMatchOfSepalForCosineSimilarity.getyPoint()));
        bestMatchOfSepalSeriesForCosineSimilarity.setMarker(SeriesMarkers.PLUS);

        System.out.println("total var of sepal:" + calcTotalVariance(sepalPointsWithEuclidanNorm, sepalAvgPoint));

//        new SwingWrapper(chart).displayChart();


        Point petalAvgPoint = calculateAveragePoint(records, 2, 3);
        petalAvgPoint.setEuclidanNorm(calculateEuclidanNorm(petalAvgPoint.getxPoint().toString(), petalAvgPoint.getyPoint().toString()));

        XYChart petalChart = new XYChartBuilder().width(600).height(500).title("Petal width and length").xAxisTitle("X - width").yAxisTitle("Y - length").build();
        XYSeries petalSeries = petalChart.addSeries("Petal Average", Collections.singletonList(petalAvgPoint.getxPoint()), Collections.singletonList(petalAvgPoint.getyPoint()));
        petalSeries.setMarker(SeriesMarkers.DIAMOND);
        petalSeries.setFillColor(Color.MAGENTA);
        petalChart.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Scatter);
        petalChart.getStyler().setChartTitleVisible(false);
        petalChart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNW);
        petalChart.getStyler().setMarkerSize(16);

        List<Point> petalPointsWithEuclidanNorm = new ArrayList<>();

        for (List<String> listOfRecords : records) {
            Double xPoint = Double.parseDouble(listOfRecords.get(2).replace(",", "."));
            Double yPoint = Double.parseDouble(listOfRecords.get(3).replace(",", "."));
            Point newPetalPoint = new Point(xPoint, yPoint, calculateEuclidanNorm(listOfRecords.get(2), listOfRecords.get(3)));
            petalPointsWithEuclidanNorm.add(newPetalPoint);
//            System.out.println(newPetalPoint.toString());
        }


//        System.out.println("Petal avg point value: " + petalAvgPoint.toString());
//        System.out.println("Petal avg point euclidan norm value: " + petalAvgPoint.getEuclidanNorm());
//        System.out.println("Petal euclidan distance between avg point and 2nd point in list: " + calculateEuclidanDistance(petalPointsWithEuclidanNorm.get(1).getxPoint(), petalAvgPoint.getxPoint(), petalPointsWithEuclidanNorm.get(1).getyPoint(), petalAvgPoint.getyPoint()));
//        System.out.println("Petal cosine similarity between avg point and 2nd point in list: " + calculateCosineSimilarity(petalAvgPoint, petalPointsWithEuclidanNorm.get(0)));
        Point bestMatchOfEuclidanDistanceForPoints = findBestMatchOfEuclidanDistanceForPoints(petalPointsWithEuclidanNorm, petalAvgPoint);
        Point bestMatchOfCosineSimilarityForPoints = findBestMatchOfCosineSimilarityForPoints(petalPointsWithEuclidanNorm, petalAvgPoint);
//        System.out.println("best bestMatchOfEuclidanDistanceForPoints: " + bestMatchOfEuclidanDistanceForPoints);
//        System.out.println("best bestMatchOfCosineSimilarityForPoints: " + bestMatchOfCosineSimilarityForPoints);
//
        petalChart.addSeries("Petal width and length", xPetalData, yPetalData);

        XYSeries bestMatchOfPetalSeries = petalChart.addSeries("BestMatch of euclidan distance", Collections.singletonList(bestMatchOfEuclidanDistanceForPoints.getxPoint()), Collections.singletonList(bestMatchOfEuclidanDistanceForPoints.getyPoint()));
        bestMatchOfPetalSeries.setMarker(SeriesMarkers.CROSS);

        XYSeries bestMatchOfCosineSimilarityForPointsPetalSeries = petalChart.addSeries("BestMatch of cosine similarity", Collections.singletonList(bestMatchOfCosineSimilarityForPoints.getxPoint()), Collections.singletonList(bestMatchOfCosineSimilarityForPoints.getyPoint()));
        bestMatchOfCosineSimilarityForPointsPetalSeries.setMarker(SeriesMarkers.PLUS);
//        new SwingWrapper(petalChart).displayChart();

        System.out.println("total var of petal:" + calcTotalVariance(petalPointsWithEuclidanNorm, petalAvgPoint));

        HashMap<Double, Double> petalX = calcFrequencyOfListX(petalPointsWithEuclidanNorm);
        HashMap<Double, Double> petalY = calcFrequencyOfListY(petalPointsWithEuclidanNorm);
        HashMap<Double, Double> sepalX = calcFrequencyOfListX(sepalPointsWithEuclidanNorm);
        HashMap<Double, Double> sepalY = calcFrequencyOfListY(sepalPointsWithEuclidanNorm);
        System.out.println();
        System.out.println(petalX);
        System.out.println(petalY);
        System.out.println(sepalX);
        System.out.println(sepalY);

        System.out.println();
        System.out.println("AVG of sepal " + sepalAvgPoint);
        System.out.println("AVG of petal " + petalAvgPoint);
        System.out.println();

        System.out.println("TOTAL VAR of sepal_X " + calcTotalVarianceForOneAttributeX(sepalPointsWithEuclidanNorm, sepalAvgPoint));
        System.out.println("TOTAL VAR of sepal_Y " + calcTotalVarianceForOneAttributeY(sepalPointsWithEuclidanNorm, sepalAvgPoint));
        System.out.println("TOTAL VAR of petal_X " + calcTotalVarianceForOneAttributeX(petalPointsWithEuclidanNorm, petalAvgPoint));
        System.out.println("TOTAL VAR of petal_Y " + calcTotalVarianceForOneAttributeY(petalPointsWithEuclidanNorm, petalAvgPoint));
        System.out.println();

        System.out.println("Standard deviation of sepal_X " + Math.sqrt(calcTotalVarianceForOneAttributeX(sepalPointsWithEuclidanNorm, sepalAvgPoint)));
        System.out.println("Standard deviation of sepal_Y " + Math.sqrt(calcTotalVarianceForOneAttributeY(sepalPointsWithEuclidanNorm, sepalAvgPoint)));
        System.out.println("Standard deviation of petal_X " + Math.sqrt(calcTotalVarianceForOneAttributeX(petalPointsWithEuclidanNorm, petalAvgPoint)));
        System.out.println("Standard deviation of petal_Y " + Math.sqrt(calcTotalVarianceForOneAttributeY(petalPointsWithEuclidanNorm, petalAvgPoint)));
        System.out.println();
        System.out.println("median of SEPAL_X: " + findMedianForX(sepalPointsWithEuclidanNorm, sepalPointsWithEuclidanNorm.size()));
        System.out.println("median of SEPAL_Y: " + findMedianForY(sepalPointsWithEuclidanNorm, sepalPointsWithEuclidanNorm.size()));
        System.out.println("median of PETAL_X: " + findMedianForX(petalPointsWithEuclidanNorm, petalPointsWithEuclidanNorm.size()));
        System.out.println("median of PETAL_Y: " + findMedianForY(petalPointsWithEuclidanNorm, petalPointsWithEuclidanNorm.size()));
        System.out.println();
        System.out.println("⟨μ−σ,μ+σ⟩ of SEPAL_X: " + (sepalAvgPoint.getxPoint() - Math.sqrt(calcTotalVarianceForOneAttributeX(sepalPointsWithEuclidanNorm, sepalAvgPoint))) + " μ+σ => " + (sepalAvgPoint.getxPoint() + Math.sqrt(calcTotalVarianceForOneAttributeX(sepalPointsWithEuclidanNorm, sepalAvgPoint))));
        System.out.println("⟨μ−σ,μ+σ⟩ of SEPAL_Y: " + (sepalAvgPoint.getyPoint() - Math.sqrt(calcTotalVarianceForOneAttributeY(sepalPointsWithEuclidanNorm, sepalAvgPoint))) + " μ+σ => " + (sepalAvgPoint.getyPoint() + Math.sqrt(calcTotalVarianceForOneAttributeY(sepalPointsWithEuclidanNorm, sepalAvgPoint))));
        System.out.println("⟨μ−σ,μ+σ⟩ of PETAL_X: " + (petalAvgPoint.getxPoint() - Math.sqrt(calcTotalVarianceForOneAttributeX(petalPointsWithEuclidanNorm, petalAvgPoint))) + " μ+σ => " + (petalAvgPoint.getxPoint() + Math.sqrt(calcTotalVarianceForOneAttributeX(petalPointsWithEuclidanNorm, petalAvgPoint))));
        System.out.println("⟨μ−σ,μ+σ⟩ of PETAL_Y: " + (petalAvgPoint.getyPoint() - Math.sqrt(calcTotalVarianceForOneAttributeY(petalPointsWithEuclidanNorm, petalAvgPoint))) + " μ+σ => " + (petalAvgPoint.getyPoint() + Math.sqrt(calcTotalVarianceForOneAttributeY(petalPointsWithEuclidanNorm, petalAvgPoint))));
        System.out.println();
        System.out.println("⟨μ−2σ,μ+2σ⟩ of SEPAL_X: " + (sepalAvgPoint.getxPoint() - 2 * Math.sqrt(calcTotalVarianceForOneAttributeX(sepalPointsWithEuclidanNorm, sepalAvgPoint))) + " μ+2σ => " + (sepalAvgPoint.getxPoint() + 2 * Math.sqrt(calcTotalVarianceForOneAttributeX(sepalPointsWithEuclidanNorm, sepalAvgPoint))));
        System.out.println("⟨μ−2σ,μ+2σ⟩ of SEPAL_Y: " + (sepalAvgPoint.getyPoint() - 2 * Math.sqrt(calcTotalVarianceForOneAttributeY(sepalPointsWithEuclidanNorm, sepalAvgPoint))) + " μ+2σ => " + (sepalAvgPoint.getyPoint() + 2 * Math.sqrt(calcTotalVarianceForOneAttributeY(sepalPointsWithEuclidanNorm, sepalAvgPoint))));
        System.out.println("⟨μ−2σ,μ+2σ⟩ of PETAL_X: " + (petalAvgPoint.getxPoint() - 2 * Math.sqrt(calcTotalVarianceForOneAttributeX(petalPointsWithEuclidanNorm, petalAvgPoint))) + " μ+2σ => " + (petalAvgPoint.getxPoint() + 2 * Math.sqrt(calcTotalVarianceForOneAttributeX(petalPointsWithEuclidanNorm, petalAvgPoint))));
        System.out.println("⟨μ−2σ,μ+2σ⟩ of PETAL_Y: " + (petalAvgPoint.getyPoint() - 2 * Math.sqrt(calcTotalVarianceForOneAttributeY(petalPointsWithEuclidanNorm, petalAvgPoint))) + " μ+2σ => " + (petalAvgPoint.getyPoint() + 2 * Math.sqrt(calcTotalVarianceForOneAttributeY(petalPointsWithEuclidanNorm, petalAvgPoint))));
        System.out.println();
        System.out.println("⟨μ−3σ,μ+3σ⟩ of SEPAL_X: " + (sepalAvgPoint.getxPoint() - 3 * Math.sqrt(calcTotalVarianceForOneAttributeX(sepalPointsWithEuclidanNorm, sepalAvgPoint))) + " μ+3σ => " + (sepalAvgPoint.getxPoint() + 3 * Math.sqrt(calcTotalVarianceForOneAttributeX(sepalPointsWithEuclidanNorm, sepalAvgPoint))));
        System.out.println("⟨μ−3σ,μ+3σ⟩ of SEPAL_Y: " + (sepalAvgPoint.getyPoint() - 3 * Math.sqrt(calcTotalVarianceForOneAttributeY(sepalPointsWithEuclidanNorm, sepalAvgPoint))) + " μ+3σ => " + (sepalAvgPoint.getyPoint() + 3 * Math.sqrt(calcTotalVarianceForOneAttributeY(sepalPointsWithEuclidanNorm, sepalAvgPoint))));
        System.out.println("⟨μ−3σ,μ+3σ⟩ of PETAL_X: " + (petalAvgPoint.getxPoint() - 3 * Math.sqrt(calcTotalVarianceForOneAttributeX(petalPointsWithEuclidanNorm, petalAvgPoint))) + " μ+3σ => " + (petalAvgPoint.getxPoint() + 3 * Math.sqrt(calcTotalVarianceForOneAttributeX(petalPointsWithEuclidanNorm, petalAvgPoint))));
        System.out.println("⟨μ−3σ,μ+3σ⟩ of PETAL_Y: " + (petalAvgPoint.getyPoint() - 3 * Math.sqrt(calcTotalVarianceForOneAttributeY(petalPointsWithEuclidanNorm, petalAvgPoint))) + " μ+3σ => " + (petalAvgPoint.getyPoint() + 3 * Math.sqrt(calcTotalVarianceForOneAttributeY(petalPointsWithEuclidanNorm, petalAvgPoint))));
        System.out.println();


        TreeMap<Double, Double> relativeFrequencyMapSepalX = new TreeMap<>();
        for (Map.Entry<Double, Double> m : sepalX.entrySet()) {
            relativeFrequencyMapSepalX.put(m.getKey(), (m.getValue() / (double) records.size()));
        }

        TreeMap<Double, Double> nominalDistributionMapSepalX = new TreeMap<>();
        for (Map.Entry<Double, Double> m : sepalX.entrySet()) {
            nominalDistributionMapSepalX.put(m.getKey(), (countNominalDistribution(m.getKey(), sepalAvgPoint.getxPoint(), Math.sqrt(calcTotalVarianceForOneAttributeX(sepalPointsWithEuclidanNorm, sepalAvgPoint)))));
        }

        XYChart relativeFrequencyMapSepalXChart = new XYChartBuilder().width(600).height(500).title("relativeFrequencyMapSepalX").xAxisTitle("X - width").yAxisTitle("Y - length").build();
        relativeFrequencyMapSepalXChart.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Line);
        relativeFrequencyMapSepalXChart.getStyler().setChartTitleVisible(false);
        relativeFrequencyMapSepalXChart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNW);
        relativeFrequencyMapSepalXChart.getStyler().setMarkerSize(16);
        relativeFrequencyMapSepalXChart.addSeries("relativeFrequencyMapSepalX Series", new ArrayList<Double>(relativeFrequencyMapSepalX.keySet()), relativeFrequencyMapSepalX.values().stream().collect(Collectors.toList()));
        XYSeries nominalDistributionMapSepalXChart = relativeFrequencyMapSepalXChart.addSeries("nominalDistributionMapSepalX", new ArrayList<Double>(nominalDistributionMapSepalX.keySet()), nominalDistributionMapSepalX.values().stream().collect(Collectors.toList()));
        relativeFrequencyMapSepalXChart.addSeries("Mean", Collections.singletonList(sepalAvgPoint.getxPoint()), Collections.singletonList(sepalAvgPoint.getxPoint() / records.size()));
//        new SwingWrapper<>(relativeFrequencyMapSepalXChart).displayChart();


        TreeMap<Double, Double> relativeFrequencyMapSepalY = new TreeMap<>();
        for (Map.Entry<Double, Double> m : sepalY.entrySet()) {
            relativeFrequencyMapSepalY.put(m.getKey(), (m.getValue() / (double) records.size()));
        }

        TreeMap<Double, Double> nominalDistributionMapSepalY = new TreeMap<>();
        for (Map.Entry<Double, Double> m : sepalY.entrySet()) {
            nominalDistributionMapSepalY.put(m.getKey(), (countNominalDistribution(m.getKey(), sepalAvgPoint.getyPoint(), Math.sqrt(calcTotalVarianceForOneAttributeY(sepalPointsWithEuclidanNorm, sepalAvgPoint)))));
        }

        XYChart relativeFrequencyMapSepalYChart = new XYChartBuilder().width(600).height(500).title("relativeFrequencyMapSepalY").xAxisTitle("X - width").yAxisTitle("Y - length").build();
        relativeFrequencyMapSepalYChart.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Line);
        relativeFrequencyMapSepalYChart.getStyler().setChartTitleVisible(false);
        relativeFrequencyMapSepalYChart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNW);
        relativeFrequencyMapSepalYChart.getStyler().setMarkerSize(16);
        relativeFrequencyMapSepalYChart.addSeries("relativeFrequencyMapSepalY Series", new ArrayList<Double>(relativeFrequencyMapSepalY.keySet()), relativeFrequencyMapSepalY.values().stream().collect(Collectors.toList()));
        XYSeries nominalDistributionMapSepalYChart = relativeFrequencyMapSepalYChart.addSeries("relativeFrequencyMapSepalYChart", new ArrayList<Double>(nominalDistributionMapSepalY.keySet()), nominalDistributionMapSepalY.values().stream().collect(Collectors.toList()));

//        new SwingWrapper<>(relativeFrequencyMapSepalYChart).displayChart();


        TreeMap<Double, Double> relativeFrequencyMapPetalX = new TreeMap<>();
        for (Map.Entry<Double, Double> m : petalX.entrySet()) {
            relativeFrequencyMapPetalX.put(m.getKey(), (m.getValue() / (double) records.size()));
        }

        TreeMap<Double, Double> nominalDistributionMapPetalX = new TreeMap<>();
        for (Map.Entry<Double, Double> m : petalX.entrySet()) {
            nominalDistributionMapPetalX.put(m.getKey(), (countNominalDistribution(m.getKey(), petalAvgPoint.getxPoint(), Math.sqrt(calcTotalVarianceForOneAttributeX(petalPointsWithEuclidanNorm, petalAvgPoint)))));
        }

        XYChart relativeFrequencyMapPetalXChart = new XYChartBuilder().width(600).height(500).title("relativeFrequencyMapPetalX").xAxisTitle("X - width").yAxisTitle("Y - length").build();
        relativeFrequencyMapPetalXChart.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Line);
        relativeFrequencyMapPetalXChart.getStyler().setChartTitleVisible(false);
        relativeFrequencyMapPetalXChart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNW);
        relativeFrequencyMapPetalXChart.getStyler().setMarkerSize(16);
        relativeFrequencyMapPetalXChart.addSeries("relativeFrequencyMapPetalXChart Series", new ArrayList<Double>(relativeFrequencyMapPetalX.keySet()), relativeFrequencyMapPetalX.values().stream().collect(Collectors.toList()));
        relativeFrequencyMapPetalXChart.addSeries("nominalDistributionMapPetalX Series", new ArrayList<Double>(nominalDistributionMapPetalX.keySet()), nominalDistributionMapPetalX.values().stream().collect(Collectors.toList()));
//        relativeFrequencyMapPetalXChart.addSeries("Mean Series",Collections.singletonList(sepalAvgPoint.getyPoint()), Collections.singletonList(sepalAvgPoint.getyPoint()/(double)records.size())).setFillColor(Color.MAGENTA);
//        new SwingWrapper<>(relativeFrequencyMapPetalXChart).displayChart();


        TreeMap<Double, Double> relativeFrequencyMapPetalY = new TreeMap<>();
        for (Map.Entry<Double, Double> m : petalY.entrySet()) {
            relativeFrequencyMapPetalY.put(m.getKey(), (m.getValue() / (double) records.size()));
        }

        TreeMap<Double, Double> nominalDistributionMapPetalY = new TreeMap<>();
        for (Map.Entry<Double, Double> m : petalY.entrySet()) {
            nominalDistributionMapPetalY.put(m.getKey(), (countNominalDistribution(m.getKey(), petalAvgPoint.getyPoint(), Math.sqrt(calcTotalVarianceForOneAttributeY(petalPointsWithEuclidanNorm, petalAvgPoint)))));
        }

        XYChart relativeFrequencyMapPetalYChart = new XYChartBuilder().width(600).height(500).title("relativeFrequencyMapPetalY").xAxisTitle("X - width").yAxisTitle("Y - length").build();
        relativeFrequencyMapPetalYChart.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Line);
        relativeFrequencyMapPetalYChart.getStyler().setChartTitleVisible(false);
        relativeFrequencyMapPetalYChart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNW);
        relativeFrequencyMapPetalYChart.getStyler().setMarkerSize(16);
        relativeFrequencyMapPetalYChart.addSeries("relativeFrequencyMapPetalYChart Series", new ArrayList<Double>(relativeFrequencyMapPetalY.keySet()), relativeFrequencyMapPetalY.values().stream().collect(Collectors.toList()));
        relativeFrequencyMapPetalYChart.addSeries("nominalDistributionMapPetalY Series", new ArrayList<Double>(nominalDistributionMapPetalY.keySet()), nominalDistributionMapPetalY.values().stream().collect(Collectors.toList()));

//        new SwingWrapper<>(relativeFrequencyMapPetalYChart).displayChart();
        List<FullPoint> vals = new ArrayList<>();
        for (int n = 0; n < xSepalData.size(); n++) {
            Double sepX = xSepalData.get(n);
            Double sepY = ySepalData.get(n);
            Double petX = xPetalData.get(n);
            Double petY = yPetalData.get(n);
            FullPoint fp = new FullPoint(sepX, sepY, petX, petY);

            vals.add(fp);
        }

        List<HashMap<FullPoint, List<FullPoint>>> mapOfClusterMaps = new ArrayList<>();




        for (int j = 0; j < 5; j++) {
            HashMap<FullPoint, List<FullPoint>> kk = setKMeans(3, vals);
            HashMap<FullPoint, List<FullPoint>> newCentroids = calculateNewCentroids(kk, vals);
            int it = 0;
            while (true) {
                List<FullPoint> oldCentroids = new ArrayList<>(newCentroids.keySet());
                List<FullPoint> newerCentroids = new ArrayList<>(calculateNewCentroids(newCentroids, vals).keySet());
                if (checkIfCentroidsEqual(oldCentroids, newerCentroids)) {
                    mapOfClusterMaps.add(newCentroids);
                    break;
                }
                newCentroids = calculateNewCentroids(newCentroids, vals);
                it++;
            }
            System.out.println("ite: " + it);
        }

        List<Double[]> sseResults =  calculateSSE(mapOfClusterMaps);
    }

    private static List<Double[]> calculateSSE(List<HashMap<FullPoint, List<FullPoint>>> listOfclusterMaps) {
        List<Double[]>  listOfSSEValuesOfEachCluster = new ArrayList<>();
        for (HashMap<FullPoint, List<FullPoint>> clusterMap : listOfclusterMaps) {
            Double[] ssesOfClustersOfCurrentMap = calculateSumOfTheSquaresOfTheDistancesOfEachPointFromTheCentroid(clusterMap);
            listOfSSEValuesOfEachCluster.add(ssesOfClustersOfCurrentMap);
        }
        return listOfSSEValuesOfEachCluster;
    }

    private static Double[] calculateSumOfTheSquaresOfTheDistancesOfEachPointFromTheCentroid(HashMap<FullPoint, List<FullPoint>> clusterMap) {
        Double[] ssesOfClusters = new Double[clusterMap.keySet().size()];
        for (int i = 0; i < ssesOfClusters.length; i++) {
            ssesOfClusters[i] = 0.0;
        }

        for (int i = 0; i < clusterMap.keySet().size(); i++) {
            List<FullPoint> actualClusters = new ArrayList(clusterMap.keySet());
            for (int j = 0; j < clusterMap.get(actualClusters.get(i)).size(); j++) {
                ssesOfClusters[i] += calculateEuclidanDistanceFor4D(actualClusters.get(i).getxSepalPoint(), actualClusters.get(i).getySepalPoint(), actualClusters.get(i).getxPetalPoint(), actualClusters.get(i).getyPetalPoint(), clusterMap.get(actualClusters.get(i)).get(j).getxSepalPoint(), clusterMap.get(actualClusters.get(i)).get(j).getySepalPoint(), clusterMap.get(actualClusters.get(i)).get(j).getxPetalPoint(), clusterMap.get(actualClusters.get(i)).get(j).getyPetalPoint());
            }
        }
        return ssesOfClusters;
    }

    private static boolean checkIfCentroidsEqual(List<FullPoint> oldCentroids, List<FullPoint> newCentroids) {
        return oldCentroids.equals(newCentroids);
    }

    private static HashMap<FullPoint, List<FullPoint>> calculateNewCentroids(HashMap<FullPoint, List<FullPoint>> previousClusterData, List<FullPoint> vals) {
        HashMap<FullPoint, List<FullPoint>> newCentroids = new HashMap<>();
        DecimalFormat df = new DecimalFormat("#.#");
        for (FullPoint centroid : previousClusterData.keySet()) {
            FullPoint newCentroid = new FullPoint(0.0, 0.0, 0.0, 0.0);
            previousClusterData.get(centroid).stream().forEach(fullPoint -> {
                newCentroid.setxSepalPoint(newCentroid.getxSepalPoint() + fullPoint.getxSepalPoint());
                newCentroid.setySepalPoint(newCentroid.getySepalPoint() + fullPoint.getySepalPoint());
                newCentroid.setxPetalPoint(newCentroid.getxPetalPoint() + fullPoint.getxPetalPoint());
                newCentroid.setyPetalPoint(newCentroid.getyPetalPoint() + fullPoint.getyPetalPoint());

            });
            newCentroid.setxSepalPoint(Double.parseDouble(df.format(newCentroid.getxSepalPoint() / (double) previousClusterData.get(centroid).size())));
            newCentroid.setySepalPoint(Double.parseDouble(df.format(newCentroid.getySepalPoint() / (double) previousClusterData.get(centroid).size())));
            newCentroid.setxPetalPoint(Double.parseDouble(df.format(newCentroid.getxPetalPoint() / (double) previousClusterData.get(centroid).size())));
            newCentroid.setyPetalPoint(Double.parseDouble(df.format(newCentroid.getyPetalPoint() / (double) previousClusterData.get(centroid).size())));
            newCentroids.put(newCentroid, new ArrayList<FullPoint>());
        }


        for (FullPoint item : vals) {
            Double minDistance = 1000.0;
            FullPoint bestCentroidForItem = new FullPoint();
            for (FullPoint centroid : newCentroids.keySet()) {
                Double actualDistance = calculateEuclidanDistanceFor4D(item.getxSepalPoint(), item.getySepalPoint(), item.getxPetalPoint(), item.getyPetalPoint()
                        , centroid.getxSepalPoint(), centroid.getySepalPoint(), centroid.getxPetalPoint(), centroid.getyPetalPoint());
                if (actualDistance < minDistance) {
                    minDistance = actualDistance;
                    bestCentroidForItem = centroid;
                }
            }

            List<FullPoint> actualClusterForNearestCentroid = new ArrayList<>();
            if (newCentroids.get(bestCentroidForItem) != null) {
                actualClusterForNearestCentroid = newCentroids.get(bestCentroidForItem);
            }
            actualClusterForNearestCentroid.add(item);
            newCentroids.put(bestCentroidForItem, actualClusterForNearestCentroid);
        }


        return newCentroids;
    }

    private static HashMap<FullPoint, List<FullPoint>> setKMeans(int numberOfCentroids, List<FullPoint> vals) {
        List<FullPoint> actualCentroids = new ArrayList<>();
        DecimalFormat df = new DecimalFormat("#.#");
        HashMap<FullPoint, List<FullPoint>> clusterMap = new HashMap<>();
        for (int i = 0; i < numberOfCentroids; i++) {
            actualCentroids.add(vals.get((int) (Math.random() * vals.size())));
        }

        for (FullPoint fp : actualCentroids) {
            clusterMap.put(fp, new ArrayList<>());
        }

        for (FullPoint item : vals) {
            Double minDistance = 1000.0;
            FullPoint bestCentroidForItem = new FullPoint();
            for (FullPoint centroid : clusterMap.keySet()) {
                Double actualDistance = calculateEuclidanDistanceFor4D(item.getxSepalPoint(), item.getySepalPoint(), item.getxPetalPoint(), item.getyPetalPoint()
                                                                    , centroid.getxSepalPoint(), centroid.getySepalPoint(), centroid.getxPetalPoint(), centroid.getyPetalPoint());
                if (actualDistance < minDistance) {
                    minDistance = actualDistance;
                    bestCentroidForItem = centroid;
                }
            }

            List<FullPoint> actualClusterForNearestCentroid = new ArrayList<>();
            if (clusterMap.get(bestCentroidForItem) != null) {
                actualClusterForNearestCentroid = clusterMap.get(bestCentroidForItem);
            }
            actualClusterForNearestCentroid.add(item);
            clusterMap.put(bestCentroidForItem, actualClusterForNearestCentroid);
        }


        return clusterMap;
    }

    private static XYChart createChart(String title, List<Double> xVals, List<Double> yVals) {
        XYChart empiricDistributionOfSepalXChart = new XYChartBuilder().width(600).height(500).title(title).xAxisTitle("empiric val").yAxisTitle("occurrences").build();
        empiricDistributionOfSepalXChart.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Line);
        empiricDistributionOfSepalXChart.getStyler().setChartTitleVisible(false);
        empiricDistributionOfSepalXChart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNW);
        empiricDistributionOfSepalXChart.getStyler().setMarkerSize(16);
        empiricDistributionOfSepalXChart.addSeries(title + " Series", xVals, yVals);
//        XYSeries nominalDistributionMapSepalXChart = empiricDistributionOfSepalChart.addSeries("nominalDistributionMapSepalX",new ArrayList<Double>(nominalDistributionMapSepalX.keySet()),nominalDistributionMapSepalX.values().stream().collect(Collectors.toList()));
//        empiricDistributionOfSepalChart.addSeries("Mean", Collections.singletonList(sepalAvgPoint.getxPoint()),Collections.singletonList(sepalAvgPoint.getxPoint()/records.size()));
        return empiricDistributionOfSepalXChart;
    }

    private static TreeMap<Double, Double> calcNormalDistributionOfDistanceOfList(List<Point> vals, boolean calcX) {
        DecimalFormat df = new DecimalFormat("#.#");
        Double mean = 0.0;
        Double variance = 0.0;
        Double deviation = 0.0;
        TreeMap<Double, Double> euclidianDistancesRelativeFrequency = new TreeMap<Double, Double>();
        List<Double> euclidanDistances = new ArrayList<>();
        for (Point fp : vals) {
            for (Point sp : vals) {
                Double actualVal = 0.0;
                if (calcX) {
                    actualVal = Double.parseDouble(df.format(calculateEuclidanDistanceForOneAttribute(fp.getxPoint(), sp.getxPoint())));
                } else {
                    actualVal = Double.parseDouble(df.format(calculateEuclidanDistanceForOneAttribute(fp.getyPoint(), sp.getyPoint())));
                }
                mean += actualVal;
                euclidanDistances.add(actualVal);
                if (euclidianDistancesRelativeFrequency.containsKey(actualVal)) {
                    euclidianDistancesRelativeFrequency.put(actualVal, (euclidianDistancesRelativeFrequency.get(actualVal) + 1.0));
                } else {
                    euclidianDistancesRelativeFrequency.put(actualVal, 1.0);
                }
            }

        }

        mean = mean / ((double) (150));

        for (Double varVal : euclidanDistances) {
            variance += Math.pow(varVal - mean, 2);
        }

        variance = variance / ((double) (150));
        deviation = Math.sqrt(variance);
        TreeMap<Double, Double> euclidianNormalDistributionOfDistances = new TreeMap<Double, Double>();
        for (Map.Entry<Double, Double> e : euclidianDistancesRelativeFrequency.entrySet()) {
            euclidianNormalDistributionOfDistances.put(e.getKey(), countNominalDistribution(e.getValue(), mean, deviation));
        }
        return euclidianNormalDistributionOfDistances;
    }

    private static TreeMap<Double, Double> calcEmpiricDistributionOfDistanceOfList(List<Point> vals, int amountOfRecords, boolean calcEmpiricDistance, boolean calcX) {
        DecimalFormat df = new DecimalFormat("#.#");
        TreeMap<Double, Double> freqMap = new TreeMap<Double, Double>();
        for (Point fp : vals) {
            for (Point sp : vals) {
                Double actualEucVal = 0.0;
                if (calcX) {
                    actualEucVal = Math.abs(calculateEuclidanDistanceForOneAttribute(fp.getxPoint(), sp.getxPoint()));
                } else {
                    actualEucVal = Math.abs(calculateEuclidanDistanceForOneAttribute(fp.getyPoint(), sp.getyPoint()));
                }

                actualEucVal = Math.abs(Double.parseDouble(df.format(actualEucVal)));
                if (freqMap.containsKey(actualEucVal)) {
                    freqMap.put(actualEucVal, (freqMap.get(actualEucVal) + 1.0));
                } else {
                    freqMap.put(actualEucVal, (1.0));
                }
            }
        }

        if (calcEmpiricDistance) {
            TreeMap<Double, Double> empiricDistanceDistribution = new TreeMap<>();
            for (Map.Entry<Double, Double> m : freqMap.entrySet()) {
                empiricDistanceDistribution.put(m.getKey(), (m.getValue() / (double) amountOfRecords));
            }
            return empiricDistanceDistribution;
        }
        return freqMap;
    }


    private static HashMap<Double, Double> calcFrequencyOfListX(List<Point> points) {
        HashMap freqOfvales = new HashMap<Double, Integer>();

        for (Point p : points) {
            if (!freqOfvales.containsKey(p.getxPoint())) {
                freqOfvales.put(p.getxPoint(), 0.0);
            }
            freqOfvales.put(p.getxPoint(), calcFrequencyX(points, p.getxPoint()));

        }

        return freqOfvales;
    }

    private static HashMap<Double, Double> calcFrequencyOfListY(List<Point> points) {
        HashMap freqOfvales = new HashMap<Double, Double>();

        for (Point p : points) {
            if (!freqOfvales.containsKey(p.getyPoint())) {
                freqOfvales.put(p.getyPoint(), 0);
            }
            freqOfvales.put(p.getyPoint(), (double) (calcFrequencyY(points, p.getyPoint())));

        }
        return freqOfvales;
    }

    private static Double calcFrequencyX(List<Point> points, Double amount) {
        Double freq = 0.0;
        for (Point p : points) {
            if (p.getxPoint().equals(amount)) {
                freq++;
            }
        }

        return freq;
    }


    private static int calcFrequencyY(List<Point> points, double amount) {
        int freq = 0;
        for (Point p : points) {
            if (p.getyPoint().equals(amount)) {
                freq++;
            }
        }

        return freq;
    }


    private static double calcTotalVariance(List<Point> points, Point avgPoint) {
        double totalAmount = 0;
        for (Point p : points) {
            totalAmount += Math.abs(Math.pow(calculateEuclidanDistance(p.getxPoint(), avgPoint.getxPoint(), p.getyPoint(), avgPoint.getyPoint()), 2));
        }
        return totalAmount / points.size();

    }


    private static double calcTotalVarianceForOneAttributeX(List<Point> points, Point avgPoint) {
        double totalAmount = 0;
        for (Point p : points) {
            totalAmount += Math.abs(Math.pow(calculateEuclidanDistanceForOneAttribute(p.getxPoint(), avgPoint.getxPoint()), 2));
        }
        return totalAmount / points.size();

    }


    private static double calcTotalVarianceForOneAttributeY(List<Point> points, Point avgPoint) {
        double totalAmount = 0;
        for (Point p : points) {
            totalAmount += Math.abs(Math.pow(calculateEuclidanDistanceForOneAttribute(p.getyPoint(), avgPoint.getyPoint()), 2));
        }
        return totalAmount / points.size();

    }

    public static double findMedianForX(List<Point> points, int n) {
        Double[] vals = new Double[points.size()];

        for (int i = 0; i < vals.length; i++) {
            vals[i] = points.get(i).getxPoint();
        }

        // First we sort the array
        Arrays.sort(vals);

        // check for even case
        if (n % 2 != 0)
            return (double) vals[n / 2];

        return (double) (vals[(n - 1) / 2] + vals[n / 2]) / 2.0;
    }


    public static double findMedianForY(List<Point> points, int n) {
        Double[] vals = new Double[points.size()];

        for (int i = 0; i < vals.length; i++) {
            vals[i] = points.get(i).getyPoint();
        }

        // First we sort the array
        Arrays.sort(vals);

        // check for even case
        if (n % 2 != 0)
            return (double) vals[n / 2];

        return (double) (vals[(n - 1) / 2] + vals[n / 2]) / 2.0;
    }

    private static Point calculateAveragePoint(List<List<String>> records, int widthIndex, int lengthIndex) {
        Point averagePoint = new Point();

        int i = 0;
        double xPoint = 0.0;
        double yPoint = 0.0;
        for (List<String> r : records) {
            int k = 0;
            for (String rA : r) {
                if (k == widthIndex) {
                    xPoint += Double.parseDouble(rA.trim().replace(",", "."));
                } else if (k == lengthIndex) {
                    yPoint += Double.parseDouble(rA.trim().replace(",", "."));
                }
                k++;
            }
            i++;
        }

        averagePoint.setxPoint(xPoint / (double) records.size());
        averagePoint.setyPoint(yPoint / (double) records.size());
        return averagePoint;
    }

    private static Double calculateEuclidanNorm(String xA, String yA) {
        double euclidanPoint = 0.0;

        int i = 0;
        double xPoint = 0.0;
        double yPoint = 0.0;

        xPoint = Double.parseDouble(xA.trim().replace(",", "."));

        yPoint = Double.parseDouble(yA.trim().replace(",", "."));

        euclidanPoint = Math.sqrt(Math.pow(xPoint, 2) + Math.pow(yPoint, 2));

        return euclidanPoint;
    }

    private static Double calculateEuclidanDistance(Double x1, Double x2, Double y1, Double y2) {
        return Math.sqrt((y2 - y1) * (y2 - y1) + (x2 - x1) * (x2 - x1));
    }

    private static Double calculateEuclidanDistanceFor4D(Double x1, Double x2, Double x3, Double x4, Double y1, Double y2, Double y3, Double y4) {
        return Math.sqrt((Math.pow(x1 - y1, 2)) + (Math.pow(x2 - y2, 2)) + (Math.pow(x3 - y3, 2)) + (Math.pow(x4 - y4, 2)));
    }

    private static Double calculateEuclidanDistanceForOneAttribute(Double x1, Double x2) {
        return Math.sqrt((x2 - x1) * (x2 - x1));
    }

    private static Double calculateCosineSimilarity(Point a, Point b) {
        double nominator = (a.getxPoint() * b.getxPoint()) + (a.getyPoint() + b.getyPoint());
        double sqrtOfA = Math.sqrt((Math.pow(a.getxPoint(), 2)) + (Math.pow(a.getyPoint(), 2)));
        double sqrtOfB = Math.sqrt((Math.pow(b.getxPoint(), 2)) + (Math.pow(b.getyPoint(), 2)));
        double denominator = sqrtOfA * sqrtOfB;
        return ((nominator) / (denominator));
    }

    private static Point findBestMatchOfEuclidanDistanceForPoints(List<Point> points, Point pointToMatch) {
        double bestMatchvalue = 5.0;
        Point bestMatchPoint = new Point();
        for (Point p : points) {

            if (Math.abs(calculateEuclidanDistance(pointToMatch.getxPoint(), p.getxPoint(), pointToMatch.getyPoint(), p.getyPoint())) <= bestMatchvalue) {
                bestMatchvalue = Math.abs(calculateEuclidanDistance(pointToMatch.getxPoint(), p.getxPoint(), pointToMatch.getyPoint(), p.getyPoint()));
                bestMatchPoint = p;
            }
//            System.out.println("bestMatchvalue   " + bestMatchvalue + "   " + p);
        }
//        System.out.println(bestMatchvalue);
        return bestMatchPoint;
    }

    private static Point findBestMatchOfCosineSimilarityForPoints(List<Point> points, Point pointToMatch) {
        Point bestPointToMatch = new Point();
        double bestMatchValue = -1;
        for (Point actualPoint : points) {
//            System.out.println("Cosine similarity with avg  "+ calculateCosineSimilarity(actualPoint,pointToMatch));
            if (Math.abs(calculateCosineSimilarity(pointToMatch, actualPoint)) >= bestMatchValue) {
                bestMatchValue = Math.abs(calculateCosineSimilarity(pointToMatch, actualPoint));
                bestPointToMatch = actualPoint;
            }
//            System.out.println("bestMaatch value : " + bestMatchValue);

        }
//        System.out.println("bestMaatch value : " + bestMatchValue);
        return bestPointToMatch;
    }

    public static Double countNominalDistribution(double x, double mean, double deviation) {
        return (1 / (Math.sqrt(2 * Math.PI * Math.pow(deviation, 2)))) * Math.exp(-((Math.pow((x - mean), 2)) / (2 * Math.pow(deviation, 2)))) / 5;
    }


}
