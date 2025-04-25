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
