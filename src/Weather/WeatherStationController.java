package weather;

import java.awt.Desktop.Action;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONML;
import org.json.JSONObject;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Label;
import javafx.scene.control.Button;

public class WeatherStationController {
	
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

	
	private static final int HISTORICAL_DAYS = 5; // Number of days back for historical weather
	private List<Weather> weatherList = new ArrayList<>(); // List to store weather objects
	
	@FXML
	void onSearchButtonClick(ActionEvent event){
		getCurrentWeather();
		getForecast();
		showingInfoLabel.setText("Forecast");
	}
	// Test action listeners
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
	
	// Test methods
	private void getCurrentWeather(){
		
		String currentWeatherLabelTitle = "Current Weather";
		currentWeatherLabel.setText(currentWeatherLabelTitle);
		
		String location = searchTextField.getText(); // Get user input from text field
		String weatherInfo = "";
		String extraInfo = "";
		
		if(isValidLocation(location)){ // Validates user input			
			// Populate weatherList with appropriate data
			weatherList = WeatherService.getCurrentWeather(location);
			
			// Setting the text of a label
			weatherInfo += "\n\nCurrent Temperature: " + weatherList.get(0).currentTemp + "\n\n"
					+ "Max Temp: " + weatherList.get(0).maxTemp + "\n\n"
					+ "Min Temp: " + weatherList.get(0).minTemp;
			currentWeatherInfoLabel.setText(weatherInfo);
			
			extraInfo += "\n\n\nDescription: " + weatherList.get(0).description + "\n\n"
					+ "Humidity: " + weatherList.get(0).humidity + "\n\n"
					+ "Pressure: " + weatherList.get(0).pressure + "\n";
			
			extraInfoLabel.setText(extraInfo);
			
			// Setting an image
			Image icon = makeImage(weatherList.get(0).iconURL);
			currentWeatherImageView.setImage(icon);
	
		}
		else{
			searchTextField.setText("Invalid Entry");
		}
	}

	private void getHistoricalWeather(){
		String location = searchTextField.getText();
		
		if(isValidLocation(location)){
			weatherList = WeatherService.getHistoricalWeather(5, location);
			
			firstDayLabel.setText(weatherList.get(4).date);
			secondDayLabel.setText(weatherList.get(3).date);
			thirdDayLabel.setText(weatherList.get(2).date);
			fourthDayLabel.setText(weatherList.get(1).date);
			fifthDayLabel.setText(weatherList.get(0).date);
			
			firstDayImageView.setImage(new Image("application/noPhoto-icon.png", 70, 70, false, false));
			secondDayImageView.setImage(new Image("application/noPhoto-icon.png", 70, 70, false, false));
			thirdDayImageView.setImage(new Image("application/noPhoto-icon.png", 70, 70, false, false));
			fourthDayImageView.setImage(new Image("application/noPhoto-icon.png", 70, 70, false, false));
			fifthDayImageView.setImage(new Image("application/noPhoto-icon.png", 70, 70, false, false));
			
			System.out.println(weatherList.get(0).iconURL);
			
			firstDayWeatherLabel.setText("\n" + weatherList.get(4).description + "\n\n" 
					+ "Max: " +  weatherList.get(4).maxTemp + "\n\n" + "Min: " + weatherList.get(4).minTemp);
			secondDayWeatherLabel.setText("\n" + weatherList.get(3).description + "\n\n" 
					+ "Max: " +  weatherList.get(3).maxTemp + "\n\n" + "Min: " + weatherList.get(3).minTemp);
			thirdDayWeatherLabel.setText("\n" + weatherList.get(2).description + "\n\n" 
					+ "Max: " +  weatherList.get(2).maxTemp + "\n\n" + "Min: " + weatherList.get(2).minTemp);
			fourthDayWeatherLabel.setText("\n" + weatherList.get(1).description + "\n\n" 
					+ "Max: " +  weatherList.get(1).maxTemp + "\n\n" + "Min: " + weatherList.get(1).minTemp);
			fifthDayWeatherLabel.setText("\n" + weatherList.get(0).description + "\n\n" 
					+ "Max: " +  weatherList.get(0).maxTemp + "\n\n" + "Min: " + weatherList.get(0).minTemp);	
		}
	
	}
	
	private void getForecast(){
		
		String location = searchTextField.getText();
		
		if(isValidLocation(location)){
		
			weatherList = WeatherService.getForecast(location);
			
			Image[] images = new Image[5];
			for(int i = 0; i < 5; i++){
				images[i] = makeImage(weatherList.get(i + 1).iconURL);
			}
			
			firstDayLabel.setText(weatherList.get(1).dayOfWeek);
			secondDayLabel.setText(weatherList.get(2).dayOfWeek);
			thirdDayLabel.setText(weatherList.get(3).dayOfWeek);
			fourthDayLabel.setText(weatherList.get(4).dayOfWeek);
			fifthDayLabel.setText(weatherList.get(5).dayOfWeek);
			
			firstDayImageView.setImage(images[0]);
			secondDayImageView.setImage(images[1]);
			thirdDayImageView.setImage(images[2]);
			fourthDayImageView.setImage(images[3]);
			fifthDayImageView.setImage(images[4]);
			
			firstDayWeatherLabel.setText("\n" + weatherList.get(1).description + "\n\n" 
					+ "Max: " +  weatherList.get(1).maxTemp + "\n\n" + "Min: " + weatherList.get(1).minTemp);
			secondDayWeatherLabel.setText("\n" + weatherList.get(2).description + "\n\n" 
					+ "Max: " +  weatherList.get(2).maxTemp + "\n\n" + "Min: " + weatherList.get(2).minTemp);
			thirdDayWeatherLabel.setText("\n" + weatherList.get(3).description + "\n\n" 
					+ "Max: " +  weatherList.get(3).maxTemp + "\n\n" + "Min: " + weatherList.get(3).minTemp);
			fourthDayWeatherLabel.setText("\n" + weatherList.get(4).description + "\n\n" 
					+ "Max: " +  weatherList.get(4).maxTemp + "\n\n" + "Min: " + weatherList.get(4).minTemp);
			fifthDayWeatherLabel.setText("\n" + weatherList.get(5).description + "\n\n" 
					+ "Max: " +  weatherList.get(5).maxTemp + "\n\n" + "Min: " + weatherList.get(5).minTemp);		
			
		}
		else{
			searchTextField.setText("Invalid Entry");
		}
	}
	
	// Method to simplify making image icons
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
