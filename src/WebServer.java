import java.io.IOException;
import java.io.OutputStream;
//import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;

import redis.clients.jedis.Jedis;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
//import com.sun.net.httpserver.HttpServer;

public class WebServer {

   public static class ActiveKML implements HttpHandler {
	   private static ActiveKML instance;
	   private ActiveKML(){}
	   public static ActiveKML getInstance()
	   {
		   if(instance == null)
			   instance = new ActiveKML();
		   return instance;
	   }
        public void handle(HttpExchange t) throws IOException {
            String response = aircraft_active_kml();//aircraft_active_kml(); //new String(Files.readAllBytes(Paths.get("active.kml")));//aircraft_active_kml(); // create a kml response
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }


  public   static class MapBasic implements HttpHandler {
	  private static MapBasic instance;
	  private MapBasic(){};
	  public static MapBasic getInstance()
	  {
		  if(instance == null)
			  instance = new MapBasic();
		  return instance;
	  }
public void handle ( HttpExchange t ) throws IOException {
   System.err.println ( "map.basic acessed" ); // send basicMap.html
   String response = new String ( Files.readAllBytes (Paths.get("basicMap.html") ) );
   t.sendResponseHeaders ( 200, response.length() );
   OutputStream os = t.getResponseBody();
   os.write ( response.getBytes() );
   os.close ();
}
    }

    private static Jedis jed = new Jedis ("localhost");
    private static String aircraft_active_kml () {
    //boolean print = false;	
StringBuilder kml = new StringBuilder (256);
// creates a KML string from active.aircraft.* in redis
// basicMap.html expects "http://localhost:3000/aircraft_active_kml
// set preamble for the kml string
kml.append ( "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"     );
kml.append ( "<kml xmlns=\"http://www.opengis.net/kml/2.2\">\n" );
kml.append ( "<Document>\n" );
Set<String> keys =   jed.keys ("*"); //( "aircraft.active.*" );
for ( String k : keys ) 
{
   // get the active positions from redis
   String position = jed.get ( k );
   //System.out.println(position);
   // 1385651813.7752786,3956954,9.536544799804688,48.21986389160156,18850
   // ts                 icao    lon               lat               alt
   String [] entry = position.split (",");
  // String lon = 
   //System.err.println ( entry[5] + "," + entry[4] + " size:"+entry.length);
   if (!entry[5].equals("-1.0") && !entry[4].equals("-1.0"))
   {
   kml.append ( "<Placemark>\n<name>" + entry[1] + "</name>\n" +
		   		"<Point>\n<coordinates>" + entry[5] + "," + entry[4] +
		   		",0</coordinates>\n</Point>\n</Placemark>\n" );
   }
}
	kml.append ( "</Document>\n" );
	kml.append ( "</kml>\n\n" );
	return kml.toString();
}

	public String toString()
	{
		return super.toString()+
			", Instance: " +getInstance()+
			", ResponseBody: " +getResponseBody()+
			", Bytes: " +getBytes()+
	}/* toString() hinzugefuegt - glkeit00 */
}	
