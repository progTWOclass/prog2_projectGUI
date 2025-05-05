// -------------------------------------------------------
// Final Project
// Written by: Steve Banh 1971537
// For “Programming 2” Section 02 – Winter 2025
// --------------------------------------------------------
/**
 * This is a custom class that will print a message informing the user that they do not have
 * a spending limit set for their selected category
 * */
package files.project_prog2_javafx.Exceptions;

public class SpendingLimitNotFoundException extends Exception{

    //CONSTRUCTOR
    //default
    public SpendingLimitNotFoundException(){
        super();
    }
    //parameterized
    public SpendingLimitNotFoundException(String message){
        super(message);
    }
}
