package application;

import java.awt.Desktop.Action;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.gluonhq.charm.glisten.control.ToggleButtonGroup;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;

import entities.Candidacy;
import entities.CandidacyStatus;
import entities.Candidate;
import entities.Interview;
import entities.JobOffer;
import entities.Question;
import entities.Test;
import entities.Test_t;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import services_cand_interv.CandidacyServiceRemote;
import services_cand_interv.InterviewServiceRemote;
import services_cand_interv.JobOfferServiceRemote;

public class RecruitmentController implements Initializable {
	
	static int EntId=1;
	private Pane currentPane;
	@FXML
	private StackPane rootPane;
	@FXML
	private JFXButton btn_showOffers,btn_showTests,btn_showInterviews,btn_showCandidacies;
	@FXML
	private JFXButton btn_newOffer,btn_editOffer,btn_deleteOffer,btn_viewCand,btn_acceptCand,btn_rejectCand;
	@FXML
	private JFXButton btn_cancelAddOffer,btn_addOffer;
	@FXML
	private JFXButton btn_newQuestion,btn_editQuestion,brn_deleteQuestion,btn_addTest,btn_deleteTest;
	@FXML
	private JFXButton btn_testAddQuestion,btn_testRemoveQuestion,btn_testSave,btn_testCancel;
	@FXML
	private Pane pane_offers,pane_candidacies,pane_interviews,pane_tests,pane_addOffer,pane_viewCand,pane_addTest;
	@FXML
	private JFXListView<Label>lv_offers,lv_questions,lv_testQuestions;
	@FXML
	private JFXDatePicker dp_startDate,dp_endDate,dp_nOffStart,dp_nOffEnd;
	@FXML
	private JFXTextArea ta_description,ta_nOffDescription,ta_question;
	@FXML
	private JFXTextField tf_nOffTitle,tf_choice1,tf_choice2,tf_choice3,tf_answer;
	@FXML
	private JFXTextField tf_testDuration,tf_testQuestion,tf_testAnswer,tf_testChoice1,tf_testChoice2,tf_testChoice3;
	@FXML
	private Label lbl_offer;
	@FXML
	private JFXRadioButton rb_allOffers,rb_currentOffers,rb_allCand,rb_acceptedCand,rb_rejectedCand,rb_pendingCand;
	@FXML
	private JFXRadioButton rb_allInterview,rb_comingInterview;
	@FXML
	private TableView<Candidacy>tv_viewCand,tv_cand;
	@FXML
	private TableView<Interview>tv_interviews,tv_testResults;
	@FXML
	private TableColumn<Interview,String>tc_resultCandidate,tc_resultTest,tc_resultPassed,tc_candidateInterview,tc_testInterview;
	@FXML
	private TableColumn<Interview,Date>tc_dateInterview;
	@FXML
	private TableColumn<Interview,Integer>tc_idInterview;
	@FXML
	private TableColumn<Interview,Float>tc_resultScore;
	@FXML
	private TableColumn<Candidacy,String>tc_name,tc_birth,tc_offerTitle,tc_nameCand,tc_emailCand,tc_birthCand;
	@FXML
	private TableColumn<Candidacy,Date>tc_submitAt,tc_offerEnd,tc_submitCand;
	@FXML
	private TableColumn<Candidacy,CandidacyStatus>tc_status,tc_statusCand;
	@FXML
	private JFXComboBox<String> cbx_cand;
	@FXML
	private JFXComboBox<Test_t>cbx_tests,cbx_testType;
	@FXML
	private ToggleButtonGroup tbg_cand;
	
	public static List<Question>tempQuestions = new ArrayList<Question>();
	private String jndiname = "spectrum-ear/spectrum-ejb/JobOfferService!services_cand_interv."
			+ "JobOfferServiceRemote";
	private String jndiname2 ="spectrum-ear/spectrum-ejb/CandidacyService!services_cand_interv."
			+ "CandidacyServiceRemote";
	private String jndiname3 ="spectrum-ear/spectrum-ejb/InterviewService!services_cand_interv."
			+ "InterviewServiceRemote";
	
	//recuperer toutes les offres d'emploi
	public List<JobOffer> loadAllOffers() throws NamingException {
		Context context = new InitialContext();
		JobOfferServiceRemote offer_s = (JobOfferServiceRemote) context.lookup(jndiname);
		return offer_s.getjobOffersByEnt(EntId);
	}
	//recuperer les offres d'emploi actuelles
	public List<JobOffer> loadCurrentOffers() throws NamingException {
		Context context = new InitialContext();
		JobOfferServiceRemote offer_s = (JobOfferServiceRemote) context.lookup(jndiname);
		List<JobOffer>currentOffers = new ArrayList<JobOffer>();
		for (JobOffer offer : offer_s.getjobOffersByEnt(EntId)) {
			if (offer.getEnd().compareTo(new Date())>0)
				currentOffers.add(offer);
		}
		return currentOffers;
	}
	//affichage des offres
	public void printOffers(List<JobOffer>list) throws NamingException{
		lv_offers.getItems().clear();
		for (JobOffer offer : list) {
			Label lbl = new Label(offer.getTitle());
			lv_offers.getItems().add(lbl);
		}
	}
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		pane_offers.toFront();
		try {
			printOffers(loadAllOffers());
		} catch (NamingException e) {
			e.printStackTrace();
		}
		
	}
	@FXML
	public void menuButton(ActionEvent e) throws NamingException{
		if(currentPane==pane_addOffer) {
			cancelAddOffer(new ActionEvent());
			
		}
		else if (btn_editOffer.getText().equals("Save")) {
			simpleDialog(rootPane, "You are in edit mode, save the changes first", "Ok");
		}
		else if (btn_editQuestion.getText().equals("Save")) {
			simpleDialog(rootPane, "You are in edit mode, save the changes first", "Ok");
		}
		else if (btn_newQuestion.getText().equals("Save")) {
			simpleDialog(rootPane, "You are in edit mode, save the changes first", "Ok");
		}
		else if(e.getSource()==btn_showOffers) {
			pane_offers.toFront();
			printOffers(loadAllOffers());
			currentPane = pane_offers;
		}
		else if(e.getSource()==btn_showCandidacies) {
			currentPane = pane_candidacies;
			cbx_cand.getItems().clear();
			cbx_cand.getItems().add("All offers");
			cbx_cand.getItems().add("Current offers");
			cbx_cand.getItems().add("Expired offers");
			cbx_cand.setValue("All offers");
			loadAllCand(getAllCandidacies());
			pane_candidacies.toFront();
		}
		else if(e.getSource()==btn_showInterviews) {
			loadInterviews(getInterviews());
			loadResults(getInterviews());
			currentPane = pane_interviews;
			pane_interviews.toFront();
		}
		else if(e.getSource()==btn_showTests) {
			ta_question.clear();
			tf_answer.clear();
			tf_choice1.clear();
			tf_choice2.clear();
			tf_choice3.clear();
			cbx_tests.getItems().clear();
			for (Test test : loadTests()) {
				cbx_tests.getItems().add(test.getType());
			}
			pane_tests.toFront();
			currentPane = pane_tests;
		}
	}
	
	//A la selection d'une offre
	@FXML
	public void selectedOffer(MouseEvent e) throws NamingException {
		Context context = new InitialContext();
		JobOfferServiceRemote offer_s = (JobOfferServiceRemote) context.lookup(jndiname);
		Label offerlbl = lv_offers.getSelectionModel().getSelectedItem();
		JobOffer currentOffer = offer_s.getjobOfferByTitle(offerlbl.getText(), EntId);
		Calendar cal = Calendar.getInstance();
		cal.setTime(currentOffer.getStart());
		dp_startDate.setValue(LocalDate.of(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH)+1,cal.get(Calendar.DAY_OF_MONTH)));
		cal.setTime(currentOffer.getEnd());
		dp_endDate.setValue(LocalDate.of(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH)+1,cal.get(Calendar.DAY_OF_MONTH)));
		ta_description.setText(currentOffer.getDescription());
	}
	
	//Mise à jour d'une offre
	@FXML
	public void editOffer(ActionEvent e) throws NamingException {
		Context context = new InitialContext();
		JobOfferServiceRemote offer_s = (JobOfferServiceRemote) context.lookup(jndiname);
		if (btn_editOffer.getText().equals("Edit")) {
			if (lv_offers.getSelectionModel().isEmpty())
				simpleDialog(rootPane, "Select the job offer you want to edit", "Ok");
			else {
				btn_editOffer.setText("Save");
				ta_description.setEditable(true);
				dp_startDate.setDisable(true);
			}
		}else {
			Label offerlbl = lv_offers.getSelectionModel().getSelectedItem();
			JobOffer currentOffer = offer_s.getjobOfferByTitle(offerlbl.getText(), EntId);
			currentOffer.setDescription(ta_description.getText());
			currentOffer.setEnd(Date.from(dp_endDate.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()));
			offer_s.modifyJobOffer(EntId, currentOffer);
			btn_editOffer.setText("Edit");
			ta_description.setEditable(false);
			dp_startDate.setDisable(false);
		}
	}
	@FXML
	public void newOffer(ActionEvent e) throws NamingException {
		if (btn_editOffer.getText().equals("Save")) {
			simpleDialog(rootPane, "You are in edit mode, finish the changes first", "Ok");
		}else {
			pane_addOffer.toFront();
			dp_nOffStart.setValue(LocalDate.now());
			dp_nOffEnd.setValue(LocalDate.now().plusDays(1));
			currentPane = pane_addOffer;
		}
	}
	//annulation des modifications
	@FXML
	public void cancelAddOffer(ActionEvent e) throws NamingException {
		JFXDialogLayout dialogLayout = new JFXDialogLayout();
		JFXButton no = new JFXButton("No");
		JFXButton yes = new JFXButton("Yes");
		JFXDialog dialog = new JFXDialog(rootPane,dialogLayout,JFXDialog.DialogTransition.TOP);
		yes.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent mouseEvent)-> {
			dialog.close();
			tf_nOffTitle.clear();
			ta_nOffDescription.clear();
			pane_offers.toFront();
			currentPane = pane_offers;
		});
		no.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent mouseEvent)-> {
			dialog.close();
		});
		dialogLayout.setHeading(new Label("Modifications not saved, continue ?"));
		dialogLayout.setActions(no,yes);
		dialog.show();
	}
	public void simpleDialog(StackPane pane, String msg,String buttonMsg) {
		JFXDialogLayout dialogLayout = new JFXDialogLayout();
		JFXButton button = new JFXButton(buttonMsg);
		JFXDialog dialog = new JFXDialog(pane,dialogLayout,JFXDialog.DialogTransition.TOP);
		button.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent mouseEvent)-> {
			dialog.close();
		});
		dialogLayout.setHeading(new Label(msg));
		dialogLayout.setActions(button);
		dialog.show();
	}
	//ajout d'une nouvelle offre
	@FXML
	public void addOffer(ActionEvent e) throws NamingException {
		Context context = new InitialContext();
		JobOfferServiceRemote offer_s = (JobOfferServiceRemote) context.lookup(jndiname);
		Date end = Date.from(dp_nOffEnd.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
		if(tf_nOffTitle.getText().equals("")||ta_nOffDescription.getText().equals("")) {
			simpleDialog(rootPane, "Required fields not filled", "Ok");
		}else if(end.compareTo(new Date())<=0) {
			simpleDialog(rootPane, "End date must be after today", "Ok");
		}else if(offer_s.getjobOfferByTitle(tf_nOffTitle.getText(), EntId)!=null) {
			simpleDialog(rootPane, "This job offer's title already exist", "Change it");
		}else {
			JobOffer offer = new JobOffer();
			offer.setTitle(tf_nOffTitle.getText());
			offer.setDescription(ta_nOffDescription.getText());
			offer.setStart(Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()));
			offer.setEnd(end);
			offer_s.addJobOffer(EntId, offer);
			pane_offers.toFront();
			tf_nOffTitle.clear();
			ta_nOffDescription.clear();
			currentPane = pane_offers;
			printOffers(loadAllOffers());
		}
	}
	//suppresion d'une offre d'emploi
	@FXML
	public void removeOffer(ActionEvent e) throws NamingException {
		Context context = new InitialContext();
		JobOfferServiceRemote offer_s = (JobOfferServiceRemote) context.lookup(jndiname);
		if (lv_offers.getSelectionModel().isEmpty()) {
			simpleDialog(rootPane, "Select the job offer you want to delete", "Ok");
		}else if (btn_editOffer.getText().equals("Save")) {
			simpleDialog(rootPane, "You are in edit mode, finish the changes first", "Ok");
		}else {
			Label offerlbl = lv_offers.getSelectionModel().getSelectedItem();
			JobOffer currentOffer = offer_s.getjobOfferByTitle(offerlbl.getText(), EntId);
			JFXDialogLayout dialogLayout = new JFXDialogLayout();
			JFXButton no = new JFXButton("No");
			JFXButton delete = new JFXButton("Delete");
			JFXDialog dialog = new JFXDialog(rootPane,dialogLayout,JFXDialog.DialogTransition.TOP);
			delete.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent mouseEvent)-> {
				offer_s.removeJobOfferFromEnt(EntId, currentOffer.getId());
				dialog.close();
				try {
					printOffers(loadAllOffers());
				} catch (NamingException e1) {
					e1.printStackTrace();
				}
			});
			no.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent mouseEvent)-> {
				dialog.close();
			});
			dialogLayout.setHeading(new Label("Do you really want to delete this job offer ?"));
			dialogLayout.setActions(no,delete);
			dialog.show();
		}
	}
	//selectionner toutes les offres/les offres en cours
	@FXML
	public void toggleButton(ActionEvent e) throws NamingException {
		if (e.getSource()==rb_allOffers) {
			printOffers(loadAllOffers());
		}else
			printOffers(loadCurrentOffers());
	}
	//----------------------------------JOB OFFERS END-------------------------------------------------------------
		
	//----------------------------------CANDIDACIES PART---------------------------------------------------------------
	
	//afficher les candidatures d'une offre precise
	@FXML
	public void viewCand(ActionEvent e) throws NamingException{
		if (lv_offers.getSelectionModel().isEmpty()) {
			simpleDialog(rootPane, "Select the job offer", "Ok");
		}else if (btn_editOffer.getText().equals("Save")) {
			simpleDialog(rootPane, "You are in edit mode, finish the changes first", "Ok");
		}else {
			pane_viewCand.toFront();
			currentPane = pane_viewCand;
			loadCand(getCandidacies());
		}
	}
	
	//charger les candidatures(d'une offre) dans le tableview
	public void loadCand(ObservableList<Candidacy> liste) throws NamingException {
		tv_viewCand.getItems().clear();
		tc_nameCand.setCellValueFactory(new PropertyValueFactory<>("name"));
		tc_birthCand.setCellValueFactory(new PropertyValueFactory<>("birth"));
		tc_emailCand.setCellValueFactory(new PropertyValueFactory<>("email"));
		tc_submitCand.setCellValueFactory(new PropertyValueFactory<>("date"));
		tc_statusCand.setCellValueFactory(new PropertyValueFactory<>("status"));
		tv_viewCand.setItems(liste);
	}
	
	//recuperer les candidatures d'une offre
	public ObservableList<Candidacy> getCandidacies() throws NamingException{
		Context context = new InitialContext();
		JobOfferServiceRemote offer_s = (JobOfferServiceRemote) context.lookup(jndiname);
		CandidacyServiceRemote candidacy_s = (CandidacyServiceRemote) context.lookup(jndiname2);
		Label offerlbl = lv_offers.getSelectionModel().getSelectedItem();
		JobOffer currentOffer = offer_s.getjobOfferByTitle(offerlbl.getText(), EntId);
		lbl_offer.setText(currentOffer.getTitle());
		ObservableList<Candidacy>candidacies = FXCollections.observableArrayList();
		for (Candidacy candidacy : candidacy_s.getCandidaciesByOffer(currentOffer.getId())) {
			Candidate candidate = candidacy.getCandidate();
			candidacy.setName(candidate.getUser().getUsername());
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			candidacy.setBirth(dateFormat.format(candidate.getUser().getBirth()));
			candidacy.setEmail(candidate.getUser().getEmail());
			candidacy.setOfferTitle(candidacy.getJoboffer().getTitle());
			candidacy.setOfferEnd(candidacy.getJoboffer().getEnd());
			candidacies.add(candidacy);
		}
		return candidacies;
	}
	//recuperer toutes les candidatures
	public ObservableList<Candidacy> getAllCandidacies() throws NamingException{
		Context context = new InitialContext();
		CandidacyServiceRemote candidacy_s = (CandidacyServiceRemote) context.lookup(jndiname2);
		ObservableList<Candidacy>candidacies = FXCollections.observableArrayList();
		for (Candidacy candidacy : candidacy_s.getAllCandidacies()) {
			Candidate candidate = candidacy.getCandidate();
			candidacy.setName(candidate.getUser().getName());
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			candidacy.setBirth(dateFormat.format(candidate.getUser().getBirth()));
			candidacy.setEmail(candidate.getUser().getEmail());
			candidacy.setOfferTitle(candidacy.getJoboffer().getTitle());
			candidacy.setOfferEnd(candidacy.getJoboffer().getEnd());
			candidacies.add(candidacy);
		}
		return candidacies;
	}
	
   
	public void decision(CandidacyStatus status) throws NamingException {
		Context context = new InitialContext();
		CandidacyServiceRemote candidacy_s = (CandidacyServiceRemote) context.lookup(jndiname2);
		String name = tv_viewCand.getSelectionModel().getSelectedItem().getName();
		Candidacy cand = new Candidacy();
		for (Candidacy candidacy : getCandidacies()) {
			if(candidacy.getName().equals(name)) {
				cand = candidacy;
			}
		}
		if (status==CandidacyStatus.accepted)
			cand.setStatus(CandidacyStatus.accepted);
		else
			cand.setStatus(CandidacyStatus.rejected);
		candidacy_s.modifyCandidacy(cand);
	}
	
	//accepter un candidature
	@FXML
	public void acceptCand(ActionEvent e) throws NamingException {
		decision(CandidacyStatus.accepted);
		loadCand(getCandidacies());
	}
	//rejeter une candidature
	@FXML
	public void rejectCand(ActionEvent e) throws NamingException {
		decision(CandidacyStatus.rejected);
		loadCand(getCandidacies());
	}
	//checker si la candidature est acceptée ou l'offre expirée
	@FXML
	public void onSelectedRow(MouseEvent e) throws NamingException {
		if(tv_cand.getSelectionModel().getSelectedItem().getStatus()!=CandidacyStatus.pending ||
				tv_cand.getSelectionModel().getSelectedItem().getOfferEnd().compareTo(new Date())<=0){
			btn_acceptCand.setDisable(true);
			btn_rejectCand.setDisable(true);
		}else {
			btn_acceptCand.setDisable(false);
			btn_rejectCand.setDisable(false);
		}
	}
	//charger toutes le candidatures dans le tableview
	public void loadAllCand(ObservableList<Candidacy> liste) throws NamingException {
		tv_cand.getItems().clear();
		tc_name.setCellValueFactory(new PropertyValueFactory<>("name"));
		tc_birth.setCellValueFactory(new PropertyValueFactory<>("birth"));
		tc_submitAt.setCellValueFactory(new PropertyValueFactory<>("date"));
		tc_status.setCellValueFactory(new PropertyValueFactory<>("status"));
		tc_offerTitle.setCellValueFactory(new PropertyValueFactory<>("offerTitle"));
		tc_offerEnd.setCellValueFactory(new PropertyValueFactory<>("offerEnd"));
		tv_cand.setItems(liste);
	}
	
	public List<Candidacy>triCbxCand(List<Candidacy>allCandidacies){
		List<Candidacy> candidacies = allCandidacies;
		if (cbx_cand.getValue().equals("Expired offers")) {
			for (Candidacy candidacy : candidacies) {
				if (candidacy.getJoboffer().getEnd().compareTo(new Date())>0)
					candidacies.remove(candidacy);
			}
			return candidacies;
		}else if(cbx_cand.getValue().equals("Current offers")){
			for (Candidacy candidacy : candidacies) {
				if (candidacy.getJoboffer().getEnd().compareTo(new Date())<=0)
					candidacies.remove(candidacy);
			}
			return candidacies;
		}else
			return candidacies;
	}
	//tri candidatures
	@FXML
	public void triCand(ActionEvent e) throws NamingException{
		List<Candidacy> candidacies = triCbxCand((List<Candidacy>)getAllCandidacies());
		ObservableList<Candidacy> sorted = FXCollections.observableArrayList();
		if(rb_allCand.isSelected()) {
			sorted = (ObservableList<Candidacy>) candidacies;
		}else if(rb_pendingCand.isSelected()) {
			for (Candidacy candidacy : candidacies) {
				if(candidacy.getStatus()==CandidacyStatus.pending)
					sorted.add(candidacy);
			}
		}else if(rb_acceptedCand.isSelected()) {
			for (Candidacy candidacy : candidacies) {
				if(candidacy.getStatus()==CandidacyStatus.accepted)
					sorted.add(candidacy);
			}
		}else if(rb_rejectedCand.isSelected()) {
			for (Candidacy candidacy : candidacies) {
				if(candidacy.getStatus()==CandidacyStatus.rejected)
					sorted.add(candidacy);
			}
		}
		loadAllCand(sorted);
	}
	//------------------------------------------CANDIDACIES END------------------------------------------------------
		
	//------------------------------------------------TESTS PART----------------------------------------------------
	
	
	public List<Test> loadTests() throws NamingException{
		Context context = new InitialContext();
		InterviewServiceRemote interview_s = (InterviewServiceRemote ) context.lookup(jndiname3);
		return interview_s.getAllTest(EntId);
	}
	public void printQuestions(Test test) throws NamingException{
		lv_questions.getItems().clear();
			for (Question question : test.getQuestions()) {
				Label lbl = new Label(question.getContent());
				lv_questions.getItems().add(lbl);
			}
		
	}
	
	@FXML
	public void onSelectTest(ActionEvent e) throws NamingException {
		Context context = new InitialContext();
		InterviewServiceRemote interview_s = (InterviewServiceRemote ) context.lookup(jndiname3);
		printQuestions(interview_s.searchTest(EntId, cbx_tests.getValue()));
	}
	@FXML
	public void onSelectQuestion(MouseEvent e) throws NamingException {
		Context context = new InitialContext();
		InterviewServiceRemote interview_s = (InterviewServiceRemote ) context.lookup(jndiname3);
		Question q = interview_s.searchQuestion(interview_s.searchTest(EntId, cbx_tests.getValue()).getId(),
				lv_questions.getSelectionModel().getSelectedItem().getText());
		ta_question.setText(q.getContent());
		tf_answer.setText(q.getAnswer());
		tf_choice1.setText(q.getChoice1());
		tf_choice2.setText(q.getChoice2());
		tf_choice3.setText(q.getChoice3());
	}
	@FXML
	public void editQuestion(ActionEvent e) throws NamingException {
		Context context = new InitialContext();
		InterviewServiceRemote interview_s = (InterviewServiceRemote ) context.lookup(jndiname3);
		if (btn_editQuestion.getText().equals("Edit")) {
			if (lv_questions.getSelectionModel().isEmpty())
				simpleDialog(rootPane, "Select the question you want to edit", "Ok");
			else if (btn_newQuestion.getText().equals("Save"))
				simpleDialog(rootPane, "You are in edit mode, save the changes first", "Ok");
			else {
				btn_editQuestion.setText("Save");
				ta_question.setEditable(true);
				tf_answer.setEditable(true);
				tf_choice1.setEditable(true);
				tf_choice2.setEditable(true);
				tf_choice3.setEditable(true);
			}
		}else {
			Question currentQuestion =interview_s.searchQuestion(interview_s.searchTest(EntId, cbx_tests.getValue()).getId(),
					lv_questions.getSelectionModel().getSelectedItem().getText());
			currentQuestion.setContent(ta_question.getText());
			currentQuestion.setAnswer(tf_answer.getText());
			currentQuestion.setChoice1(tf_choice1.getText());
			currentQuestion.setChoice2(tf_choice2.getText());
			currentQuestion.setChoice3(tf_choice3.getText());
			interview_s.modifyQuestion(currentQuestion);
			btn_editQuestion.setText("Edit");
			ta_question.setEditable(false);
			tf_answer.setEditable(false);
			tf_choice1.setEditable(false);
			tf_choice2.setEditable(false);
			tf_choice3.setEditable(false);
			printQuestions(interview_s.searchTest(EntId, cbx_tests.getValue()));
		}
	}
	@FXML
	public void removeQuestion(ActionEvent e) throws NamingException {
		Context context = new InitialContext();
		InterviewServiceRemote interview_s = (InterviewServiceRemote ) context.lookup(jndiname3);
		if (lv_questions.getSelectionModel().isEmpty()) {
			simpleDialog(rootPane, "Select the question you want to delete", "Ok");
		}else if (btn_editQuestion.getText().equals("Save")) {
			simpleDialog(rootPane, "You are in edit mode, save the changes first", "Ok");
		}else if (btn_newQuestion.getText().equals("Save")) {
			simpleDialog(rootPane, "You are in edit mode, save the changes first", "Ok");
		}else {
			Test test = interview_s.searchTest(EntId, cbx_tests.getValue());
			Question currentQuestion = interview_s.searchQuestion(test.getId(),lv_questions.getSelectionModel().getSelectedItem().getText());
			JFXDialogLayout dialogLayout = new JFXDialogLayout();
			JFXButton no = new JFXButton("No");
			JFXButton delete = new JFXButton("Delete");
			JFXDialog dialog = new JFXDialog(rootPane,dialogLayout,JFXDialog.DialogTransition.TOP);
			delete.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent mouseEvent)-> {
				interview_s.removeQuestion(currentQuestion);
				dialog.close();
				try {
					printQuestions(interview_s.searchTest(EntId, cbx_tests.getValue()));
				} catch (NamingException e1) {
					e1.printStackTrace();
				}
			});
			no.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent mouseEvent)-> {
				dialog.close();
			});
			dialogLayout.setHeading(new Label("Do you really want to delete this question ?"));
			dialogLayout.setActions(no,delete);
			dialog.show();
		}
	}
	@FXML
	public void addQuestion(ActionEvent e) throws NamingException {
		Context context = new InitialContext();
		InterviewServiceRemote interview_s = (InterviewServiceRemote ) context.lookup(jndiname3);
		if (btn_newQuestion.getText().equals("New")) {
			if (btn_editQuestion.getText().equals("Save"))
				simpleDialog(rootPane, "You are in edit mode, save the changes first", "Ok");
			else if(cbx_tests.getValue()==null)
			simpleDialog(rootPane, "Please choice a test", "Ok");
			else {
				ta_question.clear();
				tf_answer.clear();
				tf_choice1.clear();
				tf_choice2.clear();
				tf_choice3.clear();
				btn_newQuestion.setText("Save");
				ta_question.setEditable(true);
				tf_answer.setEditable(true);
				tf_choice1.setEditable(true);
				tf_choice2.setEditable(true);
				tf_choice3.setEditable(true);
			}	
		}else {
			if(ta_question.getText().equals("")||tf_answer.getText().equals("")||tf_choice1.getText().equals("")||
					tf_choice2.getText().equals("")||tf_choice3.getText().equals("")) {
				simpleDialog(rootPane, "Required fields not filled", "Ok");
			}
			else{
				Question question = new Question();
				question.setContent(ta_question.getText());
				question.setAnswer(tf_answer.getText());
				question.setChoice1(tf_choice1.getText());
				question.setChoice2(tf_choice2.getText());
				question.setChoice3(tf_choice3.getText());
				interview_s.addQuestionToTest(interview_s.searchTest(EntId, cbx_tests.getValue()).getId(), question);
				btn_newQuestion.setText("New");
				ta_question.setEditable(false);
				tf_answer.setEditable(false);
				tf_choice1.setEditable(false);
				tf_choice2.setEditable(false);
				tf_choice3.setEditable(false);
				printQuestions(interview_s.searchTest(EntId, cbx_tests.getValue()));
			}
		}
	}
	@FXML
	public void newTest(ActionEvent e) throws NamingException {
		Context context = new InitialContext();
		InterviewServiceRemote interview_s = (InterviewServiceRemote ) context.lookup(jndiname3);
		if(interview_s.getAllTest(EntId).size()>=3)
			simpleDialog(rootPane, "You got all test added", "Ok");
		else {
			pane_addTest.toFront();
			cbx_testType.getItems().clear();
			lv_testQuestions.getItems().clear();
			cbx_testType.getItems().add(Test_t.Technical);
			cbx_testType.getItems().add(Test_t.Linguistic);
			cbx_testType.getItems().add(Test_t.Psychotechnical);
			for (Test test : interview_s.getAllTest(EntId)) {
				if (test.getType()==Test_t.Technical||test.getType()==Test_t.Linguistic
						||test.getType()==Test_t.Psychotechnical)
					cbx_testType.getItems().remove(test.getType());
				else
					cbx_testType.setValue(test.getType());
			}
			currentPane = pane_addTest;
		}
		
	}
	@FXML
	public void testAddQuestion(ActionEvent e) {
		Question question = new Question ();
		if(tf_testQuestion.getText().equals("")||tf_testAnswer.getText().equals("")||tf_testChoice1.getText().equals("")||
				tf_testChoice2.getText().equals("")||tf_testChoice3.getText().equals("")) {
			simpleDialog(rootPane, "Required fields not filled", "Ok");
		}else {
			question.setContent(tf_testQuestion.getText());
			question.setAnswer(tf_testAnswer.getText());
			question.setChoice1(tf_testChoice1.getText());
			question.setChoice2(tf_testChoice2.getText());
			question.setChoice3(tf_testChoice3.getText());
			tempQuestions.add(question);
			lv_testQuestions.getItems().add(new Label(question.getContent()));
			tf_testQuestion.clear();
			tf_testAnswer.clear();
			tf_testChoice1.clear();
			tf_testChoice2.clear();
			tf_testChoice3.clear();
		}
		
	}
	public Question getQuestionByContent(String content) {
		for (Question question : tempQuestions) {
			if (question.getContent().equals(content))
				return question;
		}
		return null;
	}
	@FXML
	public void testRemoveQuestion(ActionEvent e) {
		if (lv_testQuestions.getSelectionModel().isEmpty()) {
			simpleDialog(rootPane, "Select the question you want to remove", "Ok");
		}else {
			Question question = getQuestionByContent(lv_testQuestions.getSelectionModel().getSelectedItem().getText());
			lv_testQuestions.getItems().remove(lv_testQuestions.getSelectionModel().getSelectedItem());
			tempQuestions.remove(question);
		}
	}
	@FXML
	public void saveTest(ActionEvent e) throws NamingException {
		Context context = new InitialContext();
		InterviewServiceRemote interview_s = (InterviewServiceRemote ) context.lookup(jndiname3);
		if(tf_testDuration.getText().isEmpty())
			simpleDialog(rootPane, "Required fields not filled", "Ok");
		else if(tempQuestions.isEmpty()||lv_testQuestions.getItems().isEmpty())
			simpleDialog(rootPane, "You need to add at least one question", "Ok");
		else if (!isInteger(tf_testDuration.getText()))
			simpleDialog(rootPane, "Please enter a valid duration", "Ok");
		else if (cbx_testType.getValue()==null)
			simpleDialog(rootPane, "Please select the test type", "Ok");
		else {
			Test test = new Test();
			test.setType(cbx_testType.getValue());
			test.setDuration(Integer.parseInt(tf_testDuration.getText()));
			test=interview_s.addTest(test, EntId);
			for (Question question : tempQuestions) {
				interview_s.addQuestionToTest(test.getId(), question);
			}
			for (Test tst : loadTests()) {
				cbx_tests.getItems().add(tst.getType());
			}
			pane_tests.toFront();
			currentPane = pane_tests;
		}
			
	}
	@FXML
	public void deleteTest(ActionEvent e) throws NamingException {
		Context context = new InitialContext();
		InterviewServiceRemote interview_s = (InterviewServiceRemote ) context.lookup(jndiname3);
		if (cbx_tests.getValue()==null)
			simpleDialog(rootPane, "Please select a test to delete", "Ok");
		else {
			Test test = interview_s.searchTest(EntId, cbx_tests.getValue());
			interview_s.deleteTest(test);
			cbx_tests.getItems().clear();
			for (Test tst : loadTests()) {
				cbx_tests.getItems().add(tst.getType());
			}
		}
	}
		
	@FXML
	public void cancelAddTest(ActionEvent e) throws NamingException {
		JFXDialogLayout dialogLayout = new JFXDialogLayout();
		JFXButton no = new JFXButton("No");
		JFXButton yes = new JFXButton("Yes");
		JFXDialog dialog = new JFXDialog(rootPane,dialogLayout,JFXDialog.DialogTransition.TOP);
		yes.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent mouseEvent)-> {
			dialog.close();
			tf_testDuration.clear();
			lv_testQuestions.getItems().clear();
			pane_tests.toFront();
			currentPane = pane_tests;
			
		});
		no.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent mouseEvent)-> {
			dialog.close();
		});
		dialogLayout.setHeading(new Label("Modifications not saved, continue ?"));
		dialogLayout.setActions(no,yes);
		dialog.show();
	}
	public static boolean isInteger(String s) {
	    try { 
	        Integer.parseInt(s); 
	    } catch(NumberFormatException e) { 
	        return false; 
	    } catch(NullPointerException e) {
	        return false;
	    }
	    return true;
	}
	//------------------------------------------TESTS END------------------------------------------------------
	
		//------------------------------------------------INTERVIEW PART----------------------------------------------------
		
	
	public void loadInterviews(ObservableList<Interview>list) throws NamingException {
		tv_interviews.getItems().clear();
		tc_candidateInterview.setCellValueFactory(new PropertyValueFactory<>("candidate"));
		tc_testInterview.setCellValueFactory(new PropertyValueFactory<>("test"));
		tc_idInterview.setCellValueFactory(new PropertyValueFactory<>("id"));
		tc_dateInterview.setCellValueFactory(new PropertyValueFactory<>("date"));
		tv_interviews.setItems(list);
	}
	public void loadResults(ObservableList<Interview>list) throws NamingException {
		tv_testResults.getItems().clear();
		tc_resultCandidate.setCellValueFactory(new PropertyValueFactory<>("candidate"));
		tc_resultTest.setCellValueFactory(new PropertyValueFactory<>("test"));
		tc_resultScore.setCellValueFactory(new PropertyValueFactory<>("score"));
		tc_resultPassed.setCellValueFactory(new PropertyValueFactory<>("passed"));
		tv_testResults.setItems(list);
	}
	public ObservableList<Interview>getInterviews() throws NamingException{
		Context context = new InitialContext();
		InterviewServiceRemote interview_s = (InterviewServiceRemote ) context.lookup(jndiname3);
		ObservableList<Interview>interviews = FXCollections.observableArrayList();
		for (Interview interview : interview_s.getAllInterviews()) {
			Interview interv = new Interview();
			interv.setId(interview.getId());
			interv.setDate(interview.getDate());
			interv.setCandidate(interview.getTestResult().getCandidate().getUser().getUsername());
			interv.setTest(interview.getTestResult().getTest().getType());
			interv.setScore(interview.getTestResult().getScore());
			interv.setPassed(interview.getTestResult().getPassed() ? "Yes": "No");
			interviews.add(interv);
		}
		return interviews;
	}
	@FXML
	public void sortInterview(ActionEvent e) throws NamingException {
		ObservableList<Interview>interviews = FXCollections.observableArrayList();
		if (rb_allInterview.isSelected()) {
			interviews = getInterviews();
			loadInterviews(interviews);
		}else {
			for (Interview interview : getInterviews()) {
				if(interview.getDate().compareTo(new Date())>0)
					interviews.add(interview);
			}
			loadInterviews(interviews);
		}
	}
	
}
