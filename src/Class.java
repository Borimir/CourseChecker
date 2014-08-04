import java.io.Serializable;
import java.util.ArrayList;


public class Class implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7279867521434677581L;
	public ArrayList<String> nameList;
	public ArrayList<String> urlList;
	public int desiredSeats;
	public String notifyEmail;
	public boolean email = false;
	
	public Class(ArrayList<String> nameList, ArrayList<String> urlList, int desiredSeats, String notifyEmail){
		this.nameList = nameList;
		this.urlList = urlList;
		this.desiredSeats = desiredSeats;
		this.notifyEmail = notifyEmail;
		if(notifyEmail != ""){
			email = true;
		}
	}
}
