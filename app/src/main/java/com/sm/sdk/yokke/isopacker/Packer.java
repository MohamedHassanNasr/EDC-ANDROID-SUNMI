package com.sm.sdk.yokke.isopacker;

import android.content.Context;

public class Packer {
    PackerImp packerImp;

    private static Packer instance = null;

    private Packer(Context context) {
        packerImp = new PackerImp(context);
    }

    public static Packer getInstance(Context context) {
        if (instance == null) {
            instance = new Packer(context);
        }
        return instance;
    }

    public IPacker getPacker() {
        return packerImp;
    }
}
