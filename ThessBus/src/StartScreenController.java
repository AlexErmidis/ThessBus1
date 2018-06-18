import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class StartScreenController extends MainController implements Initializable {
	
	@FXML private Label usernameMenu;
	@FXML private Label balanceMenu;
	@FXML private Label welcome;
	@FXML private Label fineLabel;
	@FXML private Hyperlink payNow;
	@FXML private VBox payNowPane;

	public void onClickedTicket(ActionEvent actionEvent) throws IOException {
		Stage primaryStage = getStageFromEvent(actionEvent);
		FXMLLoader loader = new FXMLLoader(getClass().getResource("Ticket_Panel.fxml"));
		Parent root = null;
		root = loader.load();

		Scene scene = new Scene(root);

		// setUserData so that the fxml file of the loader can be retrieved
		scene.setUserData(loader);

		primaryStage.setScene(scene);
		primaryStage.setTitle("ThessBus: Ticket Purchase");
		primaryStage.show();
	}

	public void onClickedCard(ActionEvent actionEvent) throws IOException {
		Stage primaryStage = getStageFromEvent(actionEvent);
		FXMLLoader loader = new FXMLLoader(getClass().getResource("Card.fxml"));
		Parent root = null;
		root = loader.load();

		Scene scene = new Scene(root);

		// setUserData so that the fxml file of the loader can be retrieved
		scene.setUserData(loader);
		primaryStage.setScene(scene);
		primaryStage.setTitle("ThessBus: Card Purchase");
		primaryStage.show();
	}

	public void onClickPayNow(ActionEvent e) throws IOException {
		ArrayList<String> choices = new ArrayList<>();
		for (Fine f : Main.loginUser.getFines()) {
			if(f.isPaid() == false)
				choices.add("Date Time: " + f.getDate_time() + ", Bus: " + f.getBus() + ", Price: "
							+ Double.toString(f.getPrice()));
		}
		
		ChoiceDialog<String> dialog = new ChoiceDialog<>("������� ���������", choices);
		dialog.setTitle("Pay Fine");
		dialog.setHeaderText(null);
		dialog.setContentText("������� �� ��������" + System.lineSeparator() +"��� ������ �� ���������: ");
		Optional<String> result = dialog.showAndWait();
		
		if (result.isPresent()) {
			for (Fine f : Main.loginUser.getFines()) {
				if (result.get().contains(f.getDate_time())) {
					if (f.getPrice() <= Main.loginUser.getBalance()) {
						f.finePaid();
						Main.loginUser.reduceBalance(f.getPrice());
						
						Alert alert = new Alert(AlertType.INFORMATION);
						alert.setTitle("Alert");
						alert.setHeaderText(null);
						alert.setContentText("�� �������� ��� ���������");
						alert.showAndWait();
						
						//initialize();
					} 
					else {
						Alert alert = new Alert(AlertType.INFORMATION);
						alert.setTitle("Alert");
						alert.setHeaderText(null);
						alert.setContentText("�� �������� ��� ��� �������!");
						alert.showAndWait();
					}

				}
			}
		}
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		usernameMenu.setText(Main.loginUser.getUsername());
		balanceMenu.setText(Double.toString(Main.loginUser.getBalance()));
		welcome.setText("���� ���, " + Main.loginUser.getUsername() + "!");
		
		fineLabel.setText("   ����� (" + Main.loginUser.countUnpaidFines() + ") ��������/� ��������/�" 
				/*") ��������/� ��� ���������: " + Double.toString(Main.loginUser.calculateTotalFines()) + "�"*/);
		
		if(Main.loginUser.countUnpaidFines() == 0) {
			payNow.setMouseTransparent(true);
			payNow.setEffect(new GaussianBlur());
			fineLabel.setText("   ��� ����� �������� ���� �������");
		}
		
		if(Main.loginUser.countMultiWayNotValidatedTickets() > 0) {
			HBox MultiWayTicketsHBox = new HBox();
			MultiWayTicketsHBox.setSpacing(30);
			MultiWayTicketsHBox.setPadding(new Insets(0, 0, 0, 20));
			
			Label multiWayTicketsLabel = new Label("����� (" + Main.loginUser.countMultiWayNotValidatedTickets() + ") ���������/� ���������"
									+ System.lineSeparator() + "��������� �� �����������");
			multiWayTicketsLabel.setFont(new Font(13));
			Hyperlink viewTicketslink = new Hyperlink("�������");
			//viewTicketslink.setTextFill(new );
			viewTicketslink.setUnderline(true);
			viewTicketslink.setOnAction((ActionEvent e) -> {
			    onClickViewTickets(e);
			});
			MultiWayTicketsHBox.getChildren().add(multiWayTicketsLabel);
			MultiWayTicketsHBox.getChildren().add(viewTicketslink);
			
			payNowPane.getChildren().add(MultiWayTicketsHBox);
		}
		
		if(Main.loginUser.countNotValidCards() > 0) {
			//label ���������� - ������ ��� ��� ������� ����?
		}
		
	}
	
	public void onClickViewTickets(ActionEvent e) {
		ArrayList<String> choices = new ArrayList<>();
		for (Product p: Main.loginUser.getProducts()) {
			if(p instanceof Ticket && ((Ticket)p).getRemaining_routes() > 0)
				choices.add("���������� ����������: " + p.getDate_time() + ", ����������� �������: " + 
							((Ticket)p).getRemaining_routes() + ", ����: " + Double.toString(p.getPrice()) + "�");
		}
		
		ChoiceDialog<String> dialog = new ChoiceDialog<>("������� ����������", choices);
		dialog.setTitle("Validate Ticket");
		dialog.setHeaderText(null);
		dialog.setContentText("������� �� ���������" + System.lineSeparator() +"��� �� ������ �� �����������: ");
		Optional<String> result = dialog.showAndWait();
		
		if (result.isPresent()) {
			for (Product p : Main.loginUser.getProducts()) {
				if (result.get().contains(p.getDate_time())) {
					
					if(((Ticket)p).isValid() == false) {
						Alert alert = new Alert(AlertType.CONFIRMATION);
						alert.setTitle("Alert");
						alert.setHeaderText(null);
						alert.setContentText("�� ������������ ��� � ��������� ��� ����������" + System.lineSeparator() + 
											 "����� ����� ��� �������� ����� ��� ��������� �� ����!" + System.lineSeparator() +
											 "��������;");
						Optional<ButtonType> result1 = alert.showAndWait();
						if(result1.get() == ButtonType.CANCEL)
							break;
					}
					
					((Ticket)p).Refresh_num_of_routes();
					((Ticket)p).setValidation_date_time();
					FileManager.updateProduct(p, "Products.dat");
						
					Alert alert = new Alert(AlertType.INFORMATION);
					alert.setTitle("Alert");
					alert.setHeaderText(null);
					alert.setContentText("�� ��������� ��� �����������");
					alert.showAndWait();
						
					//initialize();

				}
			}
		}
	}
}
