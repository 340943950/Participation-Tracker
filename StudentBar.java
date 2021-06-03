import javax.swing.*;
import java.awt.*;

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
	private JPanel parentPanel;
    public JLabel studentName;
	public JLabel studentPointsLabel;
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
    StudentBar(String studentName, JPanel parentPanel)
	{

        // Initialize all the widgets
        studentOptionBox = Box.createHorizontalBox();
		studentPointsLabel = new JLabel();
        this.studentName = new JLabel();
		this.parentPanel = parentPanel;
        plusButton = new JButton();
        minusButton = new JButton();     
        
        // Set text for label and buttons
        this.studentName.setText(studentName);
		this.studentName.setPreferredSize(new Dimension(200, this.studentName.getY()));
		studentPointsLabel.setText("Not Set");
        plusButton.setText("+");
        minusButton.setText("-");       
        
        // Add the components to studentOptionBox
        studentOptionBox.add(this.studentName);
        studentOptionBox.add(Box.createHorizontalGlue());
		studentOptionBox.add(studentPointsLabel);
		studentOptionBox.add(Box.createHorizontalGlue());
        studentOptionBox.add(plusButton);
        studentOptionBox.add(minusButton);
        
        // Add to parent panel and update for changes
        this.parentPanel.add(studentOptionBox);
        studentOptionBox.revalidate();
        this.parentPanel.revalidate();
    }

	/** 
	 * Edit points value for the student.
	 * <p>
	 * The reason why this was created instead of just editing the label is because
	 * the both the student bar needs to be revalidated
	 * <p>
	 * @param newPoints The new points system to change it to
	 */
	public void editPointsValue(float newPoints) 
	{
		studentPointsLabel.setText(Float.toString(newPoints));
		studentOptionBox.revalidate();
	}

	/**
	 * Remove the component from the parent panel
	 */
	public void hideStudentBar()
	{
		parentPanel.remove(studentOptionBox);
		parentPanel.revalidate();
	}
}
