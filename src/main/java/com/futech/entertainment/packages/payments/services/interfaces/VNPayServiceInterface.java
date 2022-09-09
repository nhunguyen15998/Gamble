package com.futech.entertainment.packages.payments.services.interfaces;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import com.google.gson.JsonObject;

public interface VNPayServiceInterface {
    public JsonObject doPost(HttpServletRequest req) throws ServletException, IOException;
    public JsonObject doReturn(HttpServletRequest req) throws ServletException, IOException;
}
