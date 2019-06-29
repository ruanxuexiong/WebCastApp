package com.android.nana.util;

/**
 * Created by Administrator on 2017/3/26 0026.
 */

public class Constant {


    public static class Payment {

        public static class AliPay {

            /**
             * 应用ID
             */
            public static final String AppId = "2018120862470654";
            /**
             * 签约合作者身份ID
             */
            public static final String Partner = "2088331760980939";
            /**
             * 签约卖家支付宝账号
             */
            public static final String Seller = "18925227903@163.com";
            /**
             * 私钥
             */
            public static final String RsaPrivate ="MIIEowIBAAKCAQEA2ogqJe7DrMHHL97X9UzYQSCs10r7mVGm" +
                    "3nIwc6qLodFm9sm04g9q/lAVCen2+vD00Rgt1Izp99MK8EuhX6DAOpPHANM7gZJpu52vuKPpPTul1sU" +
                    "5hTrqwuVVFTL0UrsBBvetm4UXOY4w+PmlsMHiso1MrNs6zON09TaM+5noEqRWowTMk/fSAgl87ASAnb" +
                    "L5favO+s504nzBxK6lnw4/hAebOF8e96fscHLhUfvxf5wyQ9eb99Uj4f/HMB9hXdrpQOlw8x1xzANTC" +
                    "gIHck9Eydd5hW/6fnNlvqC9OiObwkIpZLeVy2aXsOFB2RAjRVTiVdRMvuzCjKYpZ9xyaCTwHwIDAQAB" +
                    "AoIBAQCJBT+IMUE3nPjLpyo5gElH7Ljy9Oz0oSHD8E04+QI8IBQzQR3NcGVht8y5Pp3Bkjuurz5tOz2" +
                    "oSgddy84PK7fcArSQa+GvyKHLJX/8ganI3xGIrHj4dIymTy0d8akIBoEKYVnVXYnGaMWEFuQxBCCkW7" +
                    "xr1bn0pdm7tsyHJWVB6ef3vzolRWLvx2X53K+zRyaInX/7uta15tQo7+8hVuoREW/uNkaLzozlp9cUv" +
                    "HvxKYzM+UYKE0owi4PHXXZLvY6W6tMFEV3nhxXc2XiQafMJrNWV2ol+OmcX6kB/47J+EHl4mKhZ1HMT" +
                    "1b9Ck1RgF3z2vwSYSACPB3gXDPTtLCnhAoGBAPkJ7qV3KwJ5dep7TMhoXCNmUpnJEkMsvDPirw9csSC" +
                    "rDDkfhTL3NW1WslPXOmULFVVkZQS6b8LDyTnbIkmQpJxPR4ubKjk6Jm30PpjxNJ8SLYTAJyX1w4tHqp" +
                    "yS6IAah4PRcubNZugBPmYT7RT+Fn4/Zil+oE4JG8+pfrvOwDm7AoGBAOCj7n5nMDBp/EYNuFUz4EgzL" +
                    "v13V9ov+UI0Ai2dDwagMW2DMdvXP4gFQ3njmI0xSMAsr4ztWH6GN58hsjlPpIfgQSukhqCamoAJO6Ks" +
                    "pebo4Y57xUiBYtfiLrez/RFtWUN1s0VxGQyCmeSdUvU81ACRwgWAXQvhz7uDRmN6j5rtAoGASVXua9V" +
                    "62eCwo5spkj6BG3+PRXAbq6JpzZGsYYa3NlqIX+3zSH3Zwtfr7ZqMvKKV5q6IvOq17JHBGQN5xlYM02" +
                    "bZN7g6p92C4ZAyOtGU4F72/oejP59utP4mtLLiOo8VZvDvuB22dw2etWqavzrPnt/GkuOSC0jNYsqT4" +
                    "nRQ0O8CgYBdUn0PshqI4l7VU3nSm5x/IhRRZDTxBaENacHF5UuKux4NmsUpyUAxZ85iYrTLY0AP5gCA" +
                    "EVIELgoJZ9w8huOZNwab9Nug+Nlae3t01ziMtw6NPMn2lxnTCbnSw0lM5hqBNfJl1NhNJCx+lwURsFV" +
                    "6bxMlafA9sW+1PJIPXaku+QKBgCB9ZdULd1v8vQwLSjOcwQCcQi192zKktv6SC2COraVFaVEcPuYOuJ" +
                    "Ng/R/MjYOsRvZq+xohFcNHPbNgacAPE+ZhgPx8YklTVJtUvX/hBjs1VWAoJbsZWXsGuqRlEGJ9e5AEF" +
                    "X2Y6lkeE6/uNLIYLx0ymbTiU3vC2FEXZskuf0nL";


            public static final String NotifyUrl = "http://www.nanapal.com/Api/AliPay/notifyAliPayIos";

        }

        public static class WxPay {

            public static final String APP_ID = "wx29a730e7a2a4ed88";//
            public static final String APP_SECERT = "1a71e34b78371e9e2e4a419734c6da56";

            public static final String API_KEY = "NaNa1231864214567890123456789123";
            public static final String MCH_ID = "1521330391";
            public static final String NotifyUrl = "http://www.nanapal.com/Api/Order/notifyPayResult";
        }

    }

    public static class IncomeStatistics {

        public static final String All = "";
        public static final String Income = "INCOME";
        public static final String Pay = "EXPENDITURE";

    }

    public static class Appointment {

        public static final int Other_me = 1;
        public static final int Other_me_suc = -1;
        public static final int Me_other = 1;
        public static final int Me_other_suc = -1;

    }

    public static class UpdatePassword {

        /**
         * 找回登录密码
         */
        public static final int UpdateLoginPass = 1;
        /**
         * 找回提现密码
         */
        public static final int UpdateWithdrawPass = 2;
        /**
         * 设置提现密码
         */
        public static final int SetUpWithdrawPass = 3;
        /**
         * 绑定用户手机号
         */
        public static final int BindPhoneNumber = 4;

    }

}
