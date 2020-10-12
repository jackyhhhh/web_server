package http;

import core.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

public class HttpResponse {
    private final OutputStream out;
    private final Map<String, String> headers = new HashMap<>();
    private File entity;
    private int statusCode;
    private String statusMsg;

    public HttpResponse(OutputStream out){
        this.out = out;
    }

    public void flush(){
        System.out.println("开始响应请求：");
        sendStatusLine();
        sendHeaders();
        sendContent();
    }

    private void sendStatusLine(){
        String statusLine = "HTTP/1.1 "+statusCode+" "+statusMsg;
        println(statusLine);
        System.out.println("状态行信息发送完毕！--\""+statusLine+"\"");
    }

    private void sendHeaders(){
        headers.forEach((k,v) -> println(k+":"+v));
        println("");
        System.out.println("响应头信息发送完毕！");
    }

    private void sendContent(){
        FileInputStream fis = null;
        try{
            fis = new FileInputStream(entity);
            byte[] data = new byte[1024*10];
            int len;
            while((len=fis.read(data))!=-1){
                out.write(data, 0, len);
            }
            System.out.println("响应正文信息发送完毕！");
        }catch (IOException e){
            e.printStackTrace();
        } finally {
            if(fis!=null){
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void println(String line){
        byte[] data = line.getBytes(StandardCharsets.ISO_8859_1);
        try {
            out.write(data);
            out.write(Context.CR);
            out.write(Context.LF);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void forward(File file){
        int index = file.getName().lastIndexOf(".");
        String extension = file.getName().substring(index + 1);
        String contentType = Context.mimeTypeMapping.get(extension);
        int contentLength = (int)file.length();

        setContentLength(contentLength);
        setContentType(contentType);
        setStatusCode(Context.STATUS_200_OK);
        setEntity(file);
        flush();
    }
    public void forward(File file, int statusCode){
        int index = file.getName().lastIndexOf(".");
        String extension = file.getName().substring(index + 1);
        String contentType = Context.mimeTypeMapping.get(extension);
        int contentLength = (int)file.length();

        setContentLength(contentLength);
        setContentType(contentType);
        setStatusCode(statusCode);
        setEntity(file);
        flush();
    }

    public void setContentLength(int contentLength){
        headers.put("Content-Length", contentLength+"");
    }

    public void setContentType(String contentType){
        headers.put("Content-Type", contentType);
    }

    public void setEntity(File file) {
        entity = file;
    }

    public void setStatusCode(int statusCode){
        this.statusCode = statusCode;
        this.statusMsg = Context.statusMsgMapping.get(statusCode);
    }
}
