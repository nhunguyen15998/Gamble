package com.futech.entertainment.packages.settings.modelMappers;

import javax.validation.constraints.NotBlank;

public class SettingMapper {
    
    @NotBlank(message = "Amount is required")
    private String vndExRate;
    @NotBlank(message = "Amount is required")
    private String bitcoinExRate;

    public SettingMapper() {}
    
    public String getVndExRate() {
        return vndExRate;
    }
    public void setVndExRate(String vndExRate) {
        this.vndExRate = vndExRate;
    }
    
    public String getBitcoinExRate() {
        return bitcoinExRate;
    }
    public void setBitcoinExRate(String bitcoinExRate) {
        this.bitcoinExRate = bitcoinExRate;
    }

}
