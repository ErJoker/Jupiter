package rpc;

import java.io.IOException;
//import java.io.PrintWriter;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
//import org.json.JSONException;
import org.json.JSONObject;

import db.DBConnection;
import db.DBConnectionFactory;
import entity.Item;
//import external.TicketMasterAPI;

/**
 * Servlet implementation class SearchItem
 */
@WebServlet("/search")
public class SearchItem extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SearchItem() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		// allow access only if session exists
		HttpSession session = request.getSession(false);
		if (session == null) {
			response.setStatus(403);
			return;
		}

		// optional
		String userId = session.getAttribute("user_id").toString(); 
		
		double lat = Double.parseDouble(request.getParameter("lat"));
		double lon = Double.parseDouble(request.getParameter("lon"));
		String term = request.getParameter("term");
		//String userId = request.getParameter("user_id");
		DBConnection connection = DBConnectionFactory.getConnection();
		try {
			List<Item> items = connection.searchItems(lat, lon, term);
			Set<String> favoritedItemIds = connection.getFavoriteItemIds(userId);
//		TicketMasterAPI ticketmasterapi = new TicketMasterAPI();
//		List<Item> itemList = ticketmasterapi.search(lat, lon, term);
//		response.setContentType("application/json");
//		
		JSONArray array = new JSONArray();
		
		try {
		for(Item item: items) {
			JSONObject obj = item.toJSONObject();
			obj.put("favorite", favoritedItemIds.contains(item.getItemID()));
			array.put(obj);
		}
		}catch(Exception e){
			e.printStackTrace();
		}
//		PrintWriter out = response.getWriter();
		RpcHelper.writeJsonArray(response, array);}catch(Exception e){
			e.printStackTrace();
		}finally {
			connection.close();
		}
		
		//String username = request.getParameter("username");
		
		//if(username != null) {
			
			//JSONObject obj = new JSONObject();
//		JSONArray array = new JSONArray();
//			
//			try {
//				//obj.put("username",username);
//				array.put(new JSONObject().put("username","1234"));
//				array.put(new JSONObject().put("username","abcd"));
//			} catch (JSONException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			//}
//			
//			//out.println(obj);
//			
//			//out.println("<html><body>");
//			//out.println("<h1>Hello World" + username + "</h1>");
//			//out.println("</body></html>");
//			
//		}
//		
//		out.print(array);
//		out.close();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
