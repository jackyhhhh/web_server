package core;

import http.HttpRequest;
import http.HttpResponse;
import servlets.HttpServlet;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;
import java.net.Socket;

public class WebServer {
    private ServerSocket serverSocket;

    public WebServer(){
        try {
            serverSocket = new ServerSocket(8080);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("InfiniteLoopStatement")
    public void start(){
        try {
            while(true){
                Socket socket = serverSocket.accept();
                ClientHandler handler = new ClientHandler(socket);
                Thread t = new Thread(handler);
                t.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        WebServer server = new WebServer();
        server.start();
    }

    private static class ClientHandler implements Runnable{
        private Socket socket;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run(){
            try {
                HttpRequest request = new HttpRequest(socket.getInputStream());
                HttpResponse response = new HttpResponse(socket.getOutputStream());
                String requestLine = request.getRequestLine();

                if(Context.servletMapping.containsKey(requestLine)){
                    String classname = Context.servletMapping.get(requestLine);
                    try {
                        Class cls = Class.forName(classname);
                        HttpServlet servlet = (HttpServlet) cls.getConstructor().newInstance();
                        servlet.service(request, response);
                    } catch (ClassNotFoundException
                            | NoSuchMethodException
                            | InstantiationException
                            | IllegalAccessException
                            | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                } else{
                    String path = "webapps" + File.separator + requestLine;
                    File file = new File(path);
                    if(file.exists()){
                        response.forward(file);
                        System.out.println("资源请求响应完毕");
                    }else{
                        response.forward(new File("webapps"+File.separator+"404_NOT_FOUND_PAGE.html"), Context.STATUS_404_NOT_FOUND);
                        System.out.println("对不起, 您所请求的资源不存在!!!");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                if(socket != null){
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println();
            }
        }
    }
}
