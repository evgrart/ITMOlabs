package backend;

import com.fastcgi.FCGIInterface;

public class Main {
    public static void main(String[] args) {
        FCGIInterface fcgi = new FCGIInterface();
        while (fcgi.FCGIaccept() >= 0) {
            Request request = new Request(fcgi);
            request.process();
        }
    }    
}