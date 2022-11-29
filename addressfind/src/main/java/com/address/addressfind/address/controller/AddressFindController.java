package com.address.addressfind.address.controller;

import org.json.simple.parser.ParseException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.address.addressfind.address.service.AddressFindService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(value = "/api/v1")
@RequiredArgsConstructor
public class AddressFindController {
    
    private final AddressFindService addressFindService;

    @GetMapping("/getAddress")
    public String getAddress(@RequestParam("address") String address) throws ParseException{
        return addressFindService.getAddress(address);
    }
}
