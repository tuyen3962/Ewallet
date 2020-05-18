package com.example.ewalletexample.Symbol;

public enum  ErrorCode {
    PROCESSING(2,""),
    SUCCESS(1,""),
    EXCEPTION(0, "Ngoại"),
    // -1 -> -100 validate
    VALIDATE_FULL_NAME_INVALID(-1, "Họ tên không hợp lệ"),
    VALIDATE_PIN_INVALID(-2, "Mã pin không hợp lệ"),
    VALIDATE_PHONE_INVALID(-3, "Số điện thoại không hợp lệ"),
    VALIDATE_PHONE_DUPLICATE(-4, "Số điện thoại đã tồn tại"),
    VALIDATE_USER_ID_INVALID(-5, "user id không hợp lệ"),
    VALIDATE_AMOUNT_INVALID(-6, "Số tiền không hợp lệ"),
    VALIDATE_TRANSACTION_ID_INVALID(-7, "Mã giao dịch không hợp lệ"),
    VALIDATE_TRANSACTION_DUPLICATE(-8, "Mã giao dịch trùng lặp"),
    VALIDATE_BANK_CODE_INVALID(-9, "Mã ngân hàng không hợp lệ"),
    VALIDATE_CAR_NUMBER_INVALID(-10, "Mã thẻ ngân hàng không hợp lệ"),
    VALIDATE_CMND_INVALID(-11, "Chứng minh nhân dân không hợp lệ"),
    VALIDATE_F6CARD_NO_INVALID(-12, "Sáu số đầu thẻ ngân hàng không hợp lệ"),
    VALIDATE_L4CARD_NO_INVALID(-13, "Bốn số cuối thẻ ngân hàng không hợp lệ"),
    VALIDATE_ORDER_ID_INVALID(-14, "Mã đơn hàng không hợp lệ"),
    VALIDATE_SOURCE_OF_FUN_INVALID(-15, "Nguồn tiền không hợp lệ"),
    VALIDATE_SERVICE_TYPE_INVALID(-16, "Loại dịch vụ không hợp lệ"),

    VALIDATE_EMAIL_INVALID(-17, "Email không hợp lệ"),

    EMPTY_PIN(-201,"Mã pin trống"),
    EMPTY_CONFIRM_PIN(-202, "Nhập lại mã pin lần nữa"),
    UNVALID_PIN_AND_CONFIRM_PIN(-200,"Mật khẩu và xác nhận mật khẩu không trùng"),

    // -101 -> -200 BUS
    USER_PASSWORD_WRONG(-101, "Sai mật khẩu"),
    USER_PIN_WRONG(-102,"Mã pin sai"),
    BALANCE_NOT_ENOUGHT(-103, "Số tiền ví không đủ"),
    USER_HAS_NOT_MAPPING_YET(-104, "Người dùng chưa liên kết ngân hàng"),
    DUPLICATE_TRANSACTION(-105, "Trùng lặp giao dịch");

    private int value;
    private String message;
    ErrorCode(int value, String mess){
        this.value=value;
        this.message = mess;
    }

    public int GetValue() {
        return this.value;
    }

    public String GetMessage(){
        return this.message;
    }
}
