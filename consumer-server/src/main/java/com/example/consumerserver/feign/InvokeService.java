package com.example.consumerserver.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient("provider")
public interface InvokeService {

    /**
     * 之前有想过传txCount，最后觉得，还是约定txCount+isEnd判断严谨一些，所以本demo只传xid
     */
    @GetMapping("/provider/{age}/{xid}/{txcount}")
    void addMouse(@PathVariable("age") Integer age, @PathVariable("xid") String xid,@PathVariable("txcount")Integer txcount);
}
