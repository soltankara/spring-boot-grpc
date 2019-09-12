package com.wallet.local.server;

import com.wallet.local.lib.BalanceRequest;
import com.wallet.local.lib.Currency;
import com.wallet.local.lib.TransferRequest;
import com.wallet.local.lib.WalletServiceGrpc;
import com.wallet.local.server.configuration.H2TestProfileJPAConfig;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ScriptException;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.SQLException;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {
        ServerApplication.class,
        H2TestProfileJPAConfig.class})
@ActiveProfiles("test")
public class ServerApplicationTests {

    @Autowired
    private JdbcTemplate template;

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    private WalletServiceGrpc.WalletServiceBlockingStub walletServiceBlockingStub;

    private static final String CREATE_WALLET_TABLE_SCRIPT = "script/create-wallet-table-script.sql";
    private static final String DROP_WALLET_TABLE_SCRIPT = "script/drop-wallet-table-script.sql";


    @Before
    public void before() throws SQLException, ScriptException {
        ScriptUtils.executeSqlScript(template.getDataSource().getConnection(), new ClassPathResource(CREATE_WALLET_TABLE_SCRIPT));

        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9898).usePlaintext().build();
        walletServiceBlockingStub = WalletServiceGrpc.newBlockingStub(channel);
    }

    @After
    public void after() throws SQLException, ScriptException {
        ScriptUtils.executeSqlScript(template.getDataSource().getConnection(), new ClassPathResource(DROP_WALLET_TABLE_SCRIPT));
    }

    @Test
    public void shouldThrowStatusRuntimeException_WhenUserHasNoEnoughMoneyToWithdraw() {
        expectedEx.expect(StatusRuntimeException.class);
        expectedEx.expectMessage("Insufficient Funds");

        var userId = 1;
        var amount = 200.00f;
        var currency = "EUR";

        deposit(userId, amount, currency);
        var balanceMap = balance(userId);
        assertThat(amount, Matchers.is(balanceMap.get(currency)));

        amount += 100.00f;
        withdraw(userId, amount, currency);
    }

    @Test
    public void shouldIncreaseBalance_WhenUserDepositMoney() {
        var userId = 1;
        var amount = 100f;
        var currency = "USD";

        deposit(userId, amount, currency);
        var balanceMap = balance(userId);
        assertThat(amount, Matchers.is(balanceMap.get(currency)));
    }

    @Test
    public void shouldDecreaseBalance_WhenUserWithdrawMoney() {
        var userId = 1;
        var amount = 100f;
        var currency = "GBP";

        deposit(userId, amount, currency);
        var balanceMap = balance(userId);
        assertThat(amount, Matchers.is(balanceMap.get(currency)));

        withdraw(userId, amount, currency);
        balanceMap = balance(userId);
        assertThat(0f, Matchers.is(balanceMap.get(currency)));
    }


    private void deposit(long userId, float amount, String currency) {
        var request = TransferRequest.newBuilder()
                .setUserId(userId)
                .setAmount(amount)
                .setCurrency(Currency.valueOf(currency))
                .build();

        walletServiceBlockingStub.deposit(request);
    }

    private void withdraw(long userId, float amount, String currency) {
        var request = TransferRequest.newBuilder()
                .setUserId(userId)
                .setAmount(amount)
                .setCurrency(Currency.valueOf(currency))
                .build();

        walletServiceBlockingStub.withdraw(request);
    }

    private Map<String, Float> balance(long userId) {
        var request = BalanceRequest.newBuilder().setUserId(userId).build();
        var response = walletServiceBlockingStub.balance(request);
        return response.getBalanceMap();
    }
}
