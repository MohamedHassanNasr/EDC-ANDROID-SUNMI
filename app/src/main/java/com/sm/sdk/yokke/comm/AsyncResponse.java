package com.sm.sdk.yokke.comm;

import com.sm.sdk.yokke.models.transData.TransactionResult;

public interface AsyncResponse {

    void processFinish(TransactionResult output);
}
