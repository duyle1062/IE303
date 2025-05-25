package controller.admin;

import controller.BaseServlet;
import service.BillService;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@WebServlet("/api/admin/bill/updateTotal")
public class AdminBillUpdateController extends BaseServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        BillService billService = new BillService();
        Map<String, Object> response = billService.updateAllBillTotals();
        sendJsonResponse(resp, response);
    }
}