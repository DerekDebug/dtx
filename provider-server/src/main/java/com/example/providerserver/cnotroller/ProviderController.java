package com.example.providerserver.cnotroller;

import com.example.providerserver.dtx.txmanager_client.TxProcessor;
import com.example.providerserver.service.LocalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProviderController {

    @Autowired
    LocalService localService;

    @GetMapping("/provider/{age}/{xid}/{txcount}")
    public Integer invoke(@PathVariable("age") Integer age,@PathVariable("xid")String xid,@PathVariable("txcount") Integer txcount) {
        TxProcessor.setTxCount(txcount);
        TxProcessor.setXid(xid);
        localService.insertUser(age);
        return 0;
    }


}
