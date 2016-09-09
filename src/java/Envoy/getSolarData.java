package Envoy;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.*;

@WebServlet(name = "getSolarData", urlPatterns = {"/getSolarData"})
public class getSolarData extends HttpServlet {
    private final SimpleDateFormat sdf = new SimpleDateFormat("EEEE d MMMM HH:mm");
    
    private Calendar getFullDateTime(java.sql.Date date, java.sql.Time time) {
        Calendar timeCal = Calendar.getInstance();
        Calendar dateCal = Calendar.getInstance();
        
        timeCal.setTime(time);
        dateCal.setTime(date);
        dateCal.add(Calendar.HOUR_OF_DAY, timeCal.get(Calendar.HOUR_OF_DAY));
        dateCal.add(Calendar.MINUTE, timeCal.get(Calendar.MINUTE));
        dateCal.add(Calendar.SECOND, timeCal.get(Calendar.SECOND));
        
        return dateCal;
    }
    
    private String getDateTime(java.sql.Date date, java.sql.Time time) {
        Calendar dateCal = getFullDateTime(date, time);
        
        return "Date(" + 
                String.valueOf(dateCal.get(Calendar.YEAR)) + ", " +
                String.valueOf(dateCal.get(Calendar.MONTH)) + ", " +
                String.valueOf(dateCal.get(Calendar.DAY_OF_MONTH)) + ", " +
                String.valueOf(dateCal.get(Calendar.HOUR_OF_DAY)) + ", " +
                String.valueOf(dateCal.get(Calendar.MINUTE)) + ", " +
                String.valueOf(dateCal.get(Calendar.SECOND)) + ")";
    }

    private String getDateTime(java.sql.Date date) {
        Calendar dateCal = Calendar.getInstance();
        
        dateCal.setTime(date);
        
        return "Date(" + 
                String.valueOf(dateCal.get(Calendar.YEAR)) + ", " +
                String.valueOf(dateCal.get(Calendar.MONTH)) + ", " +
                String.valueOf(dateCal.get(Calendar.DAY_OF_MONTH)) + ", " +
                String.valueOf(dateCal.get(Calendar.HOUR_OF_DAY)) + ", " +
                String.valueOf(dateCal.get(Calendar.MINUTE)) + ", " +
                String.valueOf(dateCal.get(Calendar.SECOND)) + ")";
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String query = request.getQueryString();
        String mode = "day";
        String CurrentString = "Current";
        String TodayString = "Today";
        String InvertersString = "Inverters";
        String SQLQueryString = "SELECT * FROM Production WHERE Day >= (CURDATE() - INTERVAL 1 MONTH) ORDER BY Day, Time";
        
//        if (query != null) {
//            mode = request.getParameter("mode");
//            if (mode.equals("max")) {
//                CurrentString = "max(Current)";
//                TodayString = "max(Today)";
//                InvertersString = "max(Inverters)";
//                SQLQueryString = "select Day, Time, max(Current), max(Today), max(Inverters) from Production " +
//                        "WHERE Day >= (CURDATE() - INTERVAL 1 MONTH) group by Day ORDER BY Day, Time";
//            }
//        }
        
        String dbString = "jdbc:mysql://localhost:3306/Solar";
        JSONArray rows = new JSONArray();
        JSONObject dataTable = new JSONObject();
        JSONArray cols = new JSONArray();

        JSONObject col1 = new JSONObject();
        col1.put("id", "DateTime");
        col1.put("label", "DateTime");
        col1.put("type", "datetime");
        cols.add(col1);
        
        JSONObject col2 = new JSONObject();
        col2.put("id", "Current");
        col2.put("label", "Current");
        col2.put("type", "number");
        cols.add(col2);
        
        JSONObject col2a = new JSONObject();
        col2a.put("type", "string");
        col2a.put("role", "style");
        JSONObject col2ab = new JSONObject();
        col2ab.put("role", "style");
        col2a.put("p", col2ab);
        cols.add(col2a);
        
        JSONObject col3 = new JSONObject();
        col3.put("id", "Today");
        col3.put("label", "Today");
        col3.put("type", "number");
        cols.add(col3);
        
        JSONObject col4 = new JSONObject();
        col4.put("id", "Inverters");
        col4.put("label", "Inverters");
        col4.put("type", "number");
        cols.add(col4);
        dataTable.put("cols", cols);
        
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection(dbString, "colin", "Quackquack1");
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(SQLQueryString);  
            
            while (rs.next()) {
                java.sql.Date day = rs.getDate("Day");
                java.sql.Time time = rs.getTime("Time");
                double current = rs.getDouble(CurrentString);
                double today = rs.getDouble(TodayString);
                int inverters = rs.getInt(InvertersString);
                
                String currentStr;
                if (current > 1000) {
                    currentStr = String.format ("%.2f", current / 1000) + "kW";
                } else {
                    currentStr = String.format ("%.0f", current) + "W";
                }
                
                JSONArray entry = new JSONArray();
                JSONObject v1 = new JSONObject();
                
//                if (mode.equals("max")) {
//                    v1.put("v", getDateTime(day));
//                } else {
                    v1.put("v", getDateTime(day, time));
                    v1.put("f", sdf.format(getFullDateTime(day, time).getTime()));
//                }
                entry.add(v1);

                JSONObject v2 = new JSONObject();
                v2.put("v", current);
                v2.put("f", currentStr + " (" + Integer.toString(inverters) + ")");
                entry.add(v2);
                
                double opacityRange = 0.8;
                double op = ((inverters * opacityRange) / 14) + (1.0 - opacityRange);
                JSONObject v2a = new JSONObject();
                v2a.put("v", "opacity: " + Double.toString(op));
                entry.add(v2a);
                
                JSONObject v3 = new JSONObject();
                v3.put("v", today);
                v3.put("f", Double.toString(today) + "kWh");
                entry.add(v3);
                
                JSONObject v4 = new JSONObject();
                v4.put("v", inverters);
                entry.add(v4);
                
                JSONObject row = new JSONObject();
                row.put("c", entry);
                rows.add(row);
            }
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        dataTable.put("rows", rows);

        response.setContentType("application/json");
        response.getWriter().write(dataTable.toString());    
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
