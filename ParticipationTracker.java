/*
 * Date: May 19, 2021
 * Name: Adarsh Padalia, Ethne Au, Iza Kurbanova, and Vaughn Chan
 * Teacher: Mr. Ho
 * Description: Tracking participation in a classroom setting
 * */

import java.awt.event.ActionEvent;
import java.awt.Component;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Scanner;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

public class ParticipationTracker extends GUILayout {
    public static String[] names = new String[0];
    public static int[] points = new int[0];
    public static String classListFile;
    
    /**
     * The main method for where code should run
     * 
     * @param args the command line arguments
     */
    public static void main(String args[]) throws IOException {
        // Startup
        String textFileName = "RecentFiles.txt";
        File file = new File(textFileName);
        // Creates file if it doesn't already exist
        file.createNewFile();
        ArrayList<String> recentFiles = fileToArrayList(textFileName);

	/* Set the Nimbus look and feel */
	//<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
	/* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
	try {
	    for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
		    if ("Nimbus".equals(info.getName())) {
                javax.swing.UIManager.setLookAndFeel(info.getClassName());
                break;
		    }
	    }
	} catch (ClassNotFoundException ex) {
	    java.util.logging.Logger.getLogger(GUILayout.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
	} catch (InstantiationException ex) {
	    java.util.logging.Logger.getLogger(GUILayout.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
	} catch (IllegalAccessException ex) {
	    java.util.logging.Logger.getLogger(GUILayout.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
	} catch (javax.swing.UnsupportedLookAndFeelException ex) {
	    java.util.logging.Logger.getLogger(GUILayout.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
	}
    //</editor-fold>
    
    // Intialize Student Bars
    ArrayList<StudentBar> studentBars = new ArrayList<StudentBar>();

	// Initialize the GUI
	GUILayout gui = new GUILayout();
	java.awt.EventQueue.invokeLater(new Runnable() {
		public void run() {
		    gui.setVisible(true);
		}
	});
        
    // Event handler for Populate
    gui.PopulateStudentBarMenu.addActionListener((ActionEvent ev) -> {
        ArrayList<String> tempOptions = new ArrayList<String>(recentFiles.size());
        for (String element : recentFiles) {
            tempOptions.add(element);
        }
        tempOptions.add("Other");
        String[] options = arrListToArr(tempOptions);

        String choice = dropDownDialogBox(options, "Which file would you like to open: ", "Recent Files");
        if (choice.equals("Other")) {
            classListFile = getFilePath("CSV File", "csv");
        }
        else {
            classListFile = choice;
        }

        if (!classListFile.equals("")) {
            updateRecentFiles(recentFiles, classListFile, 5);
        }

        try {
            String[] tempNames = getClassList(classListFile);
            int[] tempPoints = new int[tempNames.length];

            if (Collections.disjoint(Arrays.asList(names), Arrays.asList(tempNames))) {
                names = concatStrArrs(names, tempNames);
                points = concatIntArrs(points, tempPoints);

                for (String studentName : tempNames) {
                    studentBars.add(new StudentBar(studentName, gui.StudentListPanel));
                    createPlusMinusListeners(studentBars, names, points);
                }
            }
            else {
                JOptionPane.showMessageDialog(null, "ERROR: Overlapping elements in old class list and new class list", "Error", JOptionPane.ERROR_MESSAGE);
            }            

			// TEST CODE - Inittialize points
			studentBars.forEach((studentBar) -> {
                studentBar.editPointsValue(0.0f);
			});

        }
        catch (FileNotFoundException e) {
            // Leave names and points blank
        }
    });
    
    // Event handler for Points Menu
    gui.SavePointsMenu.addActionListener((ActionEvent ev) -> {
            // Add (POINTS) to the name of the class list file
            String filePath = classListFile.substring(0, classListFile.length() - 4) + "_POINTS.csv";
            try {
                writePointsToFile(filePath, names, points);
            }
            catch (IOException e) {
                // Nothing is written to the file
            }
    });

    // Runs this code when the application is closed
    Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
        public void run() {
            // Save the user's recently used files to a text file
            try {
                arrListToFile(recentFiles, textFileName);
            }
            catch (IOException e) {
                // Doesn't change RecentFiles.txt
            }
        }
    }, "Shutdown-thread"));

		
    /* IZABEL CODE --
	String classListFile = ("./ClassListTemplate.csv"); // When integrating replace this with the file input from Adarshes code
	String pointsFile = ("./Points-Example.csv"); // When integrating replace with points file

	String[] classList = getClassList(classListFile); // Array with first and last name of every student
    String[] classInitials = getInitials(classListFile); // Array with initials of every student for display purposes
    */

	// double[] studentAverages = getAverage(pointsFile); // Array with average point value for each student
	// double[] studentPercentiles = getPercentile(studentAverages); // Array with percentile for each student
	// String[] studentNSGE = getNSGE(studentPercentiles); // Array with letter grade for each student

	// String participationResultsFile = classListFile.replaceAll(".csv", "_ParticipationMarks.csv"); // File name of output file with name and letter grade of each student
	// printNSGEFile(participationResultsFile, classList, studentNSGE); // File containing name and participation mark of each student 
    }

    /**
     * This method updates the points that students have based on which button the user clicked
     * 
     * @param studentBars   The array list containing all the student bars
     * @param names         The names of all the students (corresponds to points)
     * @param points        The points of all the students (corresponds to names)
     */
    public static void createPlusMinusListeners(ArrayList<StudentBar> studentBars, String[] names, int[] points) {
        // Gets the last StudentBar added to the studentBars array list and creates two action 
        // listeners for it (one for the plus button and another for the minus button)
        StudentBar studentBar = studentBars.get(studentBars.size()-1);
        studentBar.plusButton.addActionListener((ActionEvent ev) -> {
            String name = studentBar.studentName.getText();
            updatePoints(name, 1, names, points);

            int newPoints = 0;
            for (int i = 0; i < names.length; i++) {
                if (name.equals(names[i])) {
                    newPoints = points[i];
                    break;
                }
            }
            studentBar.editPointsValue(newPoints);
        });
        studentBar.minusButton.addActionListener((ActionEvent ev) -> {
            String name = studentBar.studentName.getText();
            updatePoints(name, -1, names, points);
            
            int newPoints = 0;
            for (int i = 0; i < names.length; i++) {
                if (name.equals(names[i])) {
                    newPoints = points[i];
                    break;
                }
            }
            studentBar.editPointsValue(newPoints);
        });
    }

    /**
     * This method takes in the user inputed file and reads it to make an array with all the student names
     * 
     * @param fileName       The file name and location
     * @return classList     An array containing the names of every student in that class
     * @throws FileNotFoundException
     */
    public static String[] getClassList(String fileName) throws FileNotFoundException{
        File classFile = new File(fileName);  
        ArrayList<String> studentNames = new ArrayList<String>(); // Create an array list because we do not know the number of students
        try {
            BufferedReader reader = new BufferedReader(new FileReader(classFile)); // Get the file to read
            String str = reader.readLine(); // Read the first line to disriard the title

            while(str != null){ // Loop to read every line
                str = reader.readLine(); // Reading the line
                
                if(str != null){
                    String[] parts = str.split(","); // Seperates the line using at every comma
                    String firstName = parts[1]; // Finds the first name which is the second part
                    String lastName = parts[2]; // Finds the last name which is the third part
                    String name = (firstName + " " + lastName); // Puts first and last names together
                    studentNames.add(name); // Adds the name into the array list
                }
                
            }
            reader.close();
        }
        catch(Exception e) {
            // Incase an error occures, don't edit studentNames
        }

        String[] classList = new String[studentNames.size()]; // Creating an array to covert the array list into it

        for(int i = 0; i<studentNames.size(); i++){
            classList[i] = studentNames.get(i); // Converting the array list into the array classList
        }

        return classList; 
    }
    /**
     * This method takes in the user inputed file and reads it to make an array with all the student initials
     * 
     * @param fileName        The file name and location
     * @return classInitials  An array containing the initials of every student in that class
     */
    public static String[] getInitials(String fileName){
        File classFile = new File(fileName);  
        ArrayList<String> studentInitials = new ArrayList<String>(); // Create an array list because we do not know the number of students
        try {
            BufferedReader reader = new BufferedReader(new FileReader(classFile)); // Get the file to read
            String str = reader.readLine(); // Read the first line to disriard the title

            while(str != null){ // Loop to read every line
                str = reader.readLine(); // Reading the line
                
                if(str != null){
                    String[] parts = str.split(","); // Seperates the line using the comma
                    char firstInitial = parts[1].charAt(0); // Finds the first letter in the second part (first name)
                    char lastInitial = parts[2].charAt(0); // Finds the first letter in the third part (last name)
                    String Initials = (firstInitial + ". " + lastInitial + "."); // Puts the initials together
                    studentInitials.add(Initials); // Adds the initials into the array list
                }
                
            }
            reader.close();
        }
        catch(Exception e){ // Incase an error occures, informs the user of the error and quits the program
            System.out.println("An error has occured please try again");
            System.out.println(e);
            System.exit(-1);
        }

        String[] classInitials = new String[studentInitials.size()]; // Creating an array to covert the array list into it

        for(int i = 0; i<studentInitials.size(); i++){
            classInitials[i] = studentInitials.get(i); // Converting the array list into the array classInitials
        }

        return classInitials; 
    }

    /**
     * This method takes in the user inputed file contaning the daily points of each student and gets the average points value for each student
     * 
     * @param fileName     The file name and location
     * @return averages    An array containing the averages of every student in that class
     */
    public static double[] getAverage(String fileName){
        File pointsFile = new File(fileName);  
        double[] averages = new double[1];
        try {
            BufferedReader reader = new BufferedReader(new FileReader(pointsFile)); // Get the file to read
            String str = reader.readLine(); // Read the first line to disriard the title

            String[] studentParts = str.split(","); // Making an array where every point from that day is a element
            int[] total = new int[studentParts.length - 1]; // Making new array to hold the student points sum
            int count = 0; // Counts the total number of days where points where given

            while(str != null){ // Loop to read every line
                str = reader.readLine(); // Reading the line
                
                if(str != null){
                    String[] parts = str.split(","); // Seperates the line using the comma

                    for(int i = 1; i<parts.length; i++){
                        int point = Integer.parseInt(parts[i]); // Turns the point from string into integer
                        total[i-1] += point; // Adds the points for one student together
                    }
                    count++;
                }
            }
            reader.close();

            averages = new double[total.length]; // Making an array for the averages

            for(int i = 0; i<total.length; i++){
                averages[i] = (double)total[i]/(double)count; // Calculating average for each student by dividing the sum of al points by the number of points
            }
        }
        catch(Exception e){ // Incase an error occures, informs the user of the error and quits the program
            System.out.println("An error has occured please try again");
            System.out.println(e);
            System.exit(-1);
        }

        return averages;
    }

    /**
     * This method takes in the student averages array and finds the percentile of each average
     * 
     * @param studentAverages   Array containing the average point value for each student
     * @return percentiles      An array containing the percentile of the average of each student
     */
    public static double[] getPercentile(double[] studentAverages){
        bubbleSort(studentAverages); // Sorting the array from smallest to biggest
        double[] percentiles = new double[studentAverages.length]; // Making array containing the percentiles

        for(int i = 0; i<studentAverages.length; i++){
            int below = 0;

            for(int j = 0; j<i; j++){

                if(studentAverages[j] < studentAverages[i]){
		    below++; // Finding how many numbers are below the current number
                }
            }
            percentiles[i] = (double)below/(double)(studentAverages.length)*100.0; // Calculating the percentile for the average of each student
        }

        return percentiles;
    }

    /**
     * This method organises an array from smallest to biggest
     * 
     * @param arr    The array you want to organize
     */
    public static void bubbleSort(double[] arr){
        double temp;
        for(int i = 0; i < arr.length -1;i++){

            for(int j = 0; j<arr.length-i-1;j++){
                
                // Responsible for swaping elements in array (if left is > right swap)
                if (arr[j] > arr[j+1]){
                    temp = arr[j]; // Holds value on the left
                    arr[j] = arr[j+1]; // Overwrite the left element with the right
                    arr[j+1] = temp; // Place the left element to the right
                }
            }
        }
    }

    /**
     * This method takes in the student percentiles array and assignes a letter grade (NSGE) to reflect the students participation
     * 
     * @param studentPercentiles   Array containing the percentile value for each student
     * @return studentNSGE         An array containing the letter grade of each student
     */
    public static String[] getNSGE(double[] studentPercentiles){
        String[] studentNSGE = new String[studentPercentiles.length];

        for(int i = 0; i<studentPercentiles.length; i++){

            if(studentPercentiles[i]>=0 && studentPercentiles[i] < 15){
                studentNSGE[i] = (String)("N"); // If the students percentile is below 15 they get N
            }

            else if(studentPercentiles[i]>=15 && studentPercentiles[i] < 45){
                studentNSGE[i] = (String)("S"); // If the students percentile is between 15 and 45 they get S
            }

            else if(studentPercentiles[i]>=45 && studentPercentiles[i] < 85){
                studentNSGE[i] = (String)("G"); // If the students percentile is between 45 and 85 they get G
            }

            else if(studentPercentiles[i]>=85 && studentPercentiles[i] < 100){
                studentNSGE[i] = (String)("E"); // If the students percentile is between 85 and 100 they get E
            }

            else{
                studentNSGE[i] = (String)("Undefined"); // If something has gone wrong during the calculation prosses the student will get a undefined grade
            }
        }

        return studentNSGE;

    }

    /**
     * This method takes in the class list and letter grades, and outputs a file with the student name and their participation mark
     * 
     * @param fileName   Name and location of the results file
     * @param classList   Array containing the names of every student
     * @param studentNSGE   Array containing the participation mark (NSGE) of every student
     */
    public static void printNSGEFile(String fileName, String[] classList, String[] studentNSGE){
        try{
            FileWriter myWriter = new FileWriter(fileName); // Creats the file
            BufferedWriter bw = new BufferedWriter(myWriter);
            PrintWriter pw = new PrintWriter(bw);
            pw.println("Student,Participation Mark"); // Prints the title into the file
            
            for(int i=1; i<classList.length+1; i++){
                pw.println(classList[i-1] + "," + studentNSGE[i-1]); // Prints every student name followed by their grade
            }
            
            pw.flush();
            pw.close();
            System.out.println ("Successfully wrote in the file"); // Informs the user of successful completion
        }
        catch(Exception e){ // Incase an error occures, informs the user of the error and quits the program
            System.out.println("An error has occured please try again");
            System.out.println(e);
            System.exit(-1);
        }
    }

    /**
     * This method can open a file explorer pop-up that finds the path of a selected file (returns an 
     * empty string if the user didn't select a file)
     * 
     * The filetype can also be restricted so that only files of that file type show up (the file 
     * description is not used if fileType is left blank)
     * 
     * @param fileDescription   Description of the restricted file type (e.g. "CSV Files")
     * @param fileType          Restrict the filetypes (csv, txt, etc.) shown in the file explorer pop-up
     * @return fileName         The path of the file that the user selected
     */
    public static String getFilePath(String fileDescription, String fileType) {        
        if (fileDescription.equals("")) {
            fileDescription = fileType;
        }
        
        // Creates a file chooser that opens the directory that this java file is in
        JFileChooser fc = new JFileChooser(new File(System.getProperty("user.dir")));
        if (!fileType.equals("")) {
            FileFilter filter = new FileNameExtensionFilter(fileDescription, fileType);
            fc.setFileFilter(filter);
        }
        int response = fc.showOpenDialog(null);

        String filePath;
        if (response == JFileChooser.APPROVE_OPTION) {
            filePath = fc.getSelectedFile().toString();
        }
        else if (response == JFileChooser.ERROR_OPTION) {
            filePath = "";
        }
        else {
            filePath = "";
        }
        return filePath;
    }

    
    /**
     * This method creates a generic drop down dialog box and returns the user's choice
     * 
     * @param options   The set of options from which the user can choose
     * @param prompt    The text that prompts the user
     * @param title     The title at the top of the dialog box
     * @return choice   The option that the user chose (returns empty string if user didn't choose an option)
     */
    public static String dropDownDialogBox(String[] options, String prompt, String title) {
        String choice;
        try {
            choice = JOptionPane.showInputDialog(null, prompt, title, JOptionPane.PLAIN_MESSAGE, null, options, "").toString();
        }
        catch (Exception e) {
            choice = "";
        }
        return choice;
    }

    public static ArrayList<String> fileToArrayList(String filePath) {
        ArrayList<String> list = new ArrayList<String>();
        try {
            File file = new File(filePath);
            Scanner reader = new Scanner(file);
            while (reader.hasNextLine()) {
                String line = reader.nextLine();
                list.add(line);
            }
            reader.close();
        }
        catch (FileNotFoundException e) {
            // File was not found so return an empty array list
        }
        return list;
    }

    /**
     * Converts a string array list into a string array
     * 
     * @param arrList   The array list to be converted
     * @return arr      The array that the array ist is converted into
     */
    public static String[] arrListToArr(ArrayList<String> arrList) {
        String[] arr = new String[arrList.size()];
        arr = arrList.toArray(arr);
        return arr;
    }

    /**
     * This method adds an element to an array list and deletes the first element if there are too many elements
     * 
     * @param arrList       The array list to be updated
     * @param newElement    The new element to be added to the array list
     * @param maxToKeep     The maximum number of elements to keep (set to -1 to keep infinite elements)
     */
    public static void updateRecentFiles(ArrayList<String> arrList, String newElement, int maxToKeep) {
        // Adds the new element at the end of the array list
        boolean unique = true;
        for (String element : arrList) {
            if (element.equals(newElement)) {
                unique = false;
            }
        }
        if (unique) {
            arrList.add(newElement);
        }

        while (arrList.size() > maxToKeep && maxToKeep != -1) {
            // If there are more elements than should be kept then delete the first element (like queue data structure)
            arrList.remove(0);
        } 
    }

    /**
     * This method writes an array list to a file
     * 
     * @param arrList       The array list to write into the file
     * @param fileName      The file to write the array list to
     * @throws IOException  Signals that the input or output has failed
     */
    public static void arrListToFile(ArrayList<String> arrList, String fileName) throws IOException {
        File file = new File(fileName);
        file.createNewFile();
        
        FileWriter writer = new FileWriter(fileName);
        for (int i = 0; i < arrList.size(); i++) {
            writer.write(arrList.get(i));
            if (i != arrList.size() - 1) {
                writer.write("\n");
            }
        }
        writer.close();
    }

    /**
     * This method assigns chat points based on how many times a student participated in the chat
     * 
     * @param names     The names of the students (index corresponds to that of points)
     * @param points    The points each student has on the current date (index corresponds to that of points)
     * @param filePath  The path to the file with the chat data
     * @throws FileNotFoundException
     */
    public static void assignChatPoints(String[] names, int[] points, String filePath) throws FileNotFoundException {
        File file = new File(filePath);
        Scanner reader = new Scanner(file);

        int lineNumber = 0;
        char charToFind = ':';
        String lastName = "";   // The last name that was read in

        // Read each line in file
        while (reader.hasNextLine()) {
            lineNumber += 1;
            String line = reader.nextLine();
            // Only check for a name if the lineNumber % 3 == 2 (i.e. 2, 5, 8, 11, 14, etc.)
            // These are the lines with the names
            if (lineNumber % 3 == 2) {
                // Find the position of the colon in the line
                int colonIndex = -1;    // Default value of -1
                for (int i = 0; i < line.length(); i++) {
                    if (line.charAt(i) == charToFind) {
                        colonIndex = i;
                        break;
                    }
                }

                // Find name by looking at string before the colon
                String name = line.substring(0, colonIndex);
                for (int i = 0; i < names.length; i++) {
                    // If same person puts something in the chat multiple times in a row, only one point is given
                    if (names[i].equals(name) && !name.equals(lastName)) {
                        points[i] += 1;
                    }
                }
                // Update the lastName to be the current name
                lastName = name;
            }
        }
        reader.close();
    }

    /**
     * This method writes the points to an existing csv file or creates a csv file if it doesn't already exist
     * 
     * @param names     The names of the students (index corresponds to that of points)
     * @param points    The points each student has on the current date (index corresponds to that of points)
     * @param filePath  The path to the file with the old points data
     * @throws IOException
     */
    public static void writePointsToFile(String filePath, String[] names, int[] points) throws IOException, FileNotFoundException {
        File file = new File(filePath);
        String[] headers;
        // File doesn't exist but was created
        if (file.createNewFile()) {
            headers = names;
            FileWriter writer = new FileWriter(file);
            writer.append("Date");

            for (int i = 0; i < headers.length; i++) {
                writer.append("," + headers[i]);
            }
            writer.close();
        }
        // File already exists
        else {
            Scanner reader = new Scanner(file);
            String[] tempHeaders = reader.nextLine().split(",");
            headers = Arrays.copyOfRange(tempHeaders, 1, tempHeaders.length);
            reader.close();
        }
        
        // Last argument controls whether or not the data is appended to the end (if false, then overwrites)
        FileWriter writer = new FileWriter(file, true);
        
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat simpleformat = new SimpleDateFormat("MM/dd/yyyy");
        String currDate = simpleformat.format(cal.getTime());

        writer.append("\n" + currDate);
        int nameIndex;
        for (int i = 0; i < headers.length; i++) {
            nameIndex = -1; // Default value
            for (int j = 0; j < names.length; j++) {
                if (headers[i].equals(names[j])) {
                    nameIndex = j;
                    break;
                }
            }

            if (nameIndex != -1) {
                writer.append("," + Integer.toString(points[nameIndex]));
            }
            else {
                writer.append(",0");    // If name can't be found, assume they got 0 points
            }
        }
        writer.close();
    }

    /**
     * This method updates the points that a certain student has
     * 
     * @param targetName    The name of the student whose points to change
     * @param pointChange   The amount by which to change the student's points
     * @param names         The names of all the students (corresponds to points)
     * @param points        The points of all the students (corresponds to names)
     */
    public static void updatePoints(String targetName, int pointChange, String[] names, int[] points) {
        int index = 0;
        for (int i = 0; i < names.length; i++) {
            if (names[i].equals(targetName)) {
                index = i;
                break;
            }
        }
        points[index] += pointChange;
    }

    /**
     * This method concatenates two string arrays
     * 
     * @param a     The first string array
     * @param b     The second string array
     * @return c    The concatenated array
     */
    public static String[] concatStrArrs(String[] a, String[] b) {
        int aLen = a.length;
        int bLen = b.length;

        String[] c;
        if (a.length == 0) {
            c = Arrays.copyOf(b, bLen);
        }
        else if (b.length == 0) {
            c = Arrays.copyOf(a, aLen);
        }
        else {
            c = Arrays.copyOf(a, aLen + bLen);
            for (int i = 0; i < b.length; i++) {
                c[i + aLen] = b[i];
            }
        }

        return c;
    }

    /**
     * This method concatenates two integer arrays
     * 
     * @param a     The first integer array
     * @param b     The second integer array
     * @return c    The concatenated array
     */
    public static int[] concatIntArrs(int[] a, int[] b) {
        int aLen = a.length;
        int bLen = b.length;

        int[] c;
        if (a.length == 0) {
            c = Arrays.copyOf(b, bLen);
        }
        else if (b.length == 0) {
            c = Arrays.copyOf(a, aLen);
        }
        else {
            c = Arrays.copyOf(a, aLen + bLen);
            for (int i = 0; i < b.length; i++) {
                c[i + aLen] = b[i];
        }
        }

        return c;
    }
}