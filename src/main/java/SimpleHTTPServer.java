import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

public class SimpleHTTPServer {
    public static void main(String[] args) {
        int port = 8080;
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Serverul HTTP rulează pe portul " + port);

            while (true) {
                try (Socket clientSocket = serverSocket.accept()) {
                    BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    PrintWriter output = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream(), "UTF-8"), true);

                    String requestLine = input.readLine();
                    System.out.println("Cerere primită: " + requestLine);

                    while (input.readLine().length() != 0) { }

                    if (requestLine != null && requestLine.startsWith("GET")) {
                        if (requestLine.contains("/hello")) {
                            // Extrage numele din query string
                            String queryString = requestLine.split(" ")[1];
                            Map<String, String> params = parseQueryString(queryString);

                            String name = params.getOrDefault("name", null);
                            String response;

                            if (name != null) {
                                response = "<html><body><h1>Salut, " + name + "!</h1></body></html>";
                            } else {
                                // Afișează formularul dacă nu s-a furnizat numele
                                response = "<html><body>"
                                        + "<h1>Introdu numele tău</h1>"
                                        + "<form method='GET' action='/hello'>"
                                        + "Nume: <input type='text' name='name' />"
                                        + "<button type='submit'>Trimite</button>"
                                        + "</form>"
                                        + "</body></html>";
                            }

                            output.println("HTTP/1.1 200 OK");
                            output.println("Content-Type: text/html; charset=UTF-8");
                            output.println("Content-Length: " + response.getBytes("UTF-8").length);
                            output.println("");
                            output.println(response);

                        } else {
                            String response = "<html><body><h1>Bine ai venit la serverul meu!</h1></body></html>";
                            output.println("HTTP/1.1 200 OK");
                            output.println("Content-Type: text/html; charset=UTF-8");
                            output.println("Content-Length: " + response.getBytes("UTF-8").length);
                            output.println("");
                            output.println(response);
                        }
                    } else {
                        output.println("HTTP/1.1 400 Bad Request");
                        output.println("Content-Type: text/plain; charset=UTF-8");
                        output.println("Content-Length: 13");
                        output.println("");
                        output.println("Bad Request");
                    }
                } catch (IOException e) {
                    System.err.println("Eroare de I/O: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Eroare la crearea serverului: " + e.getMessage());
        }
    }

    private static Map<String, String> parseQueryString(String queryString) {
        Map<String, String> params = new HashMap<>();
        try {
            if (queryString.contains("?")) {
                String[] parts = queryString.split("\\?", 2);
                if (parts.length > 1) {
                    String[] pairs = parts[1].split("&");
                    for (String pair : pairs) {
                        String[] keyValue = pair.split("=", 2);
                        String key = URLDecoder.decode(keyValue[0], "UTF-8");
                        String value = keyValue.length > 1 ? URLDecoder.decode(keyValue[1], "UTF-8") : "";
                        params.put(key, value);
                    }
                }
            }
        } catch (UnsupportedEncodingException e) {
            System.err.println("Eroare la decodarea URL-ului: " + e.getMessage());
        }
        return params;
    }
}
