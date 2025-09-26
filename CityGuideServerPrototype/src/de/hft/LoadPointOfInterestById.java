package de.hft;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

/**
 * Servlet implementation class LoadRouteIndex
 */
@WebServlet("/LoadPointOfInterestById")
public class LoadPointOfInterestById extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoadPointOfInterestById() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    	String id = request.getParameter("id");
    	
    	response.setContentType("text/html;charset=UTF-8");
    	PrintWriter out = response.getWriter();
    	
    	JSONObject jsonObject = new JSONObject();
        try {
        	if (id.isEmpty()) {
        		out.println("{}");
        	} else {
        		PointOfInterest pointOfInterest = PointOfInterest.loadPointOfInterestById(id);
				jsonObject.put("id", pointOfInterest.getId());
				jsonObject.put("name", pointOfInterest.getName());
				jsonObject.put("description", pointOfInterest.getDescription());
				jsonObject.put("longitude", pointOfInterest.getLongitude());
				jsonObject.put("latitude", pointOfInterest.getLatitude());
				
        		out.println(jsonObject.toString());
        	}
            out.flush();
        } finally {
            out.close();
        }
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}

}