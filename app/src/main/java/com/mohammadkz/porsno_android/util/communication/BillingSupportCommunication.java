package com.mohammadkz.porsno_android.util.communication;


import com.mohammadkz.porsno_android.util.IabResult;

public interface BillingSupportCommunication {
    void onBillingSupportResult(int response);
    void remoteExceptionHappened(IabResult result);
}
