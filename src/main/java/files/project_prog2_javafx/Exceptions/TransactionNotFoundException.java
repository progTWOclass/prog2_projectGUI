package files.project_prog2_javafx.Exceptions;

// -------------------------------------------------------
// Final Project
// Written by: (include your name and student id)
// For “Programming 2” Section (include number)– Winter 2025
// --------------------------------------------------------
public class TransactionNotFoundException extends Exception{

    //CONSTRUCTOR
    //default
    public TransactionNotFoundException(){
        super();
    }

    //parameterized
    public TransactionNotFoundException(String message){
        super(message);
    }
}
