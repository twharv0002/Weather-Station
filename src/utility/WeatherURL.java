package utility;

import java.net.URL;
import java.net.URLEncoder;
import java.util.regex.Pattern;

public class WeatherURL{

	public static URL createURL(String weatherDataType, String location){
		
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
	
	public static URL createHistoricalURL(double lat, double lon, long time, String location){
		
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
				return null;
			}
		
		return null;
		
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
