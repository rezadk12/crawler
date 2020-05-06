package crawler;

import com.opencsv.CSVWriter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import utils.Utils;


import java.io.*;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;

import java.util.List;
import java.util.Scanner;


public class BasicWebCrawler extends Thread {

    private static String filePath;
    private static File file;
    private static FileOutputStream os;
    private static CSVWriter writer;
    private static List<String> links = new ArrayList<String>();

    public void getNewsProperty() {
        try {
            file = new File(filePath);
            os = new FileOutputStream(file);
            os.write(0xef);  // set utf-8 unicode
            os.write(0xbb);
            os.write(0xbf);
            writer = new CSVWriter(new OutputStreamWriter(os));
            String[] header = {"Code", "Title", "body", "date", "category", "link", "label", "Subtitle"};
            writer.writeNext(header);
            for (String url : links) {
                try {
                    Document document = Jsoup.connect(url).get();
                    String newsCode = Utils.arabicToDecimal(document.select("div.news_id_c").text().replace("کد خبر", "").replaceAll(" ", ""));
                    String newsTitle = document.select("h1.title").text();
                    String newsBody = document.select("div.body").text();
                    String newsDay = document.select("div.news_pdate_c").text().replace("تاریخ انتشار:", "");
                    String newsCategory = document.select("div.news_path a:last-child").text();
                    String newsLink = document.select("div.short-link a.link_en").text();
                    String newsLabel = document.select("a.tags_item").text();
                    String newsSubtitle = document.select("div.subtitle").text();
                    String[] data1 = {newsCode, newsTitle, newsBody, newsDay, newsCategory, newsLink, newsLabel, newsSubtitle};
                    writer.writeNext(data1);
                    System.out.println(url);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            writer.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }


    }

    public int showMwnu() {
        int catId = 0;
        System.out.println("az menu shomare mozo mored alaghe baraye khazesh ra vared konid:");
        System.out.println("1.hame  2.siasi  3.eghtesadi  4.ejtemaei  5.varzeshi");
        System.out.println("6.elmi  7.farhangi  8.havades  9.fanavari  10.sargarmi");
        System.out.println("11.omoomi  12.karbaran  13.goftogo  14.safar  15.salamat");
        Scanner userInputScanner = new Scanner(System.in);
        int num = userInputScanner.nextInt();
        switch (num) {
            case 1:
                catId = -1;
                break;
            case 2:
                catId = 1;
                break;
            case 3:
                catId = 4;
                break;
            case 4:
                catId = 5;
                break;
            case 5:
                catId = 6;
                break;
            case 6:
                catId = 7;
                break;
            case 7:
                catId = 8;
                break;
            case 8:
                catId = 9;
                break;
            case 9:
                catId = 14;
                break;
            case 10:
                catId = 15;
                break;
            case 11:
                catId = 21;
                break;
            case 12:
                catId = 22;
                break;
            case 13:
                catId = 23;
                break;
            case 14:
                catId = 24;
                break;
            case 15:
                catId = 25;
                break;
            default:
                showMwnu();
                break;


        }
        return catId;
    }

    public static void main(String[] args) throws URISyntaxException, IOException {
        try {
            BasicWebCrawler basicWebCrawler = new BasicWebCrawler();
            Scanner userInputScanner = new Scanner(System.in);
            System.out.println("tarikh aghaz ra be format yyyy/mm/dd vared konid: ");
            String startDay = userInputScanner.nextLine();
            System.out.println("tarikh payan ra be format yyyy/mm/dd vared konid: ");
            String endDay = userInputScanner.nextLine();
            if (!(Utils.validateJavaDate(startDay) && Utils.validateJavaDate(endDay))) {
                main(null);
            }

            int catId = basicWebCrawler.showMwnu();
            int serviceId = -1;
            if (catId > 0) {
                serviceId = 1;
            }
            System.out.println("adrese folder dataset ra vared konid:(dataset.csv dar in folder zakhire khahad shod) ");
            String path = userInputScanner.nextLine();
            if (path.lastIndexOf("\\") == path.length() - 1) {
                filePath = path + "dataset.csv";
            } else {
                filePath = path + "\\dataset.csv";
            }
            Document document = Jsoup.connect("https://www.asriran.com/fa/archive?service_id=" + serviceId + "&sec_id=-1&cat_id=" + catId + "&rpp=100&from_date=" + startDay + "&to_date=" + endDay + "&p=1").get();
            Elements linksOnPage = document.select("div.archive_content a.title5");  // extract link of news
            String pagination = document.select("div#pager").text(); // extract pagination
            int sd = 1;
            if (!pagination.equals("")) {
                sd = Integer.parseInt(Utils.arabicToDecimal(pagination.replaceAll("►", "").replaceAll("◄", "").split("از")[1].replaceAll(" ", "")));
            }

            for (Element page : linksOnPage) {
                links.add(page.attr("abs:href"));
            }
            for (int i = 2; i <= sd; i++) {
                document = Jsoup.connect("https://www.asriran.com/fa/archive?service_id=" + serviceId + "&sec_id=-1&cat_id=" + catId + "&rpp=100&from_date=" + startDay + "&to_date=" + endDay + "&p=1").get();
                linksOnPage = document.select("div.archive_content a.title5");
                for (Element page : linksOnPage) {
                    links.add(page.attr("abs:href"));
                }
            }
            System.out.println(links.size());
            basicWebCrawler.getNewsProperty();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

}