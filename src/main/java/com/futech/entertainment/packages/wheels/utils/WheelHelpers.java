package com.futech.entertainment.packages.wheels.utils;

import com.google.gson.JsonArray;

public class WheelHelpers {
    public static String small[] = { "X0.5", "X0", "NEXT", "X0.2", "÷2", "+2", "X1.5", "X1" };
    public static String medium[] = { "÷3", "NEXT", "X3", "X0.6", "X1.5", "NEXT", "+2", "X0" };
    public static String large[] = { "X5", "÷5", "+3", "X0.5", "÷3", "+1", "+10", "X0", "÷2.5", "+0", "X1", "X2", "÷1", "+6", "X0.25", "X0" };
   
    public static Double countBettingResult(String str, Double betAmount){
        switch (str.charAt(0)) {
            case 'N':
                break;
            case 'X':
                var multiple = Double.parseDouble(str.replace("X", ""));
                betAmount = betAmount * multiple;
                break;
            case '÷':
                var divide = Double.parseDouble(str.replace("÷", ""));
                betAmount /= divide;
                break;
            case '+':
                var plus = Double.parseDouble(str.replace("+", ""));
                betAmount += plus;
                break;
        }
        return betAmount;
    }

    public static String[] toStringArray(JsonArray jsonArray){
        try {
            if(jsonArray.size() == 0 || jsonArray == null){
                return null;
            }
            String[] array = new String[jsonArray.size()];
            for (int i = 0; i < array.length; i++) {
                array[i] = jsonArray.get(i).toString().replace("\"", "");
            }
            return array;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
