package com.github.javiersantos.piracychecker.enums;

public class PirateApp {
    private String name;
    private String[] pack;

    public PirateApp(String name, String[] pack) {
        this.name = name;
        this.pack = pack;
    }

    public String getName() {
        return name;
    }

    @Deprecated
    public String[] getPack() {
        return pack;
    }

    public String getPackage() {
        StringBuilder sb = new StringBuilder();
        for (String s : pack) {
            sb.append(s);
        }
        return sb.toString();
    }

    public boolean isUnauthorized() {
        return (name.equalsIgnoreCase("Lucky Patcher") || name.equalsIgnoreCase("Freedom") ||
                name.equalsIgnoreCase("Uret Patcher") || name.equalsIgnoreCase("CreeHack"));
    }
}