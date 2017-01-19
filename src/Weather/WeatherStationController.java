package weather;

import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Label;
import javafx.scene.control.Button;

public class WeatherStationController implements Initializable{
	
	@FXML private Label currentWeatherInfoLabel;
	@FXML private Label currentWeatherLabel;
	@FXML private Label firstDayLabel;
	@FXML private Label secondDayLabel;
	@FXML private Label thirdDayLabel;
	@FXML private Label fourthDayLabel;
	@FXML private Label fifthDayLabel;
	@FXML private Label firstDayWeatherLabel;
	@FXML private Label secondDayWeatherLabel;
	@FXML private Label thirdDayWeatherLabel;
	@FXML private Label fourthDayWeatherLabel;
	@FXML private Label fifthDayWeatherLabel;
	@FXML private Label extraInfoLabel;
	@FXML private Label showingInfoLabel;
	@FXML private Button searchButton;
	@FXML private Button forecastButton;
	@FXML private Button historicalWeatherButton;
	@FXML private TextField searchTextField;
	@FXML private ImageView firstDayImageView;
	@FXML private ImageView secondDayImageView;
	@FXML private ImageView thirdDayImageView;
	@FXML private ImageView fourthDayImageView;
	@FXML private ImageView fifthDayImageView;
	@FXML private ImageView currentWeatherImageView;
	
	private WeatherService weatherService;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		weatherService = new WeatherService();
		currentWeatherLabel.setText("Current Weather");
		
	}
	@FXML
	void onSearchButtonClick(ActionEvent event){
		getCurrentWeather();
		getForecast();
		showingInfoLabel.setText("Forecast");
	}
	
	@FXML
	void onForecastButtonClick(ActionEvent event){
		getCurrentWeather();
		getForecast();
		showingInfoLabel.setText("Forecast");
	}
	
	@FXML
	void onHistoricalWeatherButtonClick(ActionEvent event){
		getCurrentWeather();
		getHistoricalWeather();
		showingInfoLabel.setText("Five Day Historical");
	}
	
	private void getCurrentWeather(){
		List<Weather> weatherList = new ArrayList<>();
		String location = searchTextField.getText();
		String weatherInfo = "";
		String extraInfo = "";
		
		if(isValidLocation(location)){		
			weatherList = weatherService.getCurrentWeather(location);
			
			// Setting the text of a label
			weatherInfo += "\n\nCurrent Temperature: " + weatherList.get(0).getCurrentTemp() + "\n\n"
					+ "Max Temp: " + weatherList.get(0).getMaxTemp() + "\n\n"
					+ "Min Temp: " + weatherList.get(0).getMinTemp();
			currentWeatherInfoLabel.setText(weatherInfo);
			
			extraInfo += "\n\n\nDescription: " + weatherList.get(0).getDescription() + "\n\n"
					+ "Humidity: " + weatherList.get(0).getHumidity() + "\n\n"
					+ "Pressure: " + weatherList.get(0).getPressure() + "\n";
			
			extraInfoLabel.setText(extraInfo);
			
			// Setting an image
			Image icon = makeImage(weatherList.get(0).getIconURL());
			currentWeatherImageView.setImage(icon);
	
		}
		else{
			searchTextField.setText("Invalid Entry");
		}
	}

	private void getHistoricalWeather(){
		String location = searchTextField.getText();
		List<Weather> weatherList = new ArrayList<>();
		if(isValidLocation(location)){
			weatherList = weatherService.getHistoricalWeather(5, location);
			setDateForWeekLabels(weatherList);
			setImagesForWeekImageView();
			System.out.println(weatherList.get(0).getIconURL());
			setDescriptionLabels(weatherList);	
		}
	}
	public void setDescriptionLabels(List<Weather> weatherList) {
		firstDayWeatherLabel.setText("\n" + weatherList.get(4).getDescription() + "\n\n" 
				+ "Max: " +  weatherList.get(4).getMaxTemp() + "\n\n" + "Min: " + weatherList.get(4).getMinTemp());
		secondDayWeatherLabel.setText("\n" + weatherList.get(3).getDescription() + "\n\n" 
				+ "Max: " +  weatherList.get(3).getMaxTemp() + "\n\n" + "Min: " + weatherList.get(3).getMinTemp());
		thirdDayWeatherLabel.setText("\n" + weatherList.get(2).getDescription() + "\n\n" 
				+ "Max: " +  weatherList.get(2).getMaxTemp() + "\n\n" + "Min: " + weatherList.get(2).getMinTemp());
		fourthDayWeatherLabel.setText("\n" + weatherList.get(1).getDescription() + "\n\n" 
				+ "Max: " +  weatherList.get(1).getMaxTemp() + "\n\n" + "Min: " + weatherList.get(1).getMinTemp());
		fifthDayWeatherLabel.setText("\n" + weatherList.get(0).getDescription() + "\n\n" 
				+ "Max: " +  weatherList.get(0).getMaxTemp() + "\n\n" + "Min: " + weatherList.get(0).getMinTemp());
	}
	public void setImagesForWeekImageView() {
		firstDayImageView.setImage(new Image("assets/noPhoto-icon.png", 70, 70, false, false));
		secondDayImageView.setImage(new Image("assets/noPhoto-icon.png", 70, 70, false, false));
		thirdDayImageView.setImage(new Image("assets/noPhoto-icon.png", 70, 70, false, false));
		fourthDayImageView.setImage(new Image("assets/noPhoto-icon.png", 70, 70, false, false));
		fifthDayImageView.setImage(new Image("assets/noPhoto-icon.png", 70, 70, false, false));
	}
	public void setDateForWeekLabels(List<Weather> weatherList) {
		firstDayLabel.setText(weatherList.get(4).getDate());
		secondDayLabel.setText(weatherList.get(3).getDate());
		thirdDayLabel.setText(weatherList.get(2).getDate());
		fourthDayLabel.setText(weatherList.get(1).getDate());
		fifthDayLabel.setText(weatherList.get(0).getDate());
	}
	
	private void getForecast(){
		
		String location = searchTextField.getText();
		List<Weather> weatherList = new ArrayList<>();
		if(isValidLocation(location)){
		
			weatherList = weatherService.getForecast(location);
			
			Image[] images = new Image[5];
			for(int i = 0; i < 5; i++){
				images[i] = makeImage(weatherList.get(i + 1).getIconURL());
			}
			
			firstDayLabel.setText(weatherList.get(1).getDayOfWeek());
			secondDayLabel.setText(weatherList.get(2).getDayOfWeek());
			thirdDayLabel.setText(weatherList.get(3).getDayOfWeek());
			fourthDayLabel.setText(weatherList.get(4).getDayOfWeek());
			fifthDayLabel.setText(weatherList.get(5).getDayOfWeek());
			
			firstDayImageView.setImage(images[0]);
			secondDayImageView.setImage(images[1]);
			thirdDayImageView.setImage(images[2]);
			fourthDayImageView.setImage(images[3]);
			fifthDayImageView.setImage(images[4]);
			
			firstDayWeatherLabel.setText("\n" + weatherList.get(1).getDescription() + "\n\n" 
					+ "Max: " +  weatherList.get(1).getMaxTemp() + "\n\n" + "Min: " + weatherList.get(1).getMinTemp());
			secondDayWeatherLabel.setText("\n" + weatherList.get(2).getDescription() + "\n\n" 
					+ "Max: " +  weatherList.get(2).getMaxTemp() + "\n\n" + "Min: " + weatherList.get(2).getMinTemp());
			thirdDayWeatherLabel.setText("\n" + weatherList.get(3).getDescription() + "\n\n" 
					+ "Max: " +  weatherList.get(3).getMaxTemp() + "\n\n" + "Min: " + weatherList.get(3).getMinTemp());
			fourthDayWeatherLabel.setText("\n" + weatherList.get(4).getDescription() + "\n\n" 
					+ "Max: " +  weatherList.get(4).getMaxTemp() + "\n\n" + "Min: " + weatherList.get(4).getMinTemp());
			fifthDayWeatherLabel.setText("\n" + weatherList.get(5).getDescription() + "\n\n" 
					+ "Max: " +  weatherList.get(5).getMaxTemp() + "\n\n" + "Min: " + weatherList.get(5).getMinTemp());		
			
		}
		else{
			searchTextField.setText("Invalid Entry");
		}
	}
	
	private Image makeImage(String imageURL){
		
		BufferedImage bufferedImage = null;
		Image image = null;
		URL iconURL = null;
		
		try {
			iconURL = new URL(imageURL);
			bufferedImage = ImageIO.read(iconURL);
			image = SwingFXUtils.toFXImage(bufferedImage, null);

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return image;		
	}
	// Method to validate user input
	private boolean isValidLocation(String location) {
		String cityRegex = "^[a-zA-Z]+(?:[\\s-][a-zA-Z]+)*$"; 
		String zipRegex = "^[0-9]{5}$"; 
		
		boolean validCity = Pattern.matches(cityRegex, location);
		boolean validZipcode = Pattern.matches(zipRegex, location);
		
		if(validCity || validZipcode){
			return true;
		}

		return false;
	}

}
