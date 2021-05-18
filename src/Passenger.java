public class Passenger implements java.io.Serializable{
    private String firstName;
    private int secondsInQueue;
    private int seatNumber;

    public String getFirstName(){
        return firstName ;
    }

    public void setFirstName(String passengerName){
        this.firstName=passengerName;
    }

    public int getSeatNumber(){
        return seatNumber;
    }

    public void setSeatNumber(int seatNumber) {
        this.seatNumber = seatNumber;
    }

    public int getSecondsInQueue(){
        return secondsInQueue;
    }

    public void setSecondsInQueue(int randomNum_one, int randomNum_two, int randomNum_three){
        this.secondsInQueue = randomNum_one + randomNum_two + randomNum_three;
    }

}
