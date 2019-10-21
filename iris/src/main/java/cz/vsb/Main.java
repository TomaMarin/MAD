package cz.vsb;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.*;
import org.knowm.xchart.*;
import org.knowm.xchart.style.Styler;
import org.knowm.xchart.style.markers.SeriesMarkers;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.stream.Stream;

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
        System.out.println("Sepal avg point value: " + sepalAvgPoint.toString());
        System.out.println("Sepal avg point euclidan norm value: " + sepalAvgPoint.getEuclidanNorm());
        System.out.println("Sepal euclidan distance between avg point and 2nd point in list: " + calculateEuclidanDistance(sepalPointsWithEuclidanNorm.get(1).getxPoint(), sepalAvgPoint.getxPoint(), sepalPointsWithEuclidanNorm.get(1).getyPoint(), sepalAvgPoint.getyPoint()));
        System.out.println("Sepal cosine similarity between avg point and 2nd point in list: " + calculateCosineSimilarity(sepalAvgPoint, sepalPointsWithEuclidanNorm.get(0)));
        Point bestMatchOfSepalForEuclidanDistance = findBestMatchOfEuclidanDistanceForPoints(sepalPointsWithEuclidanNorm, sepalAvgPoint);
        Point bestMatchOfSepalForCosineSimilarity = findBestMatchOfCosineSimilarityForPoints(sepalPointsWithEuclidanNorm, sepalAvgPoint);

        System.out.println("best match of euclidan distance: " + bestMatchOfSepalForEuclidanDistance);
        System.out.println("best match of cosine similarity: " + bestMatchOfSepalForCosineSimilarity);

        chart.addSeries("Sepal width and length", xSepalData, ySepalData);
        XYSeries bestMatchOfSepalSeries = chart.addSeries("Best Match of euclidan distance", Collections.singletonList(bestMatchOfSepalForEuclidanDistance.getxPoint()), Collections.singletonList(bestMatchOfSepalForEuclidanDistance.getyPoint()));
        bestMatchOfSepalSeries.setMarker(SeriesMarkers.CROSS);


        XYSeries bestMatchOfSepalSeriesForCosineSimilarity = chart.addSeries("Best Match of cosine similarity", Collections.singletonList(bestMatchOfSepalForCosineSimilarity.getxPoint()), Collections.singletonList(bestMatchOfSepalForCosineSimilarity.getyPoint()));
        bestMatchOfSepalSeriesForCosineSimilarity.setMarker(SeriesMarkers.PLUS);

        System.out.println("total var of sepal:"+ calcTotalVariance(sepalPointsWithEuclidanNorm,sepalAvgPoint) );

        new SwingWrapper(chart).displayChart();


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
            System.out.println(newPetalPoint.toString());
        }


        System.out.println("Petal avg point value: " + petalAvgPoint.toString());
        System.out.println("Petal avg point euclidan norm value: " + petalAvgPoint.getEuclidanNorm());
        System.out.println("Petal euclidan distance between avg point and 2nd point in list: " + calculateEuclidanDistance(petalPointsWithEuclidanNorm.get(1).getxPoint(), petalAvgPoint.getxPoint(), petalPointsWithEuclidanNorm.get(1).getyPoint(), petalAvgPoint.getyPoint()));
        System.out.println("Petal cosine similarity between avg point and 2nd point in list: " + calculateCosineSimilarity(petalAvgPoint, petalPointsWithEuclidanNorm.get(0)));
        Point bestMatchOfEuclidanDistanceForPoints = findBestMatchOfEuclidanDistanceForPoints(petalPointsWithEuclidanNorm, petalAvgPoint);
        Point bestMatchOfCosineSimilarityForPoints = findBestMatchOfCosineSimilarityForPoints(petalPointsWithEuclidanNorm, petalAvgPoint);
        System.out.println("best bestMatchOfEuclidanDistanceForPoints: " + bestMatchOfEuclidanDistanceForPoints);
        System.out.println("best bestMatchOfCosineSimilarityForPoints: " + bestMatchOfCosineSimilarityForPoints);
//
        petalChart.addSeries("Petal width and length", xPetalData, yPetalData);

        XYSeries bestMatchOfPetalSeries = petalChart.addSeries("BestMatch of euclidan distance", Collections.singletonList(bestMatchOfEuclidanDistanceForPoints.getxPoint()), Collections.singletonList(bestMatchOfEuclidanDistanceForPoints.getyPoint()));
        bestMatchOfPetalSeries.setMarker(SeriesMarkers.CROSS);

        XYSeries bestMatchOfCosineSimilarityForPointsPetalSeries = petalChart.addSeries("BestMatch of cosine similarity", Collections.singletonList(bestMatchOfCosineSimilarityForPoints.getxPoint()), Collections.singletonList(bestMatchOfCosineSimilarityForPoints.getyPoint()));
        bestMatchOfCosineSimilarityForPointsPetalSeries.setMarker(SeriesMarkers.PLUS);
        new SwingWrapper(petalChart).displayChart();

        System.out.println("total var of petal:"+ calcTotalVariance(petalPointsWithEuclidanNorm,petalAvgPoint) );

        HashMap petalX = calcFrequencyOfListX(petalPointsWithEuclidanNorm);


//        HashMap petalY = calcFrequencyOfListY(petalPointsWithEuclidanNorm);
//        HashMap sepalX = calcFrequencyOfListX(sepalPointsWithEuclidanNorm);
//        HashMap sepalY = calcFrequencyOfListY(sepalPointsWithEuclidanNorm);


//       double [] petalKeySetX = new double[petalX.keySet().size()];
//        for (int j = 0; j <petalX.keySet().size() ; j++) {
//
//        }
//       double [] petalValSetX = new  double[petalX.values().size()];
//        XYChart petalXFrequency = new XYChartBuilder().width(600).height(500).title("Sepal width and length").xAxisTitle("X - width").yAxisTitle("Y - length").build();

//        XYChart sepalChart = new XYChartBuilder().width(600).height(500).title("Sepal width and length").xAxisTitle("X - width").yAxisTitle("Y - length").build();
//        XYSeries sepalSeries = petalChart.addSeries("Sepal Average", Collections.singletonList(sepalAvgPoint.getxPoint()), Collections.singletonList(sepalAvgPoint.getyPoint()));

//        sepalSeries.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Scatter);
//        sepalSeries.getStyler().setChartTitleVisible(false);
//        sepalSeries.getStyler().setLegendPosition(Styler.LegendPosition.InsideNW);
//        sepalSeries.getStyler().setMarkerSize(16);





//
//

//        System.out.println("petal  x"+calcFrequencyOfListX(petalPointsWithEuclidanNorm));
//        System.out.println("peta y"+calcFrequencyOfListY(petalPointsWithEuclidanNorm));
//        new SwingWrapper(petalChart).displayChart();
//        new SwingWrapper(petalXFrequency).displayChart();
//


    }

    private  static HashMap<Double,Double>  calcFrequencyOfListX(List<Point> points){
        HashMap  freqOfvales = new HashMap<Double,Integer>();

        for (Point p :points) {
            if(freqOfvales.containsKey(p.getxPoint()))
                {
                    freqOfvales.put(p.getxPoint(), 0.0);
                }
            freqOfvales.put(p.getxPoint(),calcFrequencyX(points,p.getxPoint()));

        }

        return  freqOfvales;
    }

    private static HashMap<Double,Integer> calcFrequencyOfListY(List<Point> points){
        HashMap  freqOfvales = new HashMap<Double,Integer>();

        for (Point p :points) {
            if(freqOfvales.containsKey(p.getyPoint()))
            {
                freqOfvales.put(p.getyPoint(), 0);
            }
            freqOfvales.put(p.getyPoint(),calcFrequencyY(points,p.getyPoint()));

        }
        return  freqOfvales;
    }

    private static Double calcFrequencyX(List <Point> points, Double amount){
        Double freq = 0.0 ;
        for ( Point p :points) {
            if(p.getxPoint().equals(amount)){
                freq++;
            }
        }

        return  freq;
    }


    private static int calcFrequencyY(List <Point> points, double amount){
        int freq = 0 ;
        for ( Point p :points) {
            if(p.getyPoint().equals(amount)){
                freq++;
            }
        }

        return  freq;
    }



    private static double calcTotalVariance (List <Point> points, Point avgPoint ){
        double totalAmount = 0;
        for (Point p:points) {
             totalAmount += Math.abs(Math.pow(calculateEuclidanDistance(p.getxPoint(),avgPoint.getxPoint(),p.getyPoint(), avgPoint.getyPoint()),2));
        }
      return  totalAmount/points.size();

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
        System.out.println(bestMatchvalue);
        return bestMatchPoint;
    }

    private static Point findBestMatchOfCosineSimilarityForPoints(List<Point> points, Point pointToMatch) {
        Point bestPointToMatch = new Point();
        double bestMatchValue = -1;
        for (Point actualPoint : points) {
            System.out.println("Cosine similarity with avg  "+ calculateCosineSimilarity(actualPoint,pointToMatch));
            if (Math.abs(calculateCosineSimilarity(pointToMatch, actualPoint)) >= bestMatchValue) {
                bestMatchValue = Math.abs(calculateCosineSimilarity(pointToMatch, actualPoint));
                bestPointToMatch = actualPoint;
            }
//            System.out.println("bestMaatch value : " + bestMatchValue);

        }
//        System.out.println("bestMaatch value : " + bestMatchValue);
        return bestPointToMatch;
    }


}
