package com.wallet.local.server.error;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorType {
    public static final String UNKNOWN_CURRENCY = "Unknown Currency";
    public static final String INSUFFICIENT_FUNDS = "Insufficient Funds";
}
