import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;

public class GraphBuilder extends Application{
	public static String filePath = "data_POINTS.csv";

	/**
	 * This method reads the file and stores the information into an ArrayList.
	 * 
	 * @author Ethne Au
	 * @return points   An ArrayList containing the information from the loaded file
	 * @throws FileNotFoundException
	 */
	public static ArrayList<String> pointsFile() throws FileNotFoundException {
		ArrayList<String> points = new ArrayList<String>();

		File file = new File(filePath);
		Scanner reader = new Scanner(file);

		// reads file and stores data into an ArrayList
		while(reader.hasNextLine()) {
			String name = reader.nextLine();
			points.add(name);
		}

		// removes 1st row in the ArrayList (Dates, names)
		points.remove(0);

		reader.close();
		return points; 
	}
	/**
	 * This method calculates the total points the class has earned per day.
	 * 
	 * @author Ethne Au
	 * @return arrlist  ArrayList containing the total number of points earned per day.
	 * @throws FileNotFoundException
	 */
	public static ArrayList<Number> totalPoints() throws FileNotFoundException{
		File file = new File(filePath);   
		Scanner reader = new Scanner(file);

		String delimiter = ",";
		String [] testStudent = new String[0];
		ArrayList<Number> arrlist = new ArrayList<Number>();

		// skips first line in file
		reader.nextLine();

		// reads file and stores into an ArrayList
		while (reader.hasNextLine()){
			String line = reader.nextLine();
			testStudent = line.split(delimiter);
			int sum = 0;
			arrlist.add(sumOfPoints(testStudent, sum));
		}

		reader.close();
		return arrlist;
	}
	/**
	 * This method adds the number of points from every student to a total per day.
	 * 
	 * @author Ethne Au
	 * @param testStudent Array containing the points
	 * @param sum Sum of the points
	 * @return sum  The total number of points in a day
	 */
	public static int sumOfPoints(String[] testStudent, int sum){
		// adds points from each day
		for (int i = 1; i < testStudent.length; i++){
			int point = Integer.parseInt(testStudent[i]);
			sum += point;
		}
		return sum;
	}
	/**
	 * This method separates the dates from the points and puts the dates into an ArrayList.
	 * 
	 * @author Ethne Au
	 * @param dates An empty ArrayList
	 * @return dates ArrayList containing all the dates
	 * @throws FileNotFoundException
	 */
	public static ArrayList<String> datesIntoArray(ArrayList<String> dates) throws FileNotFoundException{   
		ArrayList<String> points = pointsFile();
		int length = points.size();
		String date = "";

		// separates dates from the rest of the data in the file
		for(int i = 0; i < length; i++){
			date = points.get(i);
			points.add(date.substring(0,5));
		}

		// adds dates to array list
		for(int i = length; i < length*2; i++){
			dates.add(points.get(i));
		}  
		return dates;
	}
	/**
	 * This method creates the graph.
	 * 
	 * @author Ethne Au
	 * @param stage creates a stage to show the created graph
	 * @throws IOException
	 */
	@Override
	public void start(Stage stage) throws IOException{
		ArrayList<Number> averagePoints = new ArrayList<Number>();
		averagePoints = totalPoints();

		// gets dates from file
		ArrayList<String> dates = new ArrayList<String>();
		dates = datesIntoArray(dates);


		stage.setTitle("Class Participation Graph");

		// creates axis (x and y)
		CategoryAxis xAxis = new CategoryAxis();
		xAxis.setLabel("Date");

		NumberAxis yAxis = new NumberAxis();
		yAxis.setLabel("Total Class Points");

		LineChart<String,Number> linechart = new LineChart<String,Number>(xAxis, yAxis); // creates linechart

		linechart.setTitle("Class Participation"); // title of graph

		XYChart.Series<String, Number> series = new XYChart.Series<String, Number>();
		series.setName("Class Points");

		for (int i = 0; i < dates.size(); i++){
			XYChart.Data<String, Number> data = new XYChart.Data<String, Number>(dates.get(i), (Number)averagePoints.get(i));
			series.getData().add(data); // points on the graph
		}

		Scene scene  = new Scene(linechart,800,800);
		linechart.getData().add(series); // adds points to the graph

		stage.setScene(scene);

		// stage dimensions
		stage.setHeight(500);
		stage.setWidth(500);

		stage.show(); // outputs graph
	}
}
