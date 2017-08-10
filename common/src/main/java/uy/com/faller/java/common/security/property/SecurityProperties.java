package uy.com.faller.java.common.security.property;


public enum SecurityProperties {

    OPTION_HANDLER_OUT("uy.com.faller.security.optionswss.out"),
    OPTION_HANDLER_IN("uy.com.faller.security.optionswss.in"),
    PATH_KEYSTORE("uy.com.faller.security.pathkeystore"),
    PATH_TRUSTORE("uy.com.faller.security.pathtrust"),
    PASSWORD_KEYSTORE("uy.com.faller.security.keystorepass"),
    PASSWORD_TRUSTORE("uy.com.faller.security.trustorepass"),
    ALIAS_PRIVATE_KEY("uy.com.faller.security.aliasprivatekey");

    private final String value;

    private SecurityProperties(String value) {
        this.value = value;
    }


    public String getValue() {
        return value;
    }

}
