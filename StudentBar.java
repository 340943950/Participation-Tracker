import javax.swing.*;

/**
 * A class representing a single box entity with a label and two
 * buttons appended to a panel.
 * <p>
 * The ultimate purpose is to make a container that holds a label and two buttons. Each
 * container, then, would represent a single student.
 * 
 * It should be noted that you can reference the swing widgets (such as JButton) and 
 * override their respective action event methods.
 * <p>
 * @author Vaughn Chan
 * @version 0.1
 */
public class StudentBar {
    
    private Box studentOptionBox;
    public JLabel studentName;
    public JButton plusButton;
    public JButton minusButton;
    
    /**
     * Constructor for a StudentBar.
     * <p>
     * When a StudentBar is initialized, it needs to initialize all the widgets, add 
     * widget components to a box, which then is added to its parent panel. 
     * <p>
     * @param studentName The name of the student
     * @param parentPanel The panel this box will be placed in
     */
    StudentBar(String studentName, JPanel parentPanel)  {
        
        // Initialize all the widgets
        studentOptionBox = new Box(2);
        this.studentName = new JLabel();
        plusButton = new JButton();
        minusButton = new JButton();
        
        // Set text for label and buttons
        this.studentName.setText(studentName);
        plusButton.setText("+");
        minusButton.setText("-");
        
        // Add the components to studentOptionBox
        studentOptionBox.add(this.studentName);
        studentOptionBox.add(plusButton);
        studentOptionBox.add(minusButton);
        
        // Add to parent panel and update for changes
        parentPanel.add(studentOptionBox);
        studentOptionBox.revalidate();
        parentPanel.revalidate();
    }
}