Feature: Operation of deposit, withdraw and balance from wallet for given user id

  Scenario: Deposit, withdraw and balance operations
    Given the following amount in user wallet
      | userId | amount | currency |
      | 1      | 100    | USD      |
    When make a withdraw "USD" 200 for user with id 1 and expect "Insufficient Funds" error message
    Then make a deposit "USD" 100 to user with id 1 and check that balances are correct
    Then make a withdraw "USD" 200 for user with id 1 and check that balances are correct


