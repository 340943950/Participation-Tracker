/*
 * Date: May 19, 2021
 * Name: Adarsh Padalia, Ethne Au, Iza Kurbanova, and Vaughn Chan
 * Teacher: Mr. Ho
 * Description: Tracking participation in a classroom setting
 * */

import java.io.IOException;
import javax.swing.*;
import javax.swing.filechooser.*;

 public class ParticipationTracker {
    public static void main(String[] args) throws IOException {
        
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
        if (!fileDescription.equals("")) {
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
 }
