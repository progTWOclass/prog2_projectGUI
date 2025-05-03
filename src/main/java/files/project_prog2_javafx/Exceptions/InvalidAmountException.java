// -------------------------------------------------------
// Final Project
// Written by: (include your name and student id)
// For “Programming 2” Section (include number)– Winter 2025
// --------------------------------------------------------
package files.project_prog2_javafx.Exceptions;

public class InvalidAmountException extends Exception{

    //CONSTRUCTOR
    //default
    public InvalidAmountException(){
        super();
    }

    //parameterized
    public InvalidAmountException(String message){
        super(message);
    }
}
