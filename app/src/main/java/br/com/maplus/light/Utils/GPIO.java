package br.com.maplus.light.Utils;


public enum  GPIO  {
    GPIO_V0 ("V0"),
    GPIO_V1("V1"),
    GPIO_V2 ("V2"),
    GPIO_V3 ("V3"),
    GPIO_V4 ("V4"),
    GPIO_V5 ("V5"),
    GPIO_V12( "V12"),
    GPIO_V13( "V13"),
    GPIO_V14( "V14"),
    GPIO_V15( "V15"),
    GPIO_V16( "V16");

    private String text;

    GPIO(String text) {
        this.text = text;
    }

    public String getText() {
        return this.text;
    }

    public static GPIO fromString(String text) {
        for (GPIO b : GPIO.values()) {
            if (b.text.equalsIgnoreCase(text)) {
                return b;
            }
        }
        return null;
    }
    public enum DIGITAL{
        LOW , HIGHT
    }

}
