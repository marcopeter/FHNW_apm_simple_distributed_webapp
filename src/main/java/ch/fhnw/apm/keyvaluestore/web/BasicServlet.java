package ch.fhnw.apm.keyvaluestore.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;



@SuppressWarnings("serial")
public class BasicServlet extends HttpServlet {

	private final Logger logger = Logger.getLogger(this.getClass());
	private ClusterStorage localStorage = new ClusterStorage();

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/json; charset=utf-8");
		PrintWriter writer = response.getWriter();

		// curl  http://192.168.167.80:8080/simple_distributed_app/servlet/getHostname
		if (request.getRequestURI().contains("getHostname")) {
			String hostname = InetAddress.getLocalHost().getHostName();
			writer.append("{\"hostname\": \"" + hostname + "\"}");

		// curl  http://192.168.167.80:8080/simple_distributed_app/servlet/?key=42
		} else {
			String key = request.getParameter("key");
			String value = localStorage.load(Integer.parseInt(key));
			//writer.append(value);
			writer.append("{\"key\": \"" + value + "\"}");
		}
	}

	// curl -X PUT http://192.168.167.80:8080/simple_distributed_app/servlet/?key=42&value=Hello
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		localStorage.store(Integer.parseInt(request.getParameter("key")), request.getParameter("value"));

	}


	@Override
	public void init(ServletConfig config) throws ServletException {

	}



}
