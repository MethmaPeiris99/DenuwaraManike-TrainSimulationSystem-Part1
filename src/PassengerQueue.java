public class PassengerQueue implements java.io.Serializable{
    static final int MAXIMUM_SEATS = 42;

    private Passenger[] queueArray = new Passenger[MAXIMUM_SEATS]; //used to store train queue objects
    private int first; //represents the first object in train queue
    private int last; //represents the last object in train queue
    private int maxStayInQueue; //represents the waiting time of all passengers in train queue
    private int maxLength; //represents number of passengers in train queue

    public PassengerQueue(){
        first = -1;
        last = -1;
        maxLength = 0;
    }

    public void add(Passenger next){
        if(isEmpty()){
            first = last = 0;
            queueArray[last] = next;
            maxLength++;
            System.out.println("* "+queueArray[last].getFirstName()+" ADDED TO THE TRAIN QUEUE");
        }else if(isFull()){
            System.out.println("TRAIN QUEUE IS FULL, CAN'T ADD ANY PASSENGERS!");
        }
        else{ //executes when there's only one passenger in the train queue
            last = (last+1) % MAXIMUM_SEATS;
            queueArray[last] = next;
            maxLength++;
            System.out.println("* "+queueArray[last].getFirstName()+" ADDED TO THE TRAIN QUEUE");
        }
    }

    public Passenger remove(Passenger removedPassenger){
        if(isEmpty()){
            System.out.println("TRAIN QUEUE IS EMPTY, CAN'T EXECUTE THE PROCESS!");
        }
        else if( first == last){ //executes when there's only one passenger in the train queue
            first = last = -1;
            maxLength--;
        }
        else { //executes when there's more than one passenger in the train queue
            first = (first + 1) % MAXIMUM_SEATS;
            maxLength--;
        }
        return removedPassenger;
    }

    public boolean isEmpty(){
        if(first == -1 && last == -1){
            return true;
        }
        return false;
    }

    public boolean isFull(){
        if(((last+1) % MAXIMUM_SEATS) == first){
            return true;
        }
        return false;
    }

    public void setMaxStayInQueue(int totalTime){
        this.maxStayInQueue = maxStayInQueue + totalTime;
    }

    public int getMaxStayInQueue(){
        return maxStayInQueue;
    }

    public void setMaxLength(int queueLength){
        this.maxLength = queueLength;
    }

    public int getMaxLength(){
        return maxLength;
    }

    public Passenger[] getQueueArray(){
        return queueArray;
    }

    public void deletePassengerFromQueue(int seatNumber){
        for(int i=0; i < queueArray.length; i++){
            if(queueArray[i] != null){
                if(seatNumber == queueArray[i].getSeatNumber()){
                    System.out.println("SUCCESSFULLY DELETED "+queueArray[i].getFirstName()+" FROM SEAT NUMBER "+queueArray[i].getSeatNumber());
                    queueArray[i]  = null;
                }
            }
        }
    }

    public void swapNullAfterDelete(){ //used to swap null values to the end of the queue
        Passenger temporaryStore;
        for(int i=0; i < queueArray.length-1; i++){
            for(int j=0; j < queueArray.length-i-1; i++ ){
                if(queueArray[j] == null){
                    temporaryStore = queueArray[j+1];
                    queueArray[j+1] = queueArray[j];
                    queueArray[j] = temporaryStore;
                }
            }
        }
        int countOfNull = 0; //used to count null values in queue
        for(int i=0; i < queueArray.length; i++){
            if(queueArray[i] == null){
                countOfNull++;
            }
        }
        if(countOfNull == queueArray.length){ //executes when all the values were null after deleting to reset queue
            first = -1;
            last = -1;
            maxLength = 0;
        }
        for(int i=0; i < queueArray.length-1; i++){
            if(queueArray[i] != null){
                last = i+1;
                maxLength = i+1;
            }
        }
    }
}
