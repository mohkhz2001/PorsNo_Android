package com.mohammadkz.porsno_android.Model.Response;

public class HistoryBuyResponse {
    private String buyedAccount, porsnoTrackId, amount, date;

    public String getBuyedAccount() {
        return buyedAccount;
    }

    public void setBuyedAccount(String buyedAccount) {
        this.buyedAccount = buyedAccount;
    }

    public String getPorsnoTrackId() {
        return porsnoTrackId;
    }

    public void setPorsnoTrackId(String porsnoTrackId) {
        this.porsnoTrackId = porsnoTrackId;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
