import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public  class StartScreenController extends MainController implements Initializable{

	public void onClickedTicket(ActionEvent actionEvent) throws IOException
	{
		Stage primaryStage = getStageFromEvent(actionEvent);
		FXMLLoader loader = new FXMLLoader(getClass().getResource("Ticket_Panel.fxml"));
		Parent root = null;
		root = loader.load();
        TicketPanelController ctrl = (TicketPanelController)loader.getController();
		
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.setTitle("ThessBus: Ticket Purchase");
		primaryStage.show();
	}
	public void onClickedCard(ActionEvent actionEvent) throws IOException
	{
		Stage primaryStage = getStageFromEvent(actionEvent);
		FXMLLoader loader = new FXMLLoader(getClass().getResource("Card.fxml"));
		Parent root = null;
		root = loader.load();
        CardController ctrl = (CardController)loader.getController();
		
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.setTitle("ThessBus: Card Purchase");
		primaryStage.show();
	}
	

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		
	}
}
