package lab_7;

public class URLDepthPair {
    String url;
    int depth;

    URLDepthPair(int depth,String url){   //конструктор принимающий ссылку и глубину
        this.depth = depth;
        this.url = url;
    }
    URLDepthPair(String url){   //конструктор принимающий параметр ссылки
        this.url =url;
        this.depth = 0;
    }

    public int getDepth() { //getter depth
        return depth;
    }

    public String getUrl() { //getter url
        return url;
    }
    public String toString() {  //getter url + depth
        return(url + " "+ depth);
    }
}
