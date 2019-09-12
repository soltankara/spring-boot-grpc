package com.wallet.local.server;

import com.wallet.local.server.service.WalletService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ServerApplicationTests {

    WalletService service;

    @Test
    public void contextLoads() {
    }

}
