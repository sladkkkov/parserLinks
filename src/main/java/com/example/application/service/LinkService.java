package com.example.application.service;

import com.example.application.Link;
import lombok.Data;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



@Data
public class LinkService {

    private String globalLink;
    private LinkedHashSet <String> linksSet;
    private List<Integer> size;
    private long count = 1;

    public LinkService(String globalLink) {
        this.globalLink = globalLink;
        this.linksSet = new LinkedHashSet <>();
        this.size = new ArrayList<>();
    }


    public void getLinks(String link) throws IOException, ParserConfigurationException, SAXException {

        Set<String> links = null;
        if (!linksSet.contains(link)) {
            try {
                StringBuffer html = getAllLinksByUrlConnection(link);
                if (linksSet.add(link)) {
                    size.add(html.length());
                    System.out.println(link);
                }
                links = parseHtml(html);
                for (String link1 : links) {
                    getLinks(link1);

                }

            } catch (IOException e) {
                System.err.println("For '" + link + "': " + e.getMessage());
            }
        }
    }

    /**
     * Метод для получения html через HttpURLConnection
     */
    public StringBuffer getAllLinksByUrlConnection(String link) throws IOException, ParserConfigurationException, SAXException {
        String s = "http://" + link;
        URL url = new URL(s);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("GET");
        connection.setRequestProperty("Content-Type", "href");
        connection.setConnectTimeout(10000);
        if (connection.getResponseCode() != 200) {
            return new StringBuffer(0);
        }
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        return response;
    }

    /**
     * Метод для получения html через сокет
     */
    public StringBuffer getAllLinksBySocket() throws IOException {

        String data = URLEncoder.encode("key1", StandardCharsets.UTF_8) + "=" + URLEncoder.encode("value1", StandardCharsets.UTF_8);

        Socket socket = new Socket(globalLink, 80);

        BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
        wr.write("GET / HTTP/1.0\r\n");
        wr.write("Host:" + globalLink + "\r\n");
        wr.write("\r\n");
        wr.write(data);
        wr.flush();

        BufferedReader rd = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = rd.readLine()) != null) {
            response.append(inputLine);
        }
        wr.close();
        rd.close();
        return response;
    }

    /**
     * Метод для парсинга всех ссылок из html
     */
    public Set<String> parseHtml(StringBuffer html) throws IOException {
        Set<String> list = new HashSet<>();
        String regex = "<a href=\"(\\S*)\"";
        Matcher m = Pattern.compile(regex).matcher(html);
        while (m.find()) {
            if (!m.group(1).startsWith("http")) {
                String s = globalLink + m.group(1);
                list.add(s);
            }
        }
        return list;
    }

    public Set<Link> sortLinks(Set<String> links) throws IOException, ParserConfigurationException, SAXException {
        Set<Link> linkSet = new HashSet<>();
        for (String link : links) {
            linkSet.add(new Link((long) (linkSet.size() + 1), link, size.get(linkSet.size())));

        }
        return linkSet;
    }

    public int getSizeSite(){
        int sum =0;
        for (Integer i : size) {
            sum+=i;
        }
        return sum;
    }
}

