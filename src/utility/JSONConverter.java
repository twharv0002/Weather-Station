package utility;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import weather.Weather;

public class JSONConverter {
	
	public static List<Weather> convertJSONForecast(JSONObject forecast){	
		List<Weather> weatherList = new ArrayList<>();
		
		try {
			JSONArray list = forecast.getJSONArray("list");
			
			for(int i = 0; i < list.length(); ++i){
				JSONObject day = list.getJSONObject(i);
				JSONObject temps = day.getJSONObject("temp");
				JSONObject weather = day.getJSONArray("weather").getJSONObject(0);
				
				weatherList.add(new Weather(
						day.getLong("dt"),
						temps.getDouble("min"),
						temps.getDouble("max"),
						day.getDouble("humidity"),
						weather.getString("description"),
						weather.getString("icon"),
						day.getDouble("pressure"),
						temps.getDouble("day")
						));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return weatherList;
	}
	
	public static List<Weather> convertJSONcurrentWeather(JSONObject current){		
		List<Weather> weatherList = new ArrayList<>();
			
			try {
				JSONObject main = current.getJSONObject("main");
				JSONObject weather = current.getJSONArray("weather").getJSONObject(0);
				
				weatherList.add(new Weather(
						current.getLong("dt"),
						main.getDouble("temp_min"),
						main.getDouble("temp_max"),
						main.getDouble("humidity"),
						weather.getString("description"),
						weather.getString("icon"),
						main.getDouble("pressure"),
						main.getDouble("temp")
						));
							
			} catch (JSONException e) {
				e.printStackTrace();
			}	
			return weatherList;
	}
	
	public static Weather convertJSONHistorical(JSONObject historical){
		Weather weather = null;
		try {
			JSONObject daily = historical.getJSONObject("daily");
			JSONObject currently = historical.getJSONObject("currently");
			JSONObject data = daily.getJSONArray("data").getJSONObject(0);
			
			weather = new Weather(
					currently.getLong("time"),
					data.getDouble("temperatureMin"), 
					data.getDouble("temperatureMax"), 
					currently.getDouble("humidity"), 
					currently.getString("summary"),
					data.getString("icon"),
					currently.getDouble("pressure"),
					currently.getDouble("temperature")
					);
						
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return weather;
	}
}
