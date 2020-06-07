package lab_7;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.util.*;

public class Crawler {
    public static void main(String args[]) throws Exception {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Введите адрес сайта: ");
        String host = scanner.nextLine(); // Необходимо ввести адрес веб-сайта
        System.out.println("Введите максимальную глубину поиска: ");
        int depthMax = scanner.nextInt(); // а также ввести максимальную глубину поиска
        new Crawler(host, depthMax); // Передаём введеный адрес и глубину в метод Crawler
    }

    public static final String URL_PREFIX = "https://";

    /* Сканер (Crawler) - это класс, который
перемещается по веб-страницам и ищет URL-адреса, поэтому класс сканера
включает в себя код, который фактически открывает и закрывает
сокеты */
    Crawler(String host, int depthMax) throws IOException {
        // Socket (String host, int port) создает новый сокет из полученной строки с
        // именем хоста и из целого числа с номером порта, и устанавливает соединение
        Socket socket = new Socket(host, 80); // Для HTTP соединений обычно используется порт 80
        URLDepthPair pair = new URLDepthPair("https://" + host + "/"); // Создание пары URL + заданная глубина поиска
        String host1 = host; // Запомним хост
        LinkedList<URLDepthPair> viewed_url = new LinkedList<>(); // Список для просмотреных ссылок
        LinkedList<URLDepthPair> not_viewed_url = new LinkedList<>(); // Список для непросмотренных ссылок
        not_viewed_url.add(pair); // Все ссылки сначала в непросмотренные
        socket.setSoTimeout(2000); // Время ожидания нового сокета
        while ((!not_viewed_url.isEmpty())) { // Пока есть непросмотренные ссылки
            try {
                URL url = new URL(not_viewed_url.getFirst().getUrl()); // Извлечение первого элемента из списка непросмотренных ссылок, получение его URL
                try {
                    // Буферизованный поток ввода символов, который отслеживает номера строки
                    // Метод openStream позволяет создать входной поток для чтения файла ресурса, связанного с созданным объектом класса URL
                    LineNumberReader reader = new LineNumberReader(new InputStreamReader(url.openStream()));
                    String str = reader.readLine();
                    while (str != null) { // Если принимаемая строка не пуста, то
                        // для каждой строки content, содержащейся в принятой str после атрибута href
                        for (String content : str.split("href=\"")) // атрибут href - путь к файлу, на который делается ссылка (URL)
                            try { // Если принимаемая строка содержит href и префикс, а также строка content содержит префикс
                                if (str.contains("href=\"" + URL_PREFIX) && content.startsWith(URL_PREFIX)) {
                                    pair = new URLDepthPair((content.substring(0, content.indexOf("\"")).split("/").length - 3), content.substring(0, content.indexOf("\"")));
                                    if (pair.getDepth() <= depthMax && pair.getUrl().contains(host1)) { // Если полученная глубина меньше максимальной и полученный URL содержится в хосте
                                        int size_not_viewed = not_viewed_url.size();  // Кол-во непросмотренных ссылок
                                        int size_viewed = viewed_url.size(); // Кол-во просмотренных ссылок
                                        boolean not_viewed_state = false;
                                        boolean viewed_state = false;
                                        int count1 = 0;
                                        int count2 = 0;
                                        // Пока состояние непросмотренных false и счетчик меньше кол-ва непросмотренных или состояние просмотренных false и счетчик меньше кол-ва просмотренных
                                        while ((not_viewed_state == false && count1 < size_not_viewed) || (viewed_state == false && count2 < size_viewed)) {
                                            if (count1 < size_not_viewed) {  // Если счетчик меньше кол-ва непросмотренных ссылок
                                                if (not_viewed_url.get(count1).getUrl().contains(pair.getUrl())) {
                                                    not_viewed_state = true; // Изменяем статус на true
                                                }
                                                count1++; // Увеличиваем счётчик
                                            }
                                            if (count2 < size_viewed) { // Если счетчик меньше кол-ва просмотренных ссылок
                                                if (viewed_url.get(count2).getUrl().contains(pair.getUrl())) {
                                                    viewed_state = true; // Изменяем статус на true
                                                }
                                                count2++; // Увеличиваем счётчик
                                            }
                                        }
                                        if (not_viewed_state == false && viewed_state == false) {
                                            not_viewed_url.add(pair); // Добавить пару в список непросмотренных
                                        }
                                    }
                                }
                            } catch (StringIndexOutOfBoundsException e) { // Поймать исключение
                            }

                        str = reader.readLine(); // Читаем следующую строку
                    }
                    reader.close(); // закрытие чтения
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } catch (MalformedURLException ex) {
                ex.printStackTrace();
            }
            socket.close(); // закрытие сокета
            viewed_url.add(not_viewed_url.getFirst());
            not_viewed_url.removeFirst(); // Удаление ссылки из списка непросмотренных
            System.out.println("Просмотренная cсылка: " + viewed_url.getLast().getUrl()); // Вывод последней просмотренной ссылки
            System.out.println("Всего проверено ссылок: " + viewed_url.size());
        }
    }
}
