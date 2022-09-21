package com.futech.entertainment.packages.wallets.controllers.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.futech.entertainment.packages.wallets.services.interfaces.UserWalletServiceInterface;

@RestController
@RequestMapping(path = "/api")
public class WalletsController {
    
    @Autowired
    private UserWalletServiceInterface userWalletServiceInterface;


}
