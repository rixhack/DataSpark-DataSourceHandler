/**
 * Copyright (c) 2017, The Glowsticks.
 * All rights reserved.
 */

package nl.tue.dataspark.datasourcehandler;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Main class
 * 
 * @author Luuk Godtschalk
 */
public class Main {
    private static QueryHandler queryHandler;

    public static void main(String[] args) throws Exception {
        queryHandler = new QueryHandler();
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/datavalue", new MyHandler());
        server.start();
    }

    static class MyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            String dataSource = parseDataQuery(t);
            String value = null;
            try {
                value = queryHandler.getDataValue(dataSource).toString();
            } catch (Exception ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
            String response = "This is the response for data source " + dataSource + ":\n" + value;
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
    
    public static String parseDataQuery(HttpExchange t) {
        String[] parts = t.getRequestURI().toString().split("/");
        return parts[2];
    }
}
