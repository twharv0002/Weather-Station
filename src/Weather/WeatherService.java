package weather;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import utility.JSONConverter;

public class WeatherService {
	
	private List<Weather> weatherList = new ArrayList<>();
	
	public List<Weather> getHistoricalWeather(int days, String location){
		populateWeatherListHistorical(days, location);
		return weatherList;
	}
	
	public List<Weather> getCurrentWeather(String location){
		populateWeatherListCurrent(location);
		return weatherList;
	}
	
	public List<Weather> getForecast(String location){
		populateWeatherListForecast(location);
		return weatherList;
	}	
		
		private void convertJSONHistorical(JSONObject historical){

			try {
				JSONObject daily = historical.getJSONObject("daily");
				JSONObject currently = historical.getJSONObject("currently");
				JSONObject data = daily.getJSONArray("data").getJSONObject(0);
				
				weatherList.add(new Weather(
						currently.getLong("time"),
						data.getDouble("temperatureMin"), 
						data.getDouble("temperatureMax"), 
						currently.getDouble("humidity"), 
						currently.getString("summary"),
						data.getString("icon"),
						currently.getDouble("pressure"),
						currently.getDouble("temperature")
						));
							
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		// Create URL using the Open Weather Station API
		private static URL createURL(String weatherDataType, String location){
			
			String apiKey = "f8eed5cc021d0b009e7d00a60e1c47b4";
			String baseURL = "http://api.openweathermap.org/data/2.5/";
			String locationURL = baseURL + weatherDataType;
			
			// Validate input and determine if entry is a city or a zipcode
				
				if(validateEntry(location) == 0){
					locationURL += "?q=";
				}
				else if(validateEntry(location) == 1){
					locationURL += "?zip=";
					
				}
				else{
					return null;
				}
			try{	
				String encodedLocation = URLEncoder.encode(location, "UTF-8");
				
				return new URL(locationURL + encodedLocation + "&units=imperial&cnt=16&APPID=" + apiKey);
				
			}
			catch(Exception e){
				e.printStackTrace();
			}
			
			return null;
		}
		
		// Create URL using The Dark Sky Forecast API
		private static URL createHistoricalURL(double lat, double lon, long time, String location){
			
			String apiKey = "03f925195946a60805429c61e0ffd515/";
			String baseURL = "https://api.forecast.io/forecast/";
			
			// Validate input and determine if entry is a city or a zipcode
				
				if(!(validateEntry(location) == -1)){
					
					try {
						String encodedURL = URLEncoder.encode(lat + "," + lon + "," + time, "UTF-8");
						return new URL(baseURL + apiKey + encodedURL);
						
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				else{
					//myTextField.setText("Invalid Entry");
					return null;
				}
			
			return null;
			
		}
		
		// getWeatherTask opens connection to the given URL and returns a JSON object
		private static JSONObject getWeatherTask(URL url){
			
			HttpURLConnection con = null;
			
			try{
				con = (HttpURLConnection)url.openConnection();
				int response = con.getResponseCode();
				
				if(response == HttpURLConnection.HTTP_OK){
					StringBuilder builder = new StringBuilder();
					
					try(BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()))){
						String line;
						
						while((line = reader.readLine()) != null ){
							builder.append(line);
						}
					}
					catch(IOException e){
						e.printStackTrace();
					}
					
					return new JSONObject(builder.toString());
				}
				else{
					System.out.println(response);
				}
			}
			catch(Exception e){
				e.printStackTrace();
			}
			
			return null;
			
		}
		// getCoords method - uses googles geocoding API to translate names to lat and lng coords.
		// returns an array of two elements: lat and lng, respectively.
		public static double[] getCoords(String area){
			
			double[] coords = new double[2];
			String googleAPIKey = "AIzaSyBuihsZXM2hWPZfgOUOZ5uHNlRxLxb2zyQ";
			
			try {
				URL url = new URL("https://maps.googleapis.com/maps/api/geocode/json?address=" + area + "&key" + googleAPIKey);
				JSONObject coordObject = getWeatherTask(url);
				JSONObject location = coordObject.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location");
				
				coords[0] = location.getDouble("lat");
				coords[1] = location.getDouble("lng");
				
				return coords;
		
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		
		private void populateWeatherListHistorical(int days, String location)
		{
			weatherList.clear();
			
			try {			
				long day = System.currentTimeMillis() / 1000L; // UNIX time
				int secInDay = 86400;
				int remainingTime = days * 86400;
				
				double[] locationCoords = getCoords(location);
				System.out.println("Lat: " + locationCoords[0]); // Testing
				System.out.println("Lng: " + locationCoords[1]); // Testing
				
				URL url = createHistoricalURL(locationCoords[0], locationCoords[1], day, location);
				
				if(url != null){
					
					do{
						url = createHistoricalURL(locationCoords[0], locationCoords[1], day - secInDay, location);
						
						JSONObject historical = getWeatherTask(url);	
						weatherList.add(JSONConverter.convertJSONHistorical(historical));
						//convertJSONHistorical(historical);
						remainingTime -= secInDay;
						day -= secInDay;
							
					}while(remainingTime > 0);
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		private void populateWeatherListForecast(String location){
			URL url = createURL("forecast/daily", location);
			
			if(url != null){
				JSONObject forecast = getWeatherTask(url);
				weatherList = JSONConverter.convertJSONForecast(forecast);
			}
		}
		
		private void populateWeatherListCurrent(String location){
			
			URL url = createURL("weather", location);
			
			if(url != null){
				
				JSONObject current = getWeatherTask(url);
				weatherList = JSONConverter.convertJSONcurrentWeather(current);
			}
		}
		
		private static int validateEntry(String location){
			String cityRegex = "^[a-zA-Z]+(?:[\\s-][a-zA-Z]+)*$"; 
			String zipRegex = "^[0-9]{5}$"; 
			
			boolean validCity = Pattern.matches(cityRegex, location);
			boolean validZipcode = Pattern.matches(zipRegex, location);
				
			if(validCity){
				return 0;
			}
			else if(validZipcode){
				return 1;
			}
			else{
				return -1;
			}
		}
		
}
