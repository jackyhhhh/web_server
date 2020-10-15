package servlets;

import core.DBUtils;
import http.HttpRequest;
import http.HttpResponse;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class RegServlet extends HttpServlet{
    @Override
    public void service(HttpRequest request, HttpResponse response) {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String password2 = request.getParameter("password2");
        if((!"".equals(username)) && password.equals(password2)){
            Connection conn = DBUtils.getConnection();
            try {
                Statement stat = conn.createStatement();
                stat.execute("insert into t_user(username,password) values(\""+username+"\",\""+password+"\");");
                response.forward(new File("webapps"+File.separator+"myweb"+File.separator+"reg_succ.html"));
                System.out.println("注册成功");
                System.out.println("业务请求执行成功!!!\n\n");
            } catch (SQLException sqlException) {
                sqlException.printStackTrace();
            }
        }else{
            response.forward(new File("webapps"+File.separator+"myweb"+File.separator+"reg_fail.html"));
            System.out.println("注册失败, 用户名或密码不合法");
            System.out.println("业务请求执行失败!!!\n\n");
        }
    }
}
