package core;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Context {
    public static final int CR=13;
    public static final int LF=10;
    public static final Map<String, String> servletMapping = new HashMap<String, String>();
    public static final Map<String, String> mimeTypeMapping = new HashMap<String, String>();

    static {
        init();
    }

    private static void init(){
        initServletMapping();
        initMimeTypeMapping();
    }

    private static Element getRootFromXML(String url) {
        SAXReader reader = new SAXReader();
        Document doc = null;
        try {
            doc = reader.read(url);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        if(doc==null){
            return null;
        }
        return doc.getRootElement();
    }

    private static void initServletMapping(){
       Element root = getRootFromXML("conf" + File.separator + "servletMapping.xml");
       if(root != null){
           List<Element> list = root.elements("servlets");
           for(Element e : list){
               String uri = e.attributeValue("uri");
               String classname = e.attributeValue("classname");
               servletMapping.put(uri, classname);
           }
       }
    }

    private static void initMimeTypeMapping(){
        Element root = getRootFromXML("conf" + File.separator + "web.xml");
        if(root != null){
            List<Element> list = root.elements("mime-mapping");
            for(Element e : list){
                String extension = e.elementTextTrim("extension");
                String contentType = e.elementTextTrim("mime-type");
                mimeTypeMapping.put(extension, contentType);
            }
        }
    }
}