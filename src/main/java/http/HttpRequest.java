package http;

import core.Context;

import java.io.*;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private String requestLine;
    private final Map<String, String> headers = new HashMap<>();
    private final Map<String, String> params = new HashMap<>();

    public HttpRequest(InputStream in){
        parseRequestLine(in);
        parseHeaders(in);
        parseContent(in);
    }

    private void parseRequestLine(InputStream in){
        String line = readLine(in);
        if(!"".equals(line)){
            String uri = line.split("\\s")[1];
            if(uri.contains("?")){
                int index = uri.indexOf("?");
                requestLine = uri.substring(0, index);
                String queryStr = uri.substring(index+1);
                parseQueryStr(queryStr);
            }else{
                requestLine = uri;
            }
            System.out.println("解析得请求行信息："+line);
        }
    }

    private void parseQueryStr(String queryStr){
        try {
            queryStr = URLDecoder.decode(queryStr, "GBK");
            String[] data = queryStr.split("&");
            for(String s : data){
                String[] args = s.split("=");
                if(args.length == 2){
                    params.put(args[0], args[1]);
                }else{
                    params.put(args[0], "");
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    private void parseHeaders(InputStream in){
        while(true){
            String line = readLine(in);
            if("".equals(line)){
                break;
            }
            int index = line.indexOf(":");
            String key = line.substring(0, index);
            String value = line.substring(index + 1);
            headers.put(key, value);
        }

        if(headers.size()!=0){
            System.out.println("解析得请求头信息如下：");
            headers.forEach((k,v) -> System.out.println("  " + k + ":" +v));
        }else{
            System.out.println("解析得请求头信息如下：无");
        }
    }

    private void parseContent(InputStream in){
        String contentType = this.headers.get("Content-Type");
        System.out.println("开始解析请求正文啦啦啦啦啦啦啦啦啦:");
        System.out.println("  contentType:"+contentType);
        if("application/x-www-form-urlencoded".equals(contentType)){
            System.out.println(1);
            int contentLength = Integer.parseInt(this.headers.get("Content-Length"));
            System.out.println("  contentLength:"+contentLength);
            byte[] data = new byte[contentLength];
            try {
                in.read(data);
                String content = new String(data);
                System.out.println("  content:"+content);
                parseQueryStr(content);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if(params.size()!=0){
            System.out.println("解析得请求参数信息如下：");
            params.forEach((k,v) -> System.out.println("  " + k + "=" +v));
        }else {
            System.out.println("解析得请求参数信息如下：无");
        }
    }

    private String readLine(InputStream in) {
        StringBuilder builder = new StringBuilder();
        try {
            int d;
            char c1=0,c2;
            while((d=in.read())!=-1){
                c2=(char)d;
                if(c1 == Context.CR && c2 == Context.LF){
                    break;
                }
                builder.append(c2);
                c1 = c2;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return builder.toString().trim();
    }

    public String getRequestLine() {
        return requestLine;
    }

    public String getParameter(String paramName){
        return params.get(paramName);
    }
}
