package application;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javax.naming.NamingException;

import org.jboss.logging.Logger.Level;

import com.oracle.tools.packager.Log.Logger;

import entities.Test;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

public class QuizzController implements Initializable{

	@FXML
	private Label lbl_duration;
	
	
	private String jndiname3 ="spectrum-ear/spectrum-ejb/InterviewService!services_cand_interv."
			+ "InterviewServiceRemote";
	private static Test test = new Test();
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		
	}
	@FXML
	public void onStartQuizz(ActionEvent e) throws NamingException {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("Recruitment.fxml"));
		try {
			loader.load();
		} catch (IOException ex) {
		}
		RecruitmentController recruitment = loader.getController();
			test = recruitment.getTestForDemo();
		lbl_duration.setText(String.valueOf(test.getDuration()));
		
	}
	

}
