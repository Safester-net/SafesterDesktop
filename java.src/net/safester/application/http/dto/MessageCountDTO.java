package net.safester.application.http.dto;

/**
 * DTO for message count
 * 
 * @author Nicolas de Pomereu
 *
 */
public final class MessageCountDTO {

    private final String status = "OK";
    private int count;


    public MessageCountDTO() {

    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
	return "MessageCountDTO [status=" + status + ", count=" + count + "]";
    }

    

}
