package weather;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;

import utility.JSONConverter;
import utility.WeatherURL;

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
		
		// getWeatherTask opens connection to the given URL and returns a JSON object
		private JSONObject getWeatherTask(URL url){
			
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
		public double[] getCoords(String area){
			
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
				long day = System.currentTimeMillis() / 1000L;
				int secInDay = 86400;
				int remainingTime = days * 86400;
				
				double[] locationCoords = getCoords(location);
				System.out.println("Lat: " + locationCoords[0]);
				System.out.println("Lng: " + locationCoords[1]);
				
				URL url = WeatherURL.createHistoricalURL(locationCoords[0], locationCoords[1], day, location);
				
				if(url != null){
					
					do{
						url = WeatherURL.createHistoricalURL(locationCoords[0], locationCoords[1], day - secInDay, location);
						
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
			//URL url = createURL("forecast/daily", location);
			URL url = WeatherURL.createURL("forecast/daily", location);
			
			if(url != null){
				JSONObject forecast = getWeatherTask(url);
				weatherList = JSONConverter.convertJSONForecast(forecast);
			}
		}
		
		private void populateWeatherListCurrent(String location){
			
			URL url = WeatherURL.createURL("weather", location);
			
			if(url != null){
				
				JSONObject current = getWeatherTask(url);
				weatherList = JSONConverter.convertJSONcurrentWeather(current);
			}
		}
		
}
