/*
 * Date: May 19, 2021
 * Name: Adarsh Padalia, Ethne Au, Iza Kurbanova, and Vaughn Chan
 * Teacher: Mr. Ho
 * Description: Tracking participation in a classroom setting
 * */

import java.io.IOException;
import java.io.File;
import java.io.FileNotFoundException;
import javax.swing.*;
import javax.swing.filechooser.*;
import java.util.Scanner;
import java.util.ArrayList;
import java.io.FileWriter;

 public class ParticipationTracker {
    public static void main(String[] args) throws IOException, FileNotFoundException {
        
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
        
        JFileChooser fc = new JFileChooser();
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

    /**
     * This method takes in the path to the file containing the recent files data and puts the data into 
     * an array list
     * 
     * @param filePath      The path to the file containing the list of recent files
     * @return recentFiles  A list of the files that the user recently used
     */
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
        // Adds the new element at the end of the array lsit
        arrList.add(newElement);

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
 }
