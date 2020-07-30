/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fgengine.Dispatcher;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Pinky
 */
public class DispatcherSerlvet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            String actionPerformed = request.getParameter("action").trim();
            switch (actionPerformed) {
                case "Address": {
                    getServletContext().getRequestDispatcher("/WAddressServlet").forward(request, response);
                    break;
                }
                case "Products": {
                    getServletContext().getRequestDispatcher("/WProductServlet").forward(request, response);
                    break;
                }
                case "CashOut": {
                    getServletContext().getRequestDispatcher("/WCashOutServlet").forward(request, response);
                    break;
                }
                case "Link": {
                    getServletContext().getRequestDispatcher("/LinksServlet").forward(request, response);
                    break;
                }
                case "Category": {
                    getServletContext().getRequestDispatcher("/WCategoryServlet").forward(request, response);
                    break;
                }
                case "Messages": {
                    getServletContext().getRequestDispatcher("/WMessageServlet").forward(request, response);
                    break;
                }
                case "Discount": {
                    getServletContext().getRequestDispatcher("/WDiscountCodeServlet").forward(request, response);
                    break;
                }
                case "Order": {
                    getServletContext().getRequestDispatcher("/WOrderServlet").forward(request, response);
                    break;
                }
                case "Payment": {
                    getServletContext().getRequestDispatcher("/WPaymentServlet").forward(request, response);
                    break;
                }
                case "Report": {
                    getServletContext().getRequestDispatcher("/WReportServlet").forward(request, response);
                    break;
                }
                case "Review": {
                    getServletContext().getRequestDispatcher("/WReviewServlet").forward(request, response);
                    break;
                }
                case "Shipping": {
                    getServletContext().getRequestDispatcher("/WShippingServlet").forward(request, response);
                    break;
                }
                case "Stock": {
                    getServletContext().getRequestDispatcher("/WStockServlet").forward(request, response);
                    break;
                }
                case "Pickup": {
                    getServletContext().getRequestDispatcher("/WPickupServlet").forward(request, response);
                    break;
                }
                case "Subscription": {
                    getServletContext().getRequestDispatcher("/WSubscriptionServlet").forward(request, response);
                    break;
                }
                case "Transaction": {
                    getServletContext().getRequestDispatcher("/WTransactionServlet").forward(request, response);
                    break;
                }
                case "User": {
                    getServletContext().getRequestDispatcher("/WUserServlet").forward(request, response);
                    break;
                }
                case "Wallet": {
                    getServletContext().getRequestDispatcher("/WWalletSerlvet").forward(request, response);
                    break;
                }
                case "Cart": {
                    getServletContext().getRequestDispatcher("/WCartServlet").forward(request, response);
                    break;
                }
                default: {
                    response.sendRedirect(request.getHeader("referer"));
                }
            }
        }
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
