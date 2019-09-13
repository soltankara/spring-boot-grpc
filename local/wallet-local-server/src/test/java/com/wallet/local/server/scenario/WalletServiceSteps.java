package com.wallet.local.server.scenario;

import com.wallet.local.lib.BalanceRequest;
import com.wallet.local.lib.Currency;
import com.wallet.local.lib.TransferRequest;
import com.wallet.local.lib.WalletServiceGrpc;
import com.wallet.local.server.ServerApplication;
import com.wallet.local.server.configuration.H2TestProfileJPAConfig;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import org.assertj.core.api.Assertions;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ScriptException;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.ActiveProfiles;

import java.sql.SQLException;
import java.util.Map;

@SpringBootTest(classes = {
        ServerApplication.class,
        H2TestProfileJPAConfig.class})
@ActiveProfiles("test")
public class WalletServiceSteps {

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

    @Given("^the following amount in user wallet$")
    public void theFollowingAmountInUserWallet(DataTable table) {
        var dataMaps = table.asMaps();
        for (Map<String, String> dataMap : dataMaps) {
            var userId = Integer.parseInt(dataMap.get("userId"));
            var amount = Float.parseFloat(dataMap.get("amount"));
            var currency = dataMap.get("currency");
            deposit(userId, amount, currency);
        }
    }

    @When("^make a withdraw \"([^\"]*)\" (\\d+) for user with id (\\d+) and expect \"([^\"]*)\" error message$")
    public void makeAWithdrawForUserWithIdAndExpectErrorMessage(String currency, float amount, long userId, String message) {
        Assertions.assertThatThrownBy(() -> withdraw(userId, amount, currency)).isInstanceOf(StatusRuntimeException.class).hasMessageContaining(message);
    }

    @Then("^make a deposit \"([^\"]*)\" (\\d+) to user with id (\\d+) and check that balances are correct$")
    public void makeADepositToUserWithIdAndCheckThatBalancesAreCorrect(String currency, float amount, long userId) {

        var balance = balance(userId).get(currency);
        deposit(userId, amount, currency);

        var newBalance = balance(userId).get(currency);
        Assertions.assertThat(newBalance).isEqualTo(balance + amount);
    }


    @Then("^make a withdraw \"([^\"]*)\" (\\d+) for user with id (\\d+) and check that balances are correct$")
    public void makeAWithdrawForUserWithIdAndCheckThatBalancesAreCorrect(String currency, float amount, long userId) {

        var balance = balance(userId).get(currency);
        withdraw(userId, amount, currency);

        var newBalance = balance(userId).get(currency);
        Assertions.assertThat(newBalance).isEqualTo(balance - amount);
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
